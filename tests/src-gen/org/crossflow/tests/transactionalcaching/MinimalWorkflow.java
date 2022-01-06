/** This class was automatically generated and should not be modified */
package org.crossflow.tests.transactionalcaching;

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

@Generated(value = "org.crossflow.java.Workflow2Class", date = "2022-01-05T17:43:35.045518Z")
public class MinimalWorkflow extends Workflow<MinimalWorkflowTasks> {
	
	// Streams
	protected Input input;
	protected Output output;
	
	// Tasks
	protected MinimalSource minimalSource;
	protected MinimalSink minimalSink;
	protected ParallelTaskList<ClonerTask> clonerTasks = new ParallelTaskList<>();

	protected Set<MinimalWorkflowTasks> tasksToExclude = EnumSet.noneOf(MinimalWorkflowTasks.class);

	public MinimalWorkflow() {
		this(Mode.MASTER, 1);
	}
	
	public MinimalWorkflow(Mode m) {
		this(m, 1);
	}
	
	public MinimalWorkflow(Mode mode, int parallelization) {
		super();
			
		this.parallelization = parallelization;	
			
		this.name = "MinimalWorkflow";
		this.mode = mode;
		
		if (isMaster()) {
			minimalSource = new MinimalSource();
			minimalSource.setWorkflow(this);
			tasks.add(minimalSource);
			minimalSink = new MinimalSink();
			minimalSink.setWorkflow(this);
			tasks.add(minimalSink);
		}
		
		if (isWorker()) {
			if (!tasksToExclude.contains(MinimalWorkflowTasks.CLONER_TASK)) {
				for(int i=1;i<=parallelization;i++){
					ClonerTask task = new ClonerTask();
					task.setWorkflow(this);
					tasks.add(task);
					clonerTasks.add(task);
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
		
			clonerTasks.init(this);
	
			input = new Input(MinimalWorkflow.this, enablePrefetch);
			activeStreams.add(input);
			output = new Output(MinimalWorkflow.this, enablePrefetch);
			activeStreams.add(output);
		
			if (isMaster()) {
					minimalSource.setInput(input);
					output.addConsumer(minimalSink, "MinimalSink");			
			}

			connect();
			
			if (isWorker()) {
				if (!tasksToExclude.contains(MinimalWorkflowTasks.CLONER_TASK)) {
						for(int i = 0; i <clonerTasks.size(); i++){
							ClonerTask task = clonerTasks.get(i);
							input.addConsumer(task, "ClonerTask");			
							task.setOutput(output);
						}
				}
			}
			
			if (isMaster()){
				// run all sources in parallel threads
				Thread minimalSourceThread = new Thread(() -> {
					try {
						setTaskInProgess(minimalSource);
						minimalSource.produce();
						setTaskWaiting(minimalSource);
					} catch (Exception ex) {
						reportInternalException(ex);
						terminate();
					}finally {
						activeSources.remove(Thread.currentThread());
					}
				});
				activeSources.add(minimalSourceThread);
				minimalSourceThread.start();	
				sendConfigurations();
			}
					
		} catch (Exception e) {
			log(LogLevel.ERROR, e.getMessage());
		}
	}				
	
	public void sendConfigurations(){
	}
	
	public Input getInput() {
		return input;
	}
	public Output getOutput() {
		return output;
	}
	
	public MinimalSource getMinimalSource() {
		return minimalSource;
	}
	public ClonerTask getClonerTask() {
		if(clonerTasks.size()>0)
			return clonerTasks.get(0);
		else 
			return null;
	}
	public ParallelTaskList<ClonerTask> getClonerTasks() {
		return clonerTasks;	
	}	
	public MinimalSink getMinimalSink() {
		return minimalSink;
	}
	
	public MinimalWorkflow createWorker() {
		MinimalWorkflow worker = new MinimalWorkflow(Mode.WORKER,parallelization);
		worker.setInstanceId(instanceId);
		return worker;
	}
	
	@Override
	public MinimalWorkflow excludeTask(MinimalWorkflowTasks task) {
		if (task == null) throw new IllegalArgumentException("task cannot be null");
		this.tasksToExclude.add(task);
		return this;
	}
	
	@Override
	public MinimalWorkflow excludeTasks(Collection<MinimalWorkflowTasks> tasks) {
		for (MinimalWorkflowTasks t : tasks) {
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
		serializer.registerType(Element.class);
	}
	
	public static MinimalWorkflow run(String[] args) throws Exception {
		// Parse all values into an temporary object
		MinimalWorkflow argsHolder = new MinimalWorkflow();
		JCommander.newBuilder().addObject(argsHolder).build().parse(args);
		
		// Extract values to construct new object
		MinimalWorkflow app = new MinimalWorkflow(argsHolder.getMode(), argsHolder.getParallelization());
		JCommander.newBuilder().addObject(app).build().parse(args);
		app.run();
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		// Parse all values into an temporary object
		MinimalWorkflow argsHolder = new MinimalWorkflow();
		JCommander jct = JCommander.newBuilder().addObject(argsHolder).build();
		
		try {
			jct.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(argsHolder.isHelp()) {
			jct.setProgramName("MinimalWorkflow");
			jct.usage();
			System.exit(0);
			}
			
		run(args);
	}
	
	protected boolean cancelLocalJobs(String id) {
		boolean ret = false;
		for(ClonerTask v : clonerTasks){
			ret = v.cancelJob(id) || ret;
		}
		return ret;				
	}
	
}	
