/** This class was automatically generated and should not be modified */
package org.crossflow.tests.generation;

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

@Generated(value = "org.crossflow.java.Workflow2Class", date = "2021-10-26T15:08:27.785747Z")
public class TypeGenerationWorkflow extends Workflow<TypeGenerationWorkflowTasks> {
	
	// Streams
	protected A a;
	
	// Tasks
	protected ParallelTaskList<TypeTask> typeTasks = new ParallelTaskList<>();

	protected Set<TypeGenerationWorkflowTasks> tasksToExclude = EnumSet.noneOf(TypeGenerationWorkflowTasks.class);

	public TypeGenerationWorkflow() {
		this(Mode.MASTER, 1);
	}
	
	public TypeGenerationWorkflow(Mode m) {
		this(m, 1);
	}
	
	public TypeGenerationWorkflow(Mode mode, int parallelization) {
		super();
			
		this.parallelization = parallelization;	
			
		this.name = "TypeGenerationWorkflow";
		this.mode = mode;
		
		if (isMaster()) {
		}
		
		if (isWorker()) {
			if (!tasksToExclude.contains(TypeGenerationWorkflowTasks.TYPE_TASK)) {
				for(int i=1;i<=parallelization;i++){
					TypeTask task = new TypeTask();
					task.setWorkflow(this);
					tasks.add(task);
					typeTasks.add(task);
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
		
			typeTasks.init(this);
	
			a = new A(TypeGenerationWorkflow.this, enablePrefetch);
			activeStreams.add(a);
		
			if (isMaster()) {
			}

			connect();
			
			if (isWorker()) {
				if (!tasksToExclude.contains(TypeGenerationWorkflowTasks.TYPE_TASK)) {
						for(int i = 0; i <typeTasks.size(); i++){
							TypeTask task = typeTasks.get(i);
							a.addConsumer(task, "TypeTask");			
							task.setA(a);
						}
				}
			}
			
			if (isMaster()){
				sendConfigurations();
			}
					
		} catch (Exception e) {
			log(LogLevel.ERROR, e.getMessage());
		}
	}				
	
	public void sendConfigurations(){
	}
	
	public A getA() {
		return a;
	}
	
	public TypeTask getTypeTask() {
		if(typeTasks.size()>0)
			return typeTasks.get(0);
		else 
			return null;
	}
	public ParallelTaskList<TypeTask> getTypeTasks() {
		return typeTasks;	
	}	
	
	public TypeGenerationWorkflow createWorker() {
		TypeGenerationWorkflow worker = new TypeGenerationWorkflow(Mode.WORKER,parallelization);
		worker.setInstanceId(instanceId);
		return worker;
	}
	
	@Override
	public TypeGenerationWorkflow excludeTask(TypeGenerationWorkflowTasks task) {
		if (task == null) throw new IllegalArgumentException("task cannot be null");
		this.tasksToExclude.add(task);
		return this;
	}
	
	@Override
	public TypeGenerationWorkflow excludeTasks(Collection<TypeGenerationWorkflowTasks> tasks) {
		for (TypeGenerationWorkflowTasks t : tasks) {
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
		serializer.registerType(GeneratedType.class);
		serializer.registerType(GeneratedType.EnumProp.class);
		serializer.registerType(GeneratedType.ManyEnumProp.class);
	}
	
	public static TypeGenerationWorkflow run(String[] args) throws Exception {
		// Parse all values into an temporary object
		TypeGenerationWorkflow argsHolder = new TypeGenerationWorkflow();
		JCommander.newBuilder().addObject(argsHolder).build().parse(args);
		
		// Extract values to construct new object
		TypeGenerationWorkflow app = new TypeGenerationWorkflow(argsHolder.getMode(), argsHolder.getParallelization());
		JCommander.newBuilder().addObject(app).build().parse(args);
		app.run();
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		// Parse all values into an temporary object
		TypeGenerationWorkflow argsHolder = new TypeGenerationWorkflow();
		JCommander jct = JCommander.newBuilder().addObject(argsHolder).build();
		
		try {
			jct.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(argsHolder.isHelp()) {
			jct.setProgramName("TypeGenerationWorkflow");
			jct.usage();
			System.exit(0);
			}
			
		run(args);
	}
	
	protected boolean cancelLocalJobs(String id) {
		boolean ret = false;
		for(TypeTask v : typeTasks){
			ret = v.cancelJob(id) || ret;
		}
		return ret;				
	}
	
}	
