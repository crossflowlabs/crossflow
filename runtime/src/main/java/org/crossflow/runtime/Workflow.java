package org.crossflow.runtime;

import com.beust.jcommander.Parameter;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.crossflow.runtime.serialization.Serializer;
import org.crossflow.runtime.utils.*;
import org.crossflow.runtime.utils.ControlSignal.ControlSignals;
import org.crossflow.runtime.utils.TaskStatus.TaskStatuses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.groupingBy;

public abstract class Workflow<E extends Enum<E>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Workflow.class);

    @Parameter(names = {"--help", "-h"}, description = "Help descriptions", help = true)
    private boolean help = false;

    /*
     * GENERAL
     */
    @Parameter(names = {"-name"}, description = "The name of the workflow")
    protected String name;

    @Parameter(names = {"-instance"}, description = "The instance of the master (to contribute to)")
    protected String instanceId;

    @Parameter(names = {
            "-mode"}, description = "Must be master_bare, master or worker", converter = ModeConverter.class)
    protected Mode mode = Mode.MASTER;

    @Parameter(names = {
            "-parallelization"}, description = "The parallelization of the workflow (for non-singleton tasks), defaults to 1")
    protected int parallelization = 1;// Runtime.getRuntime().availableProcessors();

    @Parameter(names = {
            "-disableTermination"}, description = "Flag to disable termination when queues are empty and no task is processing any element")
    protected boolean terminationEnabled = true;

    /*
     * CONNECTIONS
     */
    @Parameter(names = {"-master", "-brokerHost"}, description = "Host of the JMX Broker")
    protected String master = "localhost";

    @Parameter(names = {"-port"}, description = "Port of the JMX Broker")
    protected int port = 61616;

    @Parameter(names = {"-stomp"}, description = "Port to use for STOMP based messages")
    protected int stompPort = 61613;
    protected boolean enableStomp = true;

    @Parameter(names = {"-redisPort"}, description = "Port to use for Redis KV-Store")
    protected int redisPort = 6379;

    @Parameter(names = {"-redisHost"}, description = "Redis hostname")
    protected String redisHost = "localhost";
    protected boolean enableRedis = true;

    @Parameter(names = {"-ws"}, description = "Port to use for WS based messages")
    protected int wsPort = 61614;
    protected boolean enableWS = false;

    @Parameter(names = {"-activeMqConfig"}, description = "Location of ActiveMQ configuration file")
    protected String activeMqConfig;

    @Parameter(names = {"-createBroker"}, description = "Whether this workflow creates a broker or not.", arity = 1)
    protected boolean createBroker = false;

    protected BrokerService brokerService;

    /*
     * CACHING
     */
    @Parameter(names = {
            "-cacheEnabled"}, description = "Whether this workflow caches intermediary results or not, defaults to false.", arity = 1)
    protected boolean cacheEnabled = false;

    @Parameter(names = {
            "-deleteCache"}, description = "Before starting this workflow, delete the contents of the cache by queue name (use empty string to delete entire cache).")
    protected String deleteCache;

    protected Cache cache;

    /*
     * I/O
     */
    @Parameter(names = {
            "-inputDirectory"}, description = "The input directory of the workflow.", converter = DirectoryConverter.class)
    protected File inputDirectory = new File("in").getAbsoluteFile();

    @Parameter(names = {
            "-outputDirectory"}, description = "The output directory of the workflow.", converter = DirectoryConverter.class)
    protected File outputDirectory = new File("out").getParentFile();

    protected File runtimeModel = new File("").getParentFile();
    protected File tempDirectory = null;

    @Parameter(names = {
            "-disableStreamMetadataTopic"}, description = "Flag to disable data being sent to the Stream Metadata Topic")
    protected boolean enableStreamMetadataTopic = true;

    /*
     * TRANSPORT
     */
    protected BuiltinStream<LogMessage> logTopic = null;
    protected BuiltinStream<ControlSignal> controlTopic = null;
    protected BuiltinStream<TaskStatus> taskStatusTopic = null;

    /**
     * Optional broadcasting of the state of the various workflow streams, with
     * period: {@link streamMetadataPeriod}
     */
    protected BuiltinStream<StreamMetadataSnapshot> streamMetadataTopic = null;

    /**
     * Optional throttled version of {@link taskStatusTopic} that will only send
     * messages for effective status changes happening every
     * {@link taskChangePeriod} ms.
     */
    protected BuiltinStream<TaskStatus> taskMetadataTopic = null;
    protected boolean enableTaskMetadataTopic = true;

    protected BuiltinStream<FailedJob> failedJobsTopic = null;
    protected BuiltinStream<InternalException> internalExceptionsQueue = null;

    /* Bids Topics */
    protected BuiltinStream<BidOffer> bidOffersTopic;
    protected BuiltinStream<Bid> bidsQueue;
    private BuiltinStream<WinningBid> winningBids;

    private BuiltinStream<BiddingFinishedAck> ackQueue;

    /* Statistics topic */
    protected BuiltinStream<CrossflowMetrics> metrics;
    private List<CrossflowMetrics> allMetrics = new ArrayList<>();

    protected List<FailedJob> failedJobs = null;
    protected List<InternalException> internalExceptions = null;

    protected HashSet<Task> tasks = new HashSet<>();
    protected List<String> activeJobs = new ArrayList<>();
    protected HashSet<Stream> activeStreams = new HashSet<>();
    protected List<Thread> activeSources = new LinkedList<>();

    // for master to keep track of active and terminated workers
    protected Collection<String> activeWorkerIds = new HashSet<>();
    protected Collection<String> terminatedWorkerIds = new HashSet<>();
    protected Collection<ExecutorService> executorPools = new LinkedList<>();

    // Heartbeat utils
    private static final long HEARTBEAT_RATE = 2000L;
    private static final int HEARTBEAT_TIMEOUT_CYCLES = 3;
    private static final long TIMEOUT_MILLIS = HEARTBEAT_TIMEOUT_CYCLES * HEARTBEAT_RATE;
    private final Map<String, Long> lastHeartbeats = new HashMap<>();
    private Timer heartbeatCheckTimer;
    private final Timer heartbeater = new Timer();

    private Map<String, List<Bid>> bidMap = new HashMap<>();
    protected Map<String, Job> jobsWaiting = new HashMap<>();
    private ExecutorService bidExecutorService = Executors.newFixedThreadPool(500);

    protected int iteration = 0;
    protected String algorithm;
    protected String jobConf;
    protected String workerConf;
    protected Map<String, Double> estimatedBids = new HashMap<>();
    private Map<String, Boolean> acks = new HashMap<>();



    protected ExecutorService newExecutor() {
        ExecutorService executor = Executors.newFixedThreadPool(parallelization);
        executorPools.add(executor);
        return executor;
    }

    private KVSRepository repository;

    public KVSRepository getRepository() {
        return repository;
    }

    /**
     * Sets whether tasks are able to obtain more jobs while they are in the middle
     * of processing one already
     */
    protected boolean enablePrefetch = false;

    public void setEnableStomp(boolean enable) {
        enableStomp = enable;
    }

    public void setEnableWS(boolean enable) {
        enableWS = enable;
    }

    public void setActiveMqConfig(String activeMqConfig) {
        this.activeMqConfig = activeMqConfig;
    }

    public String getActiveMqConfig() {
        return activeMqConfig;
    }

    /**
     * Exclude the given task from this worker.
     * <p>
     * Returns the workflow instance for a fluent API.
     * </p>
     *
     * @param task the task to exclude
     * @return the workflow instance
     * @throws IllegalArgumentException if task is {@code null}
     */
    public abstract Workflow<E> excludeTask(E task);

    /**
     * Exclude the given tasks from this worker.
     * <p>
     * Returns the workflow instance for a fluent API.
     * </p>
     *
     * @param tasks the tasks to exclude
     * @return the workflow instance
     * @throws IllegalArgumentException if any value in tasks is {@code null}
     */
    public abstract Workflow<E> excludeTasks(Collection<E> tasks);

    public boolean isCreateBroker() {
        return createBroker;
    }

    public void createBroker(boolean createBroker) {
        this.createBroker = createBroker;
    }

    protected Timer terminationTimer;
    protected Timer streamMetadataTimer;
    protected boolean aboutToTerminate = false;
    protected boolean terminated = false;
    protected boolean terminating = false;

    /**
     * used to manually add local workers to master as they may be enabled too
     * quickly to be registered using the control topic when on the same machine
     */
    public void addActiveWorkerId(String id) {
        logger.log(LogLevel.INFO, "Adding worker " + id);
        activeWorkerIds.add(id);
    }

    // terminate workflow on master after this time (ms) regardless of confirmation
    // from workers
    private int terminationTimeout = 10000;
    // time in milliseconds between stream metadata updates
    private int streamMetadataPeriod = 200;

    private int taskChangePeriod = 1000;

    private long totalWorkTime;

    public void setTerminationTimeout(int timeout) {
        terminationTimeout = timeout;
    }

    public int getTerminationTimeout() {
        return terminationTimeout;
    }

    public Workflow() {
        taskStatusTopic = new BuiltinStream<>(this, "TaskStatusPublisher");
        streamMetadataTopic = new BuiltinStream<>(this, "StreamMetadataBroadcaster");
        taskMetadataTopic = new BuiltinStream<>(this, "TaskMetadataBroadcaster");
        controlTopic = new BuiltinStream<>(this, "ControlTopic");
        logTopic = new BuiltinStream<>(this, "LogTopic");
        failedJobsTopic = new BuiltinStream<>(this, "FailedJobs");
        internalExceptionsQueue = new BuiltinStream<>(this, "InternalExceptions", false);

        bidOffersTopic = new BuiltinStream<>(this, "BidOffersTopic");
        bidsQueue = new BuiltinStream<>(this, "BidsQueue");
        winningBids = new BuiltinStream<>(this, "WinningBids");
        ackQueue = new BuiltinStream<>(this, "BiddingFinishedAcks");
        metrics = new BuiltinStream<>(this, "Metrics");

        instanceId = UUID.randomUUID().toString();
    }

    private Timer taskStatusDelayedUpdateTimer;

    public abstract void sendConfigurations();

    protected void connect() throws Exception {
        if (tempDirectory == null) {
            tempDirectory = Files.createTempDirectory("crossflow").toFile();
        }
        taskStatusTopic.init();
        if (enableStreamMetadataTopic)
            streamMetadataTopic.init();
        if (enableTaskMetadataTopic)
            taskMetadataTopic.init();
        controlTopic.init();
        logTopic.init();
        failedJobsTopic.init();
        internalExceptionsQueue.init();
        bidsQueue.init();
        bidOffersTopic.init();
        winningBids.init();
        ackQueue.init();
        metrics.init();

        activeStreams.add(taskStatusTopic);
        activeStreams.add(failedJobsTopic);
        activeStreams.add(internalExceptionsQueue);
        // XXX do not add this topic/queue or any other non-essential ones to
        // activestreams as the workflow should be able to terminate regardless of their
        // state
        // activeStreams.add(controlTopic);
        // activeStreams.add(streamMetadataTopic);

        // All workflows subscribe to the control topic, the master to coordinate
        // termination and workers to terminate when appropriate
        controlTopic.addConsumer(signal -> {
            // System.err.println("consumeControlTopic on " + getName() + " : " +
            // signal.getSignal() + " : " + signal.getSenderId());


            if (isMaster()) {
                switch (signal.getSignal()) {
                    case ACKNOWLEDGEMENT:
                        terminatedWorkerIds.add(signal.getSenderId());
                        break;
                    case WORKER_ADDED:
                        // System.out.println("worker added: " + signal.getSenderId() + " : " +
                        // signal.getSignal());
                        activeWorkerIds.add(signal.getSenderId());
                        sendConfigurations();
                        break;
                    case WORKER_REMOVED:
                        activeWorkerIds.remove(signal.getSenderId());
                        break;
                    default:
                        break;
                }

            } else if (signal.getSignal().equals(ControlSignals.TERMINATION)) {
                try {
                    terminate();
                } catch (Exception e) {
                    unrecoverableException(e);
                }
            }

            if (signal.getSignal().equals(ControlSignals.CANCEL_JOB)) {
                System.out.println("Cancel Request received with payload: " + signal.getSenderId());
                cancelLocalJobs(signal.getSenderId());
            }

            if (signal.getSignal() == ControlSignals.HEARTBEAT) {
                handleHeartBeat(signal.getSenderId());
            }
        });

        heartbeatCheckTimer = new Timer();
        heartbeatCheckTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkHeartbeats();
            }
        }, 0, HEARTBEAT_RATE / 2);


        if (isWorker()) {
            heartbeater.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try {
                        controlTopic.send(new ControlSignal(ControlSignals.HEARTBEAT, getName()));
                    } catch (Exception e) {
                        // Print out the exception
                        e.printStackTrace();
                    }
                }
            }, 0, HEARTBEAT_RATE);
        }

        // XXX if the worker sends this before the master is listening to this topic
        // this information is lost which affects termination, for locally executed
        // workflows (whereby master and workers will start almost simultaneously),
        // workers can call the delayed start constructor to ensure the master
        // has been properly initialised
        if (!isMaster())
            controlTopic.send(new ControlSignal(ControlSignals.WORKER_ADDED, getName()));

        if (isWorker()) {
            bidOffersTopic.addConsumer(setupBidOfferConsumer());
        }

        if (isWorker()) {
            winningBids.addConsumer(setupWinningBidConsumer());
        }

        if (isMaster()) {
            metrics.addConsumer(metricsHandler());
            ackQueue.addConsumer(setupAckConsumer());
        }

        if (isMaster()) {
            taskStatusTopic.addConsumer(new BuiltinStreamConsumer<TaskStatus>() {

                private final Logger LOGGER = LoggerFactory.getLogger("TaskStatusTopic");

                @Override
                public void consume(TaskStatus status) {

                    //LOGGER.info(status.toString());

                    switch (status.getStatus()) {

                        case INPROGRESS: {
                            activeJobs.add(status.getCaller());
                            cancelTermination();
                            break;
                        }
                        case WAITING: {
                            activeJobs.remove(status.getCaller());
                            break;
                        }
                        default:
                            break;
                    }

                }

            });

            bidsQueue.addConsumer(setupBidConsumer());


            if (enableTaskMetadataTopic) {

                HashMap<String, String> displayedTaskStatuses = new HashMap<>();
                HashMap<String, Long> waitingTaskStatuses = new HashMap<>();
                HashSet<String> activeTimers = new HashSet<>();
                taskStatusDelayedUpdateTimer = new Timer();

                taskStatusTopic.addConsumer(new BuiltinStreamConsumer<TaskStatus>() {

                    @Override
                    public void consume(TaskStatus status) {

                        // sending to task metadata logic
                        try {
                            int colonIndex = status.getCaller().indexOf(":");
                            String taskName = status.getCaller().substring(0,
                                    colonIndex > 0 ? colonIndex : status.getCaller().length());

                            long time = System.currentTimeMillis();

                            // if the task is new (not displayed yet), send its initial status
                            if (!displayedTaskStatuses.containsKey(taskName)) {
                                taskMetadataTopic.send(status);
                                displayedTaskStatuses.put(taskName, status.getStatus() + ":" + time);
                                // System.out.println("updating task " + taskName + " from NEW to " +
                                // status.getStatus());
                                return;
                            }

                            // if a task was in the waiting list (delayed display change) but has a status
                            // not waiting arrive in the meantime, remove it from that list
                            if (!status.getStatus().equals(TaskStatuses.WAITING))
                                waitingTaskStatuses.remove(taskName);

                            String[] displayedSplit = displayedTaskStatuses.get(taskName).split(":");

                            // if the task is not currently displayed as inprogress or the new status is not
                            // waiting
                            if (!displayedSplit[0].equals(TaskStatuses.INPROGRESS.toString())
                                    || !status.getStatus().equals(TaskStatuses.WAITING)) {
                                // immediate visual update unless status is already finished
                                if (!displayedSplit[0].equals(status.getStatus().toString())
                                        && !displayedSplit[0].equals(TaskStatuses.FINISHED.toString())) {
                                    taskMetadataTopic.send(status);
                                    displayedTaskStatuses.put(taskName, status.getStatus() + ":" + time);
                                    // System.out.println("updating task " + taskName + " from " + displayedSplit[0]
                                    // + " to "
                                    // + status.getStatus());
                                }
                                // if the task is displayed as in progress and the new status is waiting --
                                // delay the visual update
                            } else {
                                // add the task to the delayed waiting list if it is not there (keep earliest
                                // time it was added to it)
                                if (!waitingTaskStatuses.containsKey(taskName))
                                    waitingTaskStatuses.put(taskName, time);

                                // create a delayed trigger (only once per task per cycle) for updating the ui
                                // to waiting if upon firing the
                                // task has not been updated since

                                if (!activeTimers.contains(taskName)) {
                                    activeTimers.add(taskName);
                                    taskStatusDelayedUpdateTimer.schedule(new TimerTask() {

                                        @Override
                                        public void run() {
                                            activeTimers.remove(taskName);
                                            //
                                            long delayedtime = System.currentTimeMillis();
                                            // String[] dSplit = displayedTaskStatuses.get(taskName).split(":");
                                            if (waitingTaskStatuses.containsKey(taskName) && (delayedtime
                                                    - waitingTaskStatuses.get(taskName) > taskChangePeriod)) {
                                                waitingTaskStatuses.remove(taskName);
                                                displayedTaskStatuses.put(taskName,
                                                        status.getStatus() + ":" + delayedtime);
                                                //
                                                // System.out.println("updating task " + taskName + " from " + dSplit[0]
                                                // + " to " + status.getStatus() + " (DELAYED)");
                                                try {
                                                    taskMetadataTopic.send(status);
                                                } catch (Exception e) {
                                                    System.err.println(
                                                            "Error in delayed task status metadata update timer task:");
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                // System.out.println("Delayed update did not update task:" + taskName);
                                                // System.out.println(waitingTaskStatuses.containsKey(taskName)
                                                // ? (delayedtime - waitingTaskStatuses.get(taskName))
                                                // : "n/a");
                                            }
                                        }
                                    }, (long) (taskChangePeriod * 1.1));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                });
            }
            failedJobs = new ArrayList<>();
            failedJobsTopic.addConsumer(new BuiltinStreamConsumer<FailedJob>() {

                @Override
                public void consume(FailedJob failedJob) {
                    System.err.println("Workflow received failed job");
                    System.out.println("Workflow: " + failedJob.getWorkflow());
                    System.out.println("Task: " + failedJob.getTask());
                    System.out.println("Job: " + failedJob.getJob());
                    System.out.println("Reason: " + failedJob.getReason());
                    System.out.println("Stacktrace: " + failedJob.getStacktrace());
                    failedJobs.add(failedJob);
                }
            });

            // If the strategy is set to all then log everything
            if (loggingStrategy == LoggingStrategy.ALL) {
                logTopic.addConsumer(new DefaultLogConsumer());
            }

            internalExceptions = new ArrayList<>();
            internalExceptionsQueue.addConsumer(new BuiltinStreamConsumer<InternalException>() {

                @Override
                public void consume(InternalException internalException) {
                    System.err.println(
                            "Workflow forwarding internal exception from " + internalException.getSenderId() + ":");
                    System.err.println(internalException.getReason());
                    System.err.println(internalException.getStacktrace());
                    internalExceptions.add(internalException);
                }
            });

            if (terminationEnabled) {

                // timer for managing automatic workflow termination
                terminationTimer = new Timer();

                terminationTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {

                        // System.err.println(activeJobs.size() + " " + areStreamsEmpty());

                        if (aboutToTerminate) {
                            if (canTerminate()) {
                                terminate();
                            }
                        } else {
                            aboutToTerminate = canTerminate();
                        }
                    }
                }, delay, 2000);

            }

            if (enableStreamMetadataTopic) {

                // timer for publishing stream metadata
                streamMetadataTimer = new Timer();

                streamMetadataTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        StreamMetadataSnapshot sm = new StreamMetadataSnapshot();
                        //
                        for (Stream c : new ArrayList<>(activeStreams)) {

                            try {

                                for (String destinationName : c.getDestinationNames()) {

                                    String destinationType = c.isBroadcast() ? "Topic" : "Queue";

                                    String url = "service:jmx:rmi:///jndi/rmi://" + master + ":1099/jmxrmi";
                                    JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(url));

                                    MBeanServerConnection connection = connector.getMBeanServerConnection();

                                    ObjectName destination = new ObjectName(
                                            "org.apache.activemq:type=Broker,brokerName=" + master + ",destinationType="
                                                    + destinationType + ",destinationName=" + destinationName);

                                    DestinationViewMBean mbView = MBeanServerInvocationHandler.newProxyInstance(
                                            connection, destination, DestinationViewMBean.class, true);

//									System.err.println(destinationName + ":" 
//											+ destinationType + " " 
//											+ mbView.getQueueSize() + " "
//											+ mbView.getInFlightCount());

                                    try {

                                        sm.addStream(destinationName, mbView.getQueueSize(), mbView.getInFlightCount(),
                                                c.isBroadcast(), mbView.getConsumerCount());

                                    } catch (Exception ex) {
                                        // Ignore exception
                                    }

                                    connector.close();

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //
                        try {
                            streamMetadataTopic.send(sm);
                        } catch (Exception e) {
                            // Ignore exception
                            // e.printStackTrace();
                        }

                    }
                }, 1000, streamMetadataPeriod);

            }

        }
    }

    protected BuiltinStreamConsumer<WinningBid> setupWinningBidConsumer() {
        return winningBid -> {
            if (winningBid.getWorkerId().equals(getName())) {
                estimatedBids.put(winningBid.getJobId(), winningBid.getCost());
            }
            if (!getName().equals(winningBid.getWorkerId())) {
                jobsWaiting.remove(winningBid.getJobId());
            }
            ackQueue.send(new BiddingFinishedAck(getName(), winningBid.getJobId()));
        };
    }

    protected BuiltinStreamConsumer<BidOffer> setupBidOfferConsumer() {
        return bidOffer -> {
            Job job = bidOffer.getJob();
            double jobProcessingTime = calculateProcessingTime(job);
            double cost = jobProcessingTime + queuedJobsTime();
            Bid bid = new Bid(job, cost, getName(), jobProcessingTime);
            bidsQueue.send(bid);
            jobsWaiting.put(job.jobId, job);
        };
    }

    protected double queuedJobsTime() {
        return 0;
    }

    protected double calculateProcessingTime(Job job) {
        return 0;
    }

    public void stopQueues() {
        try {
            winningBids.session.close();
            bidsQueue.session.close();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }


    protected double calculateJobCost(Job job) {
        return Double.MAX_VALUE;
    }

    protected BuiltinStreamConsumer<Bid> setupBidConsumer() {
        System.out.println("bid consumer setup for " + getName());
        return bid -> {
            System.out.println(bid);
            String jobId = bid.getJob().jobId;
            if (!bidMap.containsKey(jobId)) {
                return;
            }

            if (biddingFinished(jobId)) {
                return;
            }

            List<Bid> bids = bidMap.get(jobId);
            bids.add(bid);
        };
    }

    protected BuiltinStreamConsumer<BiddingFinishedAck> setupAckConsumer() {
        return ack -> acknowledgeWorker(ack.getWorkerId());
    }

    public void sendWinningBid(Bid bid) {
        try {
            reduceTimeForLostBids(bid);
            winningBids.send(new WinningBid(bid.getJob().jobId, bid.getWorkerId(), bid.getJobProcessingTime()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void reduceTimeForLostBids(Bid bid) {

        Map<String, Bid> workerCosts = getLosingWorkersForBid(bid);

        bidMap.forEach((jobId, otherBids) -> {
            if (jobId.equals(bid.getJob().jobId)) {
                return;
            }
            for (Bid otherBid : otherBids) {
                Bid losingBid = workerCosts.get(otherBid.getWorkerId());
                if (losingBid != null && losingBid.getBidTime().isBefore(otherBid.getBidTime())) {
                    double costGiven = workerCosts.get(otherBid.getWorkerId()).getCost();
                    otherBid.decreaseCost(costGiven);
                }
            }
        });
    }

    private Map<String, Bid> getLosingWorkersForBid(Bid bid) {
        List<Bid> bids = bidMap.get(bid.getJob().jobId);
        Map<String, Bid> workerCosts = new HashMap<>();

        bids.stream()
                .filter(b -> !b.getWorkerId().equals(bid.getWorkerId()))
                .forEach(b -> workerCosts.put(b.getWorkerId(), b));
        return workerCosts;
    }

    private boolean biddingFinished(String jobId) {
        List<Bid> bids = bidMap.get(jobId);
        return bids.size() > 0 && bids.size() >= activeWorkerIds.size();
    }

    /**
     * PROPOSAL:
     * Notify callback to the master for the new worker connected.
     * Jobs can be assigned to this sender id
     *
     * @param senderId of the worker added as worker
     */
    protected void connectQueues(String senderId) {
    }

    /**
     * PROPOSAL:
     * Finds the destination queue to accept the job.
     * If some worker has already completed the job partially,
     * or has some stored results, then its worker id is returned
     * Else, job is to be put to the general queue.
     * Worker has to be active (heartbeat)
     *
     * @param job job about to be done
     * @return workerId to send the job to if there's a preferred one,
     * or null if it's about to be sent to general queue
     */
    public String getPreferredWorkerIdFor(Job job) {
//        List<String> workerIds = repository.getWorkerIds(job.getJobHash());
//        Set<String> rejectedWorkerIds = job.getRejectedByWorkerIds();
//        for (String workerId : workerIds) {
//            if (workerIsAlive(workerId) && workerNotTooBusy(workerId) && !rejectedWorkerIds.contains(workerId)) {
//                return workerId;
//            }
//        }
        return null;
    }

    public void rejectJobByWorkerId(Job job, String workerId) {
//        repository.rejectJobByWorkerId(job.getJobHash(), workerId);
    }

    private boolean workerIsAlive(String workerId) {
        return lastHeartbeats.containsKey(workerId);
    }

    /**
     * PROPOSAL:
     * Abstract method to calculate load of the worker.
     * If the worker is working on too many jobs, jobs should be rebalanced elsewhere.
     * TODO: Calculate the cost function of the jobs,
     * TODO: and have some threshold percentage when worker is under too much stress compared to other workers' load
     *
     * @param workerId id of the worker
     * @return false if the worker is under too much load.
     */
    private boolean workerNotTooBusy(String workerId) {
        return true;
//		Map<String, Double> workerLoads = new HashMap<>();
//		for (String worker : lastHeartbeats.keySet()) {
//			workerLoads.put(worker, workerLoad(workerId));
//		}
//
//		if (lastHeartbeats.size() == 1) {
//			return true;
//		}
//
//		Map.Entry<String, Double> max =
//				Collections.max(workerLoads.entrySet(), Map.Entry.comparingByValue());
//
//		return !max.getKey().equals(workerId);
    }

    private double workerLoad(String workerId) {
        return 0;
//        return repository.getJobsByWorker(workerId)
//                .stream()
//                .mapToDouble(JobInfo::getCost)
//                .sum();
    }

    private void checkHeartbeats() {
        Iterator<String> iterator = lastHeartbeats.keySet().iterator();
        while (iterator.hasNext()) {
            String senderId = iterator.next();
            //System.out.printf("[Check] %s\n", senderId);
            if (Instant.now().toEpochMilli() - lastHeartbeats.get(senderId) > TIMEOUT_MILLIS) {
                iterator.remove();
                onWorkerTimeout(senderId);
            }
        }
    }

    private void onWorkerTimeout(String clientId) {
        // PROPOSAL:
        // Clear the dedicated queue for the jobs already assigned
        // Rebalance jobs to other workers, or send to general queue
        //System.out.printf("[Leave] %s", clientId);
    }

    private void onWorkerConnect(String senderId) {
        connectQueues(senderId);
    }

    private void handleHeartBeat(String senderId) {
        if (!lastHeartbeats.containsKey(senderId)) {
            onWorkerConnect(senderId);
        }
        lastHeartbeats.put(senderId, Instant.now().toEpochMilli());
    }

    private boolean canTerminate() {
        boolean streamsEmpty = areStreamsEmpty();
        //System.out.println(activeJobs + "::" + streamsEmpty);
        return activeJobs.size() == 0 && streamsEmpty;
    }

    public void cancelTermination() {
        aboutToTerminate = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        //
        cacheEnabled = cache != null;
        //
        this.cache = cache;
        //
        if (cache != null)
            cache.setWorkflow(this);
        //
    }

    public boolean isMaster() {
        return mode.isMaster();
    }

    public boolean isWorker() {
        return mode.isWorker();
    }

    public Mode getMode() {
        return mode;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getMaster() {
        return master;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getBroker() {
        // adds a more lenient delay for heavily loaded servers (60 instead of 10 sec)
        return "tcp://" + master + ":" + port + "?wireFormat.maxInactivityDurationInitalDelay=60000";
    }

    // TODO: Fix this to allow dynamic port
    public String getStompBroker() {
        return "stomp://" + master + ":" + stompPort;
    }

    // TODO: Fix this to allow dynamic port
    public String getWSBroker() {
        return "ws://" + master + ":" + wsPort;
    }

    public void stopBroker() throws Exception {
        brokerService.deleteAllMessages();
        // brokerService.stopAllConnectors(new ServiceStopper());
        brokerService.stopGracefully("", "", 1000, 1000);
        //System.out.println("terminated broker (" + getName() + ")");
        // logger.log(SEVERITY.INFO, "terminated broker (" + getName() + ")");
    }

    public void run() throws Exception {
        setupLogger();
        if (cacheEnabled && cache == null) {
            logger.log(LogLevel.INFO,
                    "cacheEnabled==true but no cache was defined, creating a default DirectoryCache in the temp folder of the machine.");
            DirectoryCache c = new DirectoryCache();
            setCache(c);
        }
        if (deleteCache != null)
            cache.clear(deleteCache);
        //
        run(0);
    }

    protected long delay = 0;

    /**
     * delays the execution of sources for 'delay' milliseconds. Needs to set the
     * delay field in the superclass.
     *
     * @param delay
     * @throws Exception
     */
    public abstract void run(long delay) throws Exception;

    long same = 0;
    int repeats = 0;
    boolean forceWithInFlight = false;

    private synchronized boolean areStreamsEmpty() {

        try {
            for (Stream c : new ArrayList<>(activeStreams)) {
                for (String destinationName : c.getDestinationNames()) {

                    String destinationType = c.isBroadcast() ? "Topic" : "Queue";

                    String url = "service:jmx:rmi:///jndi/rmi://" + master + ":1099/jmxrmi";
                    JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(url));
                    MBeanServerConnection connection = connector.getMBeanServerConnection();

                    ObjectName destination = new ObjectName("org.apache.activemq:type=Broker,brokerName=" + master
                            + ",destinationType=" + destinationType + ",destinationName=" + destinationName);

                    DestinationViewMBean mbView = MBeanServerInvocationHandler.newProxyInstance(connection, destination,
                            DestinationViewMBean.class, true);

                    try {

                        if (mbView.getQueueSize() > 0 || mbView.getInFlightCount() > 0) {
                            //System.err.println(getName() + " : " + getInstanceId());
                            //System.err.println(destinationName + ":" + destinationType + " " + mbView.getQueueSize() + " " + mbView.getInFlightCount());
                        }

                        if (!c.isBroadcast()) {
                            if (mbView.getQueueSize() > 0) {
                                return false;
                            }
                        } else {
                            if (mbView.getInFlightCount() > 1) {
                                if (forceWithInFlight) {
                                    if (same == mbView.getInFlightCount()) {
                                        System.out.println(
                                                "same inflight, maybe no consumers, adding 1 to repeat count (["
                                                        + repeats + "] break at 5)");
                                        repeats++;
                                    } else {
                                        repeats = 0;
                                        same = mbView.getInFlightCount();
                                    }
                                    if (repeats < 5)
                                        return false;
                                    else if (repeats > 5)
                                        repeats = 0;
                                } else
                                    return false;
                            }
                        }

                    } catch (Exception ex) {
                        // ex.printStackTrace();
                        // Ignore exception
                    }

                    connector.close();

                }
            }
        } catch (Exception ex) {
            unrecoverableException(ex);
            return true;
        }

        return true;

    }

    public void addWorkTime(long workTime) {
        totalWorkTime += workTime;
    }

    public synchronized void terminate() {

        if (terminated)
            return;

        terminating = true;

        if (terminationTimer != null)
            terminationTimer.cancel();

        heartbeater.cancel();
        heartbeatCheckTimer.cancel();

        // stop all sources (forced termination)

        for (Thread t : activeSources)
            t.stop();

        // close all tasks

        for (Task t : tasks)
            t.close();

        // send task termination to metadata
        if (isMaster() && enableTaskMetadataTopic)
            try {
                for (Task t : tasks)
                    taskMetadataTopic.send(new TaskStatus(TaskStatuses.FINISHED, t.getId(), ""));
            } catch (Exception e) {
                e.printStackTrace();
            }

        try {
            // master graceful termination logic
            if (isMaster()) {

                // ask all workers to terminate
                logger.log(LogLevel.INFO, "Asking all workers to terminate.");
                controlTopic.send(new ControlSignal(ControlSignals.TERMINATION, getName()));

                long startTime = System.currentTimeMillis();
                // wait for workers to terminate or for the termination timeout
                while ((System.currentTimeMillis() - startTime) < terminationTimeout) {
                    // System.out.println(terminatedWorkerIds);
                    // System.out.println(activeWorkerIds);
                    if (terminatedWorkerIds.equals(activeWorkerIds)) {
                        logger.log(LogLevel.INFO, "All workers terminated, terminating master.");
                        //System.out.println("all workers terminated, terminating master...");
                        break;
                    }
                    Thread.sleep(100);
                }
                //System.out.println("terminating master...");
                logger.log(LogLevel.INFO, "Terminating master...");

            }

            // termination logic
            //System.out.println("terminating workflow... (" + getName() + ")");

            if (isMaster()) {
                if (streamMetadataTimer != null)
                    streamMetadataTimer.cancel();
            }

            if (enableStreamMetadataTopic)
                try {
                    streamMetadataTopic.stop();
                } catch (Exception e) {
                    // Ignore any exception
                    e.printStackTrace();
                }

            if (isMaster()) {

                try {
                    controlTopic.stop();
                } catch (Exception e) {
                    // Ignore any exception
                    e.printStackTrace();
                }
                activeStreams.remove(controlTopic);

            } else {
                controlTopic.send(new ControlSignal(ControlSignals.ACKNOWLEDGEMENT, getName()));

                try {
                    controlTopic.stop();
                } catch (Exception e) {
                    // Ignore any exception
                    e.printStackTrace();
                }
                activeStreams.remove(controlTopic);
            }

            // stop all permanent streams
            try {
                logTopic.stop();
            } catch (Exception e) {
                // Ignore any exception
                e.printStackTrace();
            }

            activeStreams.remove(logTopic);

            //
            // stop all remaining stream connections
            for (Stream stream : activeStreams) {
                try {
                    stream.stop();
                } catch (Exception e) {
                    // Ignore any exception
                    e.printStackTrace();
                }
            }

            try {
                taskStatusTopic.stop();
            } catch (Exception e) {
                // Ignore any exception
                e.printStackTrace();
            }

            if (taskStatusDelayedUpdateTimer != null)
                taskStatusDelayedUpdateTimer.cancel();

            try {
                failedJobsTopic.stop();
            } catch (Exception e) {
                // Ignore any exception
                e.printStackTrace();
            }
            try {
                internalExceptionsQueue.stop();
            } catch (Exception e) {
                // Ignore any exception
                e.printStackTrace();
            }
            if (enableTaskMetadataTopic)
                try {
                    taskMetadataTopic.stop();
                } catch (Exception e) {
                    // Ignore any exception
                    e.printStackTrace();
                }

            // destroy all thread pools used by tasks
            for (ExecutorService executor : executorPools) {
                List<Runnable> pending = executor.shutdownNow();
                if (pending.size() > 0)
                    System.err.println("WARNING: there were pending tasks in the threadpool upon termination!");
            }

            // Destroy the Timeout Manager if there is one
            if (timeoutManager != null) {
                timeoutManager.shutdownNow();
            }

            if (isMaster()) {
                //
                //System.out.println("createBroker: " + createBroker);
                if (createBroker) {
                    stopBroker();
                }
            }

            terminated = true;
            notifyAll();

            //System.out.println("workflow " + getName() + " terminated.");
            //
        } catch (Exception ex) {
            // There is nothing to do at this stage -- print error for debugging purposes
            // only
            ex.printStackTrace();
        }
    }

    public boolean isTerminating() {
        return terminating;
    }

    public boolean hasTerminated() {
        return terminated;
    }

    public BuiltinStream<TaskStatus> getTaskStatusTopic() {
        return taskStatusTopic;
    }

    public BuiltinStream<TaskStatus> getTaskMetadataTopic() {
        return taskMetadataTopic;
    }

    public BuiltinStream<StreamMetadataSnapshot> getStreamMetadataTopic() {
        return streamMetadataTopic;
    }

    public BuiltinStream<ControlSignal> getControlTopic() {
        return controlTopic;
    }

    public BuiltinStream<FailedJob> getFailedJobsTopic() {
        return failedJobsTopic;
    }

    public BuiltinStream<LogMessage> getLogTopic() {
        return logTopic;
    }

    public List<FailedJob> getFailedJobs() {
        return failedJobs;
    }

    public List<InternalException> getInternalExceptions() {
        return internalExceptions;
    }

    long lastException = 0;
    Class<?> lastexceptionType = this.getClass();

    public void reportInternalException(Exception ex) {

        LOGGER.error("Internal Error occurred", ex);

        try {
            internalExceptionsQueue.send(new InternalException(ex, this.getName()));
        } catch (Throwable e) {
            Long currentTime = System.currentTimeMillis();
            if (currentTime - lastException > 1000 || !ex.getClass().equals(lastexceptionType)) {
                lastException = currentTime;
                lastexceptionType = ex.getClass();
                unrecoverableException(new Exception(e));
            } else if (lastException == 0)
                System.err.println(
                        ex.getMessage() + ", suppressing similar reportInternalException errors for 1 second.");
        }
    }

    public void unrecoverableException(Exception ex) {
        ex.printStackTrace();
    }

    long lastExceptionHidden = 0;

    public void sendTaskStatusUpdate(TaskStatus t) throws Exception {
        try {
            taskStatusTopic.send(t);
        } catch (Exception e) {
            long currentTime = System.currentTimeMillis();
            if (e.getMessage() != null && e.getMessage().equals("The Session is closed")) {
                if (currentTime - lastExceptionHidden > 1000)
                    System.err.println("Cannot send setTaskInProgess(" + t + ") message after workflow termination.");
                lastExceptionHidden = currentTime;
            } else
                throw e;
        }
    }

    public void setTaskInProgess(Task caller) throws Exception {
        sendTaskStatusUpdate(new TaskStatus(TaskStatuses.INPROGRESS, caller.getId(), ""));
    }

    public void setTaskWaiting(Task caller) throws Exception {
        sendTaskStatusUpdate(new TaskStatus(TaskStatuses.WAITING, caller.getId(), ""));
    }

    public void setTaskBlocked(Task caller, String reason) throws Exception {
        sendTaskStatusUpdate(new TaskStatus(TaskStatuses.BLOCKED, caller.getId(), reason));
    }

    public void setTaskUnblocked(Task caller) throws Exception {
        sendTaskStatusUpdate(new TaskStatus(TaskStatuses.INPROGRESS, caller.getId(), ""));
    }

    public void setTaskFinished(Task caller) throws Exception {
        sendTaskStatusUpdate(new TaskStatus(TaskStatuses.FINISHED, caller.getId(), ""));
    }

    public File getInputDirectory() {
        return inputDirectory;
    }

    public void setInputDirectory(File inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setRuntimeModel(File runtimeModel) {
        this.runtimeModel = runtimeModel;
    }

    public File getRuntimeModel() {
        return runtimeModel;
    }

    public File getTempDirectory() {
        return tempDirectory;
    }

    public void setTempDirectory(File tempDirectory) {
        this.tempDirectory = tempDirectory;
    }

    /**
     * default = 200 ms
     *
     * @param p
     */
    public void setStreamMetadataPeriod(int p) {
        streamMetadataPeriod = p;
    }

    public int getStreamMetadataPeriod() {
        return streamMetadataPeriod;
    }

    public void enableStreamMetadataTopic(boolean enable) {
        enableStreamMetadataTopic = enable;
    }

    public boolean isStreamMetadataTopicEnabled() {
        return enableStreamMetadataTopic;
    }

    public void enableTaskMetadataTopic(boolean enable) {
        enableTaskMetadataTopic = enable;
    }

    public boolean isTaskMetadataTopicEnabled() {
        return enableTaskMetadataTopic;
    }

    /**
     * @return A set containing all ActiveMQDestination objects used by all active
     * JobStreams
     */
    public Set<ActiveMQDestination> getAllJobStreamsInternals() {
        Set<ActiveMQDestination> ret = new HashSet<>();
        activeStreams.stream().filter(s -> s instanceof JobStream)
                .forEach(js -> ret.addAll(((JobStream<?>) js).getAllQueues()));
        return ret;
    }

    /**
     * @return Whether queues used by this workflow will keep providing messages to
     * consumers before they are finished with their current task (default =
     * false)
     */
    public boolean isEnablePrefetch() {
        return enablePrefetch;
    }

    /**
     * @param enablePrefetch Sets whether queues used by this workflow will keep
     *                       providing messages to consumers before they are
     *                       finished with their current task (default = false)
     */
    public void setEnablePrefetch(boolean enablePrefetch) {
        this.enablePrefetch = enablePrefetch;
    }

    /**
     * @return The maximum number of parallel instances for each non source/sink
     * task running in this workflow
     */
    public int getParallelization() {
        return parallelization;
    }

    /**
     * For master workflows this flag determines whether automatic workflow
     * termination will be triggered when there are no more messages in any queue
     * and no tasks are in progress
     *
     * @return Whether this workflow will terminate automatically
     */
    public boolean isTerminationEnabled() {
        return terminationEnabled;
    }

    /**
     * For master workflows this flag determines whether automatic workflow
     * termination will be triggered when there are no more messages in any queue
     * and no tasks are in progress
     *
     * @param enableTermination Sets whether this workflow will automatically
     *                          terminate (default = true)
     */
    public void setEnableTermination(boolean enableTermination) {
        terminationEnabled = enableTermination;
    }

    private long timeoutMillis = Long.MAX_VALUE;

    public long getTimeoutMillis() {
        return timeoutMillis;
    }

    /**
     * Set a hard limit for workflow execution to avoid hanging
     *
     * @param timeout_millis
     */
    public void setTimeoutMillis(long timeout_millis) {
        this.timeoutMillis = timeout_millis;
    }

    /**
     * Waits until {@link #hasTerminated()} return true.
     *
     * @throws TimeoutException
     */
    public synchronized void awaitTermination() throws TimeoutException {
        long startTime = System.currentTimeMillis();
        long latestTime = System.currentTimeMillis();
        while (!terminated && (latestTime = System.currentTimeMillis()) - startTime <= timeoutMillis) {
            try {
                wait(timeoutMillis);
            } catch (InterruptedException ie) {
                logger.log(LogLevel.INFO, ie.getMessage());
            }
        }
        if (latestTime - startTime > timeoutMillis)
            throw new TimeoutException(
                    "Workflow took longer than " + timeoutMillis + ", so released the wait() to avoid hanging");
    }

    /*
     * TASK TIMEOUTS
     */
    protected ScheduledExecutorService timeoutManager; // Lazily initialised

    /**
     * Get the {@code ExecutorService} responsible for managing task timeouts.
     *
     * @return the timeout manager
     */
    // TODO: Does this need to be synchronized?
    public ScheduledExecutorService getTimeoutManager() {
        if (timeoutManager == null) {
            timeoutManager = Executors.newScheduledThreadPool(1); // TODO: Do we need this to be parallelisation *
            // tasks?
        }
        return timeoutManager;
    }

    /*
     * SERIALIZATION
     */
    protected Serializer serializer;

    public Serializer getSerializer() {
        if (serializer == null) {
            serializer = createSerializer();
            registerDefaultSerializationTypes(serializer);
            registerCustomSerializationTypes(getSerializer());
        }
        return serializer;
    }

    /**
     * Construct a new Serializer for this workflow
     * <p>
     * Implementation will be auto-genned base on the serialiser defined in the
     * workflow model
     *
     * @return a new instance of Serializer
     */
    protected abstract Serializer createSerializer();

    /**
     * Register all workflow specific types
     * <p>
     * Implementation will be auto-genned
     *
     * @param serializer the serializer to register types
     */
    protected abstract void registerCustomSerializationTypes(Serializer serializer);

    protected void registerDefaultSerializationTypes(Serializer serializer) {
        checkNotNull(serializer);
        // o.e.s.c.runtime.*
        serializer.registerType(FailedJob.class);
        serializer.registerType(InternalException.class);
        serializer.registerType(Job.class);
        serializer.registerType(LoggingStrategy.class);
        serializer.registerType(Mode.class);

        // o.e.s.c.runtime.utils.*
        serializer.registerType(ControlSignal.class);
        serializer.registerType(ControlSignals.class);
        serializer.registerType(LogLevel.class);
        serializer.registerType(LogMessage.class);
        serializer.registerType(StreamMetadata.class);
        serializer.registerType(StreamMetadataSnapshot.class);
        serializer.registerType(TaskStatus.class);
        serializer.registerType(TaskStatuses.class);

        serializer.registerType(Bid.class);
        serializer.registerType(BidOffer.class);
        serializer.registerType(WinningBid.class);
        serializer.registerType(BiddingFinishedAck.class);

        serializer.registerType(CrossflowMetrics.class);
    }

    /*
     * LOGGING
     */
    @Parameter(names = {
            "-logging"}, description = "The logging strategy of this workflow. Can be one of ALL, SELF or NONE. By default MASTER -> ALL, WORKER -> SELF")
    protected LoggingStrategy loggingStrategy;
    protected CrossflowLogger logger = new CrossflowLogger(this);

    protected void setupLogger() {
        if (loggingStrategy == null) {
            loggingStrategy = isMaster() ? LoggingStrategy.ALL : LoggingStrategy.SELF;
        }
        logger = new CrossflowLogger(this);
        logger.setPrePrint(loggingStrategy == LoggingStrategy.SELF);
    }

    public CrossflowLogger getLogger() {
        return logger;
    }

    public void log(LogLevel level, String message) {
        logger.log(level, message);
    }

    /**
     * When running in a shell check if the help flag is set
     *
     * @return if the help flag has been set
     */
    public boolean isHelp() {
        return help;
    }

    /**
     * Internal method used by the current Workflow instance to cancel its own tasks
     * (called by the control topic)
     *
     * @param id
     * @return
     */
    protected abstract boolean cancelLocalJobs(String id);

    /**
     * Instructs all instances of this Workflow to cancel any currently executing
     * tasks with this input jobId (using the control topic)
     *
     * @param id
     */
    public void cancelAllJobs(String id) {
        try {
            controlTopic.send(new ControlSignal(ControlSignals.CANCEL_JOB, id));
        } catch (Exception e) {
            System.err.println("error in sending job cancellation signal:");
            e.printStackTrace();
        }
    }

    public Map<String, Job> getJobsWaiting() {
        return jobsWaiting;
    }

    public void issueJob(Job job, String destinationId) {
//        repository.issueJob(job, getName(), destinationId);
    }

    public void finishJob(Job job) {
//        repository.finishJob(job);
    }

    public void assignJob(Job job, String workerId) {
//        repository.setWorkerId(job.getJobHash(), workerId);
    }

    public Collection<String> getActiveWorkerIds() {
        return activeWorkerIds;
    }

    public Future<Bid> publishForBidding(Job job) {
        ensureWorkersReady();
        resetAcks(false);
        return bidExecutorService.submit(() -> {
            BidOffer bidOffer = new BidOffer(job);
            bidMap.put(job.jobId, new ArrayList<>());
            bidOffersTopic.send(bidOffer);

            while (!biddingFinished(job.jobId)) {
                Thread.sleep(100);
            }
            List<Bid> bids = bidMap.get(job.jobId);

            bids.sort(Bid::compareTo);
            Bid winningBid = bids.get(0);
            System.out.println("About to send winning bid: " + winningBid);
            sendWinningBid(winningBid);
            bidMap.remove(job.jobId);
            return winningBid;
        });

    }

    private void acknowledgeWorker(String workerId) {
        acks.put(workerId, true);
    }

    private void ensureWorkersReady() {
        while (!acksReceived()) {
            sleepMilis(100);
        }
    }

    private boolean acksReceived() {
        return acks
                .values()
                .stream()
                .noneMatch(ack -> ack.equals(false));
    }

    private void resetAcks(boolean value) {
        for (String key : acks.keySet()) {
            acks.put(key, value);
        }
    }

    private static void sleepMilis(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMetric(CrossflowMetrics metric) {
        try {
            metrics.send(metric);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BuiltinStreamConsumer<CrossflowMetrics> metricsHandler() {
        return metric -> allMetrics.add(metric);
    }

    public void saveMetricsCsv() {
        try {
            String fileName = String.format("metrics-i%s-%s-%s-%s.csv", iteration, algorithm, jobConf, workerConf);
            String csv = allMetrics.stream()
                    .map(CrossflowMetrics::toCsv)
                    .map(this::enrichMetrics)
                    .collect(Collectors.joining("\n"));
            Files.write(Paths.get(fileName), List.of(CrossflowMetrics.CSV_HEADER, csv));
            System.out.println("Saved " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String enrichMetrics(String s) {
        return s + "," + jobConf + "," + workerConf + "," + iteration + "," + algorithm;
    }

    public void destroyAllQueues() throws Exception {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616/");

        ActiveMQConnection connection = (ActiveMQConnection) connectionFactory.createConnection();
        DestinationSource ds = connection.getDestinationSource();

        connection.start();

        Set<ActiveMQQueue> queues = ds.getQueues();
        for (ActiveMQQueue queue : queues) {
            connection.destroyDestination(queue);
        }

        for (ActiveMQTopic topic : ds.getTopics()) {
            connection.destroyDestination(topic);
        }
    }

    public void setIteration(int i) {
        this.iteration = i;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public void setJobConf(String jobConf) {
        this.jobConf = jobConf;
    }

    public void setWorkerConf(String workerConf) {
        this.workerConf = workerConf;
    }

    public void deleteCachedRepositories() {
        File cacheFolder = new File("experiment/techrank/in/");
        for (File file : cacheFolder.listFiles()) {
            if (file.getName().endsWith("downloaded_repositories.txt")) {
                file.delete();
                System.out.println("Deleted " + file.getName());
            }
        }
    }

    public boolean workerConnected() {
        return lastHeartbeats.keySet().stream()
                .anyMatch(workerId -> !workerId.equals(getName()));
    }
}
