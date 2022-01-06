/** This class was automatically generated and should not be modified */
package org.crossflow.tests.concurrency;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Generated;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.Mode;
import org.crossflow.runtime.ParallelTaskList;
import org.crossflow.runtime.Workflow;
import org.crossflow.runtime.serialization.Serializer;
import org.crossflow.runtime.serialization.JsonSerializer;import org.crossflow.runtime.utils.ControlSignal;
import org.crossflow.runtime.utils.ControlSignal.ControlSignals;
import org.crossflow.runtime.utils.LogLevel;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

@Generated(value = "org.crossflow.java.Workflow2Class", date = "2022-01-05T17:43:30.516005Z")
public class ConcurrencyWorkflow extends Workflow<ConcurrencyWorkflowTasks> {
	
	// Streams
	protected SleepTimes sleepTimes;
	protected BuiltinStream<Result> results;
	
	// Tasks
	protected SleepTimeSource sleepTimeSource;
	protected ParallelTaskList<Sleeper> sleepers = new ParallelTaskList<>();

	protected Set<ConcurrencyWorkflowTasks> tasksToExclude = EnumSet.noneOf(ConcurrencyWorkflowTasks.class);

	public ConcurrencyWorkflow() {
		this(Mode.MASTER, 1);
	}
	
	public ConcurrencyWorkflow(Mode m) {
		this(m, 1);
	}
	
	public ConcurrencyWorkflow(Mode mode, int parallelization) {
		super();
			
		this.parallelization = parallelization;	
			
		this.name = "ConcurrencyWorkflow";
		this.mode = mode;
		
		if (isMaster()) {
			sleepTimeSource = new SleepTimeSource();
			sleepTimeSource.setWorkflow(this);
			tasks.add(sleepTimeSource);
		}
		
		if (isWorker()) {
			if (!tasksToExclude.contains(ConcurrencyWorkflowTasks.SLEEPER)) {
				for(int i=1;i<=parallelization;i++){
					Sleeper task = new Sleeper();
					task.setWorkflow(this);
					tasks.add(task);
					sleepers.add(task);
				}
			}
		}
	}
	
	/**
	 * Run with initial delay in ms before starting execution (after creating broker
	 * if master)
	 * 
	 * @param delay
	 */
	@Override
	public void run(long delay) throws Exception {
		this.getSerializer();
		this.delay=delay;
	
		try {
						
			if (isMaster()) {
				if (createBroker) {
					if (activeMqConfig != null && activeMqConfig != "") {
						brokerService = BrokerFactory.createBroker(new URI("xbean:" + activeMqConfig));
					} else {
						brokerService = new BrokerService();
					}
				
					//activeMqConfig
					brokerService.setUseJmx(true);
					brokerService.addConnector(getBroker());
					if(enableStomp)
						brokerService.addConnector(getStompBroker());
					if(enableWS)	
						brokerService.addConnector(getWSBroker());
					brokerService.start();
				}
			}
	
			Thread.sleep(delay);
		
			sleepers.init(this);
	
			sleepTimes = new SleepTimes(ConcurrencyWorkflow.this, enablePrefetch);
			activeStreams.add(sleepTimes);
			results = new BuiltinStream<Result>(this, "Results", false);
			results.init();
			activeStreams.add(results);
		
			if (isMaster()) {
					sleepTimeSource.setSleepTimes(sleepTimes);
			}

			connect();
			
			if (isWorker()) {
				if (!tasksToExclude.contains(ConcurrencyWorkflowTasks.SLEEPER)) {
						for(int i = 0; i <sleepers.size(); i++){
							Sleeper task = sleepers.get(i);
							sleepTimes.addConsumer(task, "Sleeper");			
							task.setResults(results);
						}
				}
			}
			
			if (isMaster()){
				// run all sources in parallel threads
				Thread sleepTimeSourceThread = new Thread(() -> {
					try {
						setTaskInProgess(sleepTimeSource);
						sleepTimeSource.produce();
						setTaskWaiting(sleepTimeSource);
					} catch (Exception ex) {
						reportInternalException(ex);
						terminate();
					}finally {
						activeSources.remove(Thread.currentThread());
					}
				});
				activeSources.add(sleepTimeSourceThread);
				sleepTimeSourceThread.start();	
				sendConfigurations();
			}
					
		} catch (Exception e) {
			log(LogLevel.ERROR, e.getMessage());
		}
	}				
	
	public void sendConfigurations(){
	}
	
	public SleepTimes getSleepTimes() {
		return sleepTimes;
	}
	public BuiltinStream<Result> getResults() {
		return results;
	}
	
	public SleepTimeSource getSleepTimeSource() {
		return sleepTimeSource;
	}
	public Sleeper getSleeper() {
		if(sleepers.size()>0)
			return sleepers.get(0);
		else 
			return null;
	}
	public ParallelTaskList<Sleeper> getSleepers() {
		return sleepers;	
	}	
	
	public ConcurrencyWorkflow createWorker() {
		ConcurrencyWorkflow worker = new ConcurrencyWorkflow(Mode.WORKER,parallelization);
		worker.setInstanceId(instanceId);
		return worker;
	}
	
	@Override
	public ConcurrencyWorkflow excludeTask(ConcurrencyWorkflowTasks task) {
		if (task == null) throw new IllegalArgumentException("task cannot be null");
		this.tasksToExclude.add(task);
		return this;
	}
	
	@Override
	public ConcurrencyWorkflow excludeTasks(Collection<ConcurrencyWorkflowTasks> tasks) {
		for (ConcurrencyWorkflowTasks t : tasks) {
			excludeTask(t);
		}
		return this;
	}
	
	@Override
	protected Serializer createSerializer() {
		return new JsonSerializer();
	}
	
	@Override
	protected void registerCustomSerializationTypes(Serializer serializer) {
		checkNotNull(serializer);
		serializer.registerType(SleepTime.class);
		serializer.registerType(Result.class);
	}
	
	public static ConcurrencyWorkflow run(String[] args) throws Exception {
		// Parse all values into an temporary object
		ConcurrencyWorkflow argsHolder = new ConcurrencyWorkflow();
		JCommander.newBuilder().addObject(argsHolder).build().parse(args);
		
		// Extract values to construct new object
		ConcurrencyWorkflow app = new ConcurrencyWorkflow(argsHolder.getMode(), argsHolder.getParallelization());
		JCommander.newBuilder().addObject(app).build().parse(args);
		app.run();
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		// Parse all values into an temporary object
		ConcurrencyWorkflow argsHolder = new ConcurrencyWorkflow();
		JCommander jct = JCommander.newBuilder().addObject(argsHolder).build();
		
		try {
			jct.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(argsHolder.isHelp()) {
			jct.setProgramName("ConcurrencyWorkflow");
			jct.usage();
			System.exit(0);
			}
			
		run(args);
	}
	
	protected boolean cancelLocalJobs(String id) {
		boolean ret = false;
		for(Sleeper v : sleepers){
			ret = v.cancelJob(id) || ret;
		}
		return ret;				
	}
	
}	
