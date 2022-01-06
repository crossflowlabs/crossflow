/** This class was automatically generated and should not be modified */
package org.crossflow.tests.churnRateRepo;

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

@Generated(value = "org.crossflow.java.Workflow2Class", date = "2022-01-05T17:43:29.814671Z")
public class ChurnRateWorkflow extends Workflow<ChurnRateWorkflowTasks> {
	
	// Streams
	protected URLs uRLs;
	protected ChurnRates churnRates;
	
	// Tasks
	protected remoteURLSource remoteURLSource;
	protected ResultsSink resultsSink;
	protected ParallelTaskList<ChurnRateCalculator> churnRateCalculators = new ParallelTaskList<>();

	protected Set<ChurnRateWorkflowTasks> tasksToExclude = EnumSet.noneOf(ChurnRateWorkflowTasks.class);

	public ChurnRateWorkflow() {
		this(Mode.MASTER, 1);
	}
	
	public ChurnRateWorkflow(Mode m) {
		this(m, 1);
	}
	
	public ChurnRateWorkflow(Mode mode, int parallelization) {
		super();
			
		this.parallelization = parallelization;	
			
		this.name = "ChurnRateWorkflow";
		this.mode = mode;
		
		if (isMaster()) {
			remoteURLSource = new remoteURLSource();
			remoteURLSource.setWorkflow(this);
			tasks.add(remoteURLSource);
			resultsSink = new ResultsSink();
			resultsSink.setWorkflow(this);
			tasks.add(resultsSink);
		}
		
		if (isWorker()) {
			if (!tasksToExclude.contains(ChurnRateWorkflowTasks.CHURN_RATE_CALCULATOR)) {
				for(int i=1;i<=parallelization;i++){
					ChurnRateCalculator task = new ChurnRateCalculator();
					task.setWorkflow(this);
					tasks.add(task);
					churnRateCalculators.add(task);
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
		
			churnRateCalculators.init(this);
	
			uRLs = new URLs(ChurnRateWorkflow.this, enablePrefetch);
			activeStreams.add(uRLs);
			churnRates = new ChurnRates(ChurnRateWorkflow.this, enablePrefetch);
			activeStreams.add(churnRates);
		
			if (isMaster()) {
					remoteURLSource.setURLs(uRLs);
					churnRates.addConsumer(resultsSink, "ResultsSink");			
			}

			connect();
			
			if (isWorker()) {
				if (!tasksToExclude.contains(ChurnRateWorkflowTasks.CHURN_RATE_CALCULATOR)) {
						for(int i = 0; i <churnRateCalculators.size(); i++){
							ChurnRateCalculator task = churnRateCalculators.get(i);
							uRLs.addConsumer(task, "ChurnRateCalculator");			
							task.setChurnRates(churnRates);
						}
				}
			}
			
			if (isMaster()){
				// run all sources in parallel threads
				Thread remoteURLSourceThread = new Thread(() -> {
					try {
						setTaskInProgess(remoteURLSource);
						remoteURLSource.produce();
						setTaskWaiting(remoteURLSource);
					} catch (Exception ex) {
						reportInternalException(ex);
						terminate();
					}finally {
						activeSources.remove(Thread.currentThread());
					}
				});
				activeSources.add(remoteURLSourceThread);
				remoteURLSourceThread.start();	
				sendConfigurations();
			}
					
		} catch (Exception e) {
			log(LogLevel.ERROR, e.getMessage());
		}
	}				
	
	public void sendConfigurations(){
	}
	
	public URLs getURLs() {
		return uRLs;
	}
	public ChurnRates getChurnRates() {
		return churnRates;
	}
	
	public remoteURLSource getremoteURLSource() {
		return remoteURLSource;
	}
	public ChurnRateCalculator getChurnRateCalculator() {
		if(churnRateCalculators.size()>0)
			return churnRateCalculators.get(0);
		else 
			return null;
	}
	public ParallelTaskList<ChurnRateCalculator> getChurnRateCalculators() {
		return churnRateCalculators;	
	}	
	public ResultsSink getResultsSink() {
		return resultsSink;
	}
	
	public ChurnRateWorkflow createWorker() {
		ChurnRateWorkflow worker = new ChurnRateWorkflow(Mode.WORKER,parallelization);
		worker.setInstanceId(instanceId);
		return worker;
	}
	
	@Override
	public ChurnRateWorkflow excludeTask(ChurnRateWorkflowTasks task) {
		if (task == null) throw new IllegalArgumentException("task cannot be null");
		this.tasksToExclude.add(task);
		return this;
	}
	
	@Override
	public ChurnRateWorkflow excludeTasks(Collection<ChurnRateWorkflowTasks> tasks) {
		for (ChurnRateWorkflowTasks t : tasks) {
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
		serializer.registerType(URL.class);
		serializer.registerType(ChurnRate.class);
	}
	
	public static ChurnRateWorkflow run(String[] args) throws Exception {
		// Parse all values into an temporary object
		ChurnRateWorkflow argsHolder = new ChurnRateWorkflow();
		JCommander.newBuilder().addObject(argsHolder).build().parse(args);
		
		// Extract values to construct new object
		ChurnRateWorkflow app = new ChurnRateWorkflow(argsHolder.getMode(), argsHolder.getParallelization());
		JCommander.newBuilder().addObject(app).build().parse(args);
		app.run();
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		// Parse all values into an temporary object
		ChurnRateWorkflow argsHolder = new ChurnRateWorkflow();
		JCommander jct = JCommander.newBuilder().addObject(argsHolder).build();
		
		try {
			jct.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(argsHolder.isHelp()) {
			jct.setProgramName("ChurnRateWorkflow");
			jct.usage();
			System.exit(0);
			}
			
		run(args);
	}
	
	protected boolean cancelLocalJobs(String id) {
		boolean ret = false;
		for(ChurnRateCalculator v : churnRateCalculators){
			ret = v.cancelJob(id) || ret;
		}
		return ret;				
	}
	
}	
