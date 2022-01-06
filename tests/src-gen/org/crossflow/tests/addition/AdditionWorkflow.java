/** This class was automatically generated and should not be modified */
package org.crossflow.tests.addition;

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

@Generated(value = "org.crossflow.java.Workflow2Class", date = "2022-01-05T17:43:28.002856Z")
public class AdditionWorkflow extends Workflow<AdditionWorkflowTasks> {
	
	// Streams
	protected Additions additions;
	protected AdditionResults additionResults;
	
	// Tasks
	protected NumberPairSource numberPairSource;
	protected AdditionResultsSink additionResultsSink;
	protected ParallelTaskList<Adder> adders = new ParallelTaskList<>();

	protected Set<AdditionWorkflowTasks> tasksToExclude = EnumSet.noneOf(AdditionWorkflowTasks.class);

	public AdditionWorkflow() {
		this(Mode.MASTER, 1);
	}
	
	public AdditionWorkflow(Mode m) {
		this(m, 1);
	}
	
	public AdditionWorkflow(Mode mode, int parallelization) {
		super();
			
		this.parallelization = parallelization;	
			
		this.name = "AdditionWorkflow";
		this.mode = mode;
		
		if (isMaster()) {
			numberPairSource = new NumberPairSource();
			numberPairSource.setWorkflow(this);
			tasks.add(numberPairSource);
			additionResultsSink = new AdditionResultsSink();
			additionResultsSink.setWorkflow(this);
			tasks.add(additionResultsSink);
		}
		
		if (isWorker()) {
			if (!tasksToExclude.contains(AdditionWorkflowTasks.ADDER)) {
				for(int i=1;i<=parallelization;i++){
					Adder task = new Adder();
					task.setWorkflow(this);
					tasks.add(task);
					adders.add(task);
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
		
			adders.init(this);
	
			additions = new Additions(AdditionWorkflow.this, enablePrefetch);
			activeStreams.add(additions);
			additionResults = new AdditionResults(AdditionWorkflow.this, enablePrefetch);
			activeStreams.add(additionResults);
		
			if (isMaster()) {
					numberPairSource.setAdditions(additions);
					additionResults.addConsumer(additionResultsSink, "AdditionResultsSink");			
			}

			connect();
			
			if (isWorker()) {
				if (!tasksToExclude.contains(AdditionWorkflowTasks.ADDER)) {
						for(int i = 0; i <adders.size(); i++){
							Adder task = adders.get(i);
							additions.addConsumer(task, "Adder");			
							task.setAdditionResults(additionResults);
						}
				}
			}
			
			if (isMaster()){
				// run all sources in parallel threads
				Thread numberPairSourceThread = new Thread(() -> {
					try {
						setTaskInProgess(numberPairSource);
						numberPairSource.produce();
						setTaskWaiting(numberPairSource);
					} catch (Exception ex) {
						reportInternalException(ex);
						terminate();
					}finally {
						activeSources.remove(Thread.currentThread());
					}
				});
				activeSources.add(numberPairSourceThread);
				numberPairSourceThread.start();	
				sendConfigurations();
			}
					
		} catch (Exception e) {
			log(LogLevel.ERROR, e.getMessage());
		}
	}				
	
	public void sendConfigurations(){
	}
	
	public Additions getAdditions() {
		return additions;
	}
	public AdditionResults getAdditionResults() {
		return additionResults;
	}
	
	public NumberPairSource getNumberPairSource() {
		return numberPairSource;
	}
	public Adder getAdder() {
		if(adders.size()>0)
			return adders.get(0);
		else 
			return null;
	}
	public ParallelTaskList<Adder> getAdders() {
		return adders;	
	}	
	public AdditionResultsSink getAdditionResultsSink() {
		return additionResultsSink;
	}
	
	public AdditionWorkflow createWorker() {
		AdditionWorkflow worker = new AdditionWorkflow(Mode.WORKER,parallelization);
		worker.setInstanceId(instanceId);
		return worker;
	}
	
	@Override
	public AdditionWorkflow excludeTask(AdditionWorkflowTasks task) {
		if (task == null) throw new IllegalArgumentException("task cannot be null");
		this.tasksToExclude.add(task);
		return this;
	}
	
	@Override
	public AdditionWorkflow excludeTasks(Collection<AdditionWorkflowTasks> tasks) {
		for (AdditionWorkflowTasks t : tasks) {
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
		serializer.registerType(NumberPair.class);
		serializer.registerType(Number.class);
	}
	
	public static AdditionWorkflow run(String[] args) throws Exception {
		// Parse all values into an temporary object
		AdditionWorkflow argsHolder = new AdditionWorkflow();
		JCommander.newBuilder().addObject(argsHolder).build().parse(args);
		
		// Extract values to construct new object
		AdditionWorkflow app = new AdditionWorkflow(argsHolder.getMode(), argsHolder.getParallelization());
		JCommander.newBuilder().addObject(app).build().parse(args);
		app.run();
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		// Parse all values into an temporary object
		AdditionWorkflow argsHolder = new AdditionWorkflow();
		JCommander jct = JCommander.newBuilder().addObject(argsHolder).build();
		
		try {
			jct.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(argsHolder.isHelp()) {
			jct.setProgramName("AdditionWorkflow");
			jct.usage();
			System.exit(0);
			}
			
		run(args);
	}
	
	protected boolean cancelLocalJobs(String id) {
		boolean ret = false;
		for(Adder v : adders){
			ret = v.cancelJob(id) || ret;
		}
		return ret;				
	}
	
}	
