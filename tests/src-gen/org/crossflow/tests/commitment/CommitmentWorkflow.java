/** This class was automatically generated and should not be modified */
package org.crossflow.tests.commitment;

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

@Generated(value = "org.crossflow.java.Workflow2Class", date = "2022-01-05T17:43:30.189489Z")
public class CommitmentWorkflow extends Workflow<CommitmentWorkflowTasks> {
	
	// Streams
	protected Animals animals;
	
	// Tasks
	protected AnimalSource animalSource;
	protected ParallelTaskList<AnimalCounter> animalCounters = new ParallelTaskList<>();

	protected Set<CommitmentWorkflowTasks> tasksToExclude = EnumSet.noneOf(CommitmentWorkflowTasks.class);

	public CommitmentWorkflow() {
		this(Mode.MASTER, 1);
	}
	
	public CommitmentWorkflow(Mode m) {
		this(m, 1);
	}
	
	public CommitmentWorkflow(Mode mode, int parallelization) {
		super();
			
		this.parallelization = parallelization;	
			
		this.name = "CommitmentWorkflow";
		this.mode = mode;
		
		if (isMaster()) {
			animalSource = new AnimalSource();
			animalSource.setWorkflow(this);
			tasks.add(animalSource);
		}
		
		if (isWorker()) {
			if (!tasksToExclude.contains(CommitmentWorkflowTasks.ANIMAL_COUNTER)) {
				for(int i=1;i<=parallelization;i++){
					AnimalCounter task = new AnimalCounter();
					task.setWorkflow(this);
					tasks.add(task);
					animalCounters.add(task);
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
		
			animalCounters.init(this);
	
			animals = new Animals(CommitmentWorkflow.this, enablePrefetch);
			activeStreams.add(animals);
		
			if (isMaster()) {
					animalSource.setAnimals(animals);
			}

			connect();
			
			if (isWorker()) {
				if (!tasksToExclude.contains(CommitmentWorkflowTasks.ANIMAL_COUNTER)) {
						for(int i = 0; i <animalCounters.size(); i++){
							AnimalCounter task = animalCounters.get(i);
							animals.addConsumer(task, "AnimalCounter");			
							task.setAnimals(animals);
						}
				}
			}
			
			if (isMaster()){
				// run all sources in parallel threads
				Thread animalSourceThread = new Thread(() -> {
					try {
						setTaskInProgess(animalSource);
						animalSource.produce();
						setTaskWaiting(animalSource);
					} catch (Exception ex) {
						reportInternalException(ex);
						terminate();
					}finally {
						activeSources.remove(Thread.currentThread());
					}
				});
				activeSources.add(animalSourceThread);
				animalSourceThread.start();	
				sendConfigurations();
			}
					
		} catch (Exception e) {
			log(LogLevel.ERROR, e.getMessage());
		}
	}				
	
	public void sendConfigurations(){
	}
	
	public Animals getAnimals() {
		return animals;
	}
	
	public AnimalSource getAnimalSource() {
		return animalSource;
	}
	public AnimalCounter getAnimalCounter() {
		if(animalCounters.size()>0)
			return animalCounters.get(0);
		else 
			return null;
	}
	public ParallelTaskList<AnimalCounter> getAnimalCounters() {
		return animalCounters;	
	}	
	
	public CommitmentWorkflow createWorker() {
		CommitmentWorkflow worker = new CommitmentWorkflow(Mode.WORKER,parallelization);
		worker.setInstanceId(instanceId);
		return worker;
	}
	
	@Override
	public CommitmentWorkflow excludeTask(CommitmentWorkflowTasks task) {
		if (task == null) throw new IllegalArgumentException("task cannot be null");
		this.tasksToExclude.add(task);
		return this;
	}
	
	@Override
	public CommitmentWorkflow excludeTasks(Collection<CommitmentWorkflowTasks> tasks) {
		for (CommitmentWorkflowTasks t : tasks) {
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
		serializer.registerType(Animal.class);
	}
	
	public static CommitmentWorkflow run(String[] args) throws Exception {
		// Parse all values into an temporary object
		CommitmentWorkflow argsHolder = new CommitmentWorkflow();
		JCommander.newBuilder().addObject(argsHolder).build().parse(args);
		
		// Extract values to construct new object
		CommitmentWorkflow app = new CommitmentWorkflow(argsHolder.getMode(), argsHolder.getParallelization());
		JCommander.newBuilder().addObject(app).build().parse(args);
		app.run();
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		// Parse all values into an temporary object
		CommitmentWorkflow argsHolder = new CommitmentWorkflow();
		JCommander jct = JCommander.newBuilder().addObject(argsHolder).build();
		
		try {
			jct.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(argsHolder.isHelp()) {
			jct.setProgramName("CommitmentWorkflow");
			jct.usage();
			System.exit(0);
			}
			
		run(args);
	}
	
	protected boolean cancelLocalJobs(String id) {
		boolean ret = false;
		for(AnimalCounter v : animalCounters){
			ret = v.cancelJob(id) || ret;
		}
		return ret;				
	}
	
}	
