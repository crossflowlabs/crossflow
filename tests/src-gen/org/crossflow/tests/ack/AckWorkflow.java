/** This class was automatically generated and should not be modified */
package org.crossflow.tests.ack;

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

@Generated(value = "org.crossflow.java.Workflow2Class", date = "2021-10-26T15:08:21.360808Z")
public class AckWorkflow extends Workflow<AckWorkflowTasks> {
	
	// Streams
	protected Numbers numbers;
	protected Results results;
	
	// Tasks
	protected Source source;
	protected Sink sink;
	protected ParallelTaskList<ProcessingTask> processingTasks = new ParallelTaskList<>();

	protected Set<AckWorkflowTasks> tasksToExclude = EnumSet.noneOf(AckWorkflowTasks.class);

	public AckWorkflow() {
		this(Mode.MASTER, 1);
	}
	
	public AckWorkflow(Mode m) {
		this(m, 1);
	}
	
	public AckWorkflow(Mode mode, int parallelization) {
		super();
			
		this.parallelization = parallelization;	
			
		this.name = "AckWorkflow";
		this.mode = mode;
		
		if (isMaster()) {
			source = new Source();
			source.setWorkflow(this);
			tasks.add(source);
			sink = new Sink();
			sink.setWorkflow(this);
			tasks.add(sink);
		}
		
		if (isWorker()) {
			if (!tasksToExclude.contains(AckWorkflowTasks.PROCESSING_TASK)) {
				for(int i=1;i<=parallelization;i++){
					ProcessingTask task = new ProcessingTask();
					task.setWorkflow(this);
					tasks.add(task);
					processingTasks.add(task);
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
		
			processingTasks.init(this);
	
			numbers = new Numbers(AckWorkflow.this, enablePrefetch);
			activeStreams.add(numbers);
			results = new Results(AckWorkflow.this, enablePrefetch);
			activeStreams.add(results);
		
			if (isMaster()) {
					source.setNumbers(numbers);
					results.addConsumer(sink, "Sink");			
			}

			connect();
			
			if (isWorker()) {
				if (!tasksToExclude.contains(AckWorkflowTasks.PROCESSING_TASK)) {
						for(int i = 0; i <processingTasks.size(); i++){
							ProcessingTask task = processingTasks.get(i);
							numbers.addConsumer(task, "ProcessingTask");			
							task.setResults(results);
						}
				}
			}
			
			if (isMaster()){
				// run all sources in parallel threads
				Thread sourceThread = new Thread(() -> {
					try {
						setTaskInProgess(source);
						source.produce();
						setTaskWaiting(source);
					} catch (Exception ex) {
						reportInternalException(ex);
						terminate();
					}finally {
						activeSources.remove(Thread.currentThread());
					}
				});
				activeSources.add(sourceThread);
				sourceThread.start();	
				sendConfigurations();
			}
					
		} catch (Exception e) {
			log(LogLevel.ERROR, e.getMessage());
		}
	}				
	
	public void sendConfigurations(){
	}
	
	public Numbers getNumbers() {
		return numbers;
	}
	public Results getResults() {
		return results;
	}
	
	public Source getSource() {
		return source;
	}
	public ProcessingTask getProcessingTask() {
		if(processingTasks.size()>0)
			return processingTasks.get(0);
		else 
			return null;
	}
	public ParallelTaskList<ProcessingTask> getProcessingTasks() {
		return processingTasks;	
	}	
	public Sink getSink() {
		return sink;
	}
	
	public AckWorkflow createWorker() {
		AckWorkflow worker = new AckWorkflow(Mode.WORKER,parallelization);
		worker.setInstanceId(instanceId);
		return worker;
	}
	
	@Override
	public AckWorkflow excludeTask(AckWorkflowTasks task) {
		if (task == null) throw new IllegalArgumentException("task cannot be null");
		this.tasksToExclude.add(task);
		return this;
	}
	
	@Override
	public AckWorkflow excludeTasks(Collection<AckWorkflowTasks> tasks) {
		for (AckWorkflowTasks t : tasks) {
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
		serializer.registerType(IntElement.class);
		serializer.registerType(StringElement.class);
	}
	
	public static AckWorkflow run(String[] args) throws Exception {
		// Parse all values into an temporary object
		AckWorkflow argsHolder = new AckWorkflow();
		JCommander.newBuilder().addObject(argsHolder).build().parse(args);
		
		// Extract values to construct new object
		AckWorkflow app = new AckWorkflow(argsHolder.getMode(), argsHolder.getParallelization());
		JCommander.newBuilder().addObject(app).build().parse(args);
		app.run();
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		// Parse all values into an temporary object
		AckWorkflow argsHolder = new AckWorkflow();
		JCommander jct = JCommander.newBuilder().addObject(argsHolder).build();
		
		try {
			jct.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(argsHolder.isHelp()) {
			jct.setProgramName("AckWorkflow");
			jct.usage();
			System.exit(0);
			}
			
		run(args);
	}
	
	protected boolean cancelLocalJobs(String id) {
		boolean ret = false;
		for(ProcessingTask v : processingTasks){
			ret = v.cancelJob(id) || ret;
		}
		return ret;				
	}
	
}	
