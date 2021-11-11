/** This class was automatically generated and should not be modified */
package org.crossflow.tests.python;

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

@Generated(value = "org.crossflow.java.Workflow2Class", date = "2021-10-26T15:08:27.215083Z")
public class PythonTestsWorkflow extends Workflow<PythonTestsWorkflowTasks> {
	
	// Streams
	protected QueueA queueA;
	
	// Tasks

	protected Set<PythonTestsWorkflowTasks> tasksToExclude = EnumSet.noneOf(PythonTestsWorkflowTasks.class);

	public PythonTestsWorkflow() {
		this(Mode.MASTER, 1);
	}
	
	public PythonTestsWorkflow(Mode m) {
		this(m, 1);
	}
	
	public PythonTestsWorkflow(Mode mode, int parallelization) {
		super();
			
		this.parallelization = parallelization;	
			
		this.name = "PythonTestsWorkflow";
		this.mode = mode;
		
		if (isMaster()) {
		}
		
		if (isWorker()) {
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
		
	
			queueA = new QueueA(PythonTestsWorkflow.this, enablePrefetch);
			activeStreams.add(queueA);
		
			if (isMaster()) {
			}

			connect();
			
			if (isWorker()) {
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
	
	public QueueA getQueueA() {
		return queueA;
	}
	
	
	public PythonTestsWorkflow createWorker() {
		PythonTestsWorkflow worker = new PythonTestsWorkflow(Mode.WORKER,parallelization);
		worker.setInstanceId(instanceId);
		return worker;
	}
	
	@Override
	public PythonTestsWorkflow excludeTask(PythonTestsWorkflowTasks task) {
		if (task == null) throw new IllegalArgumentException("task cannot be null");
		this.tasksToExclude.add(task);
		return this;
	}
	
	@Override
	public PythonTestsWorkflow excludeTasks(Collection<PythonTestsWorkflowTasks> tasks) {
		for (PythonTestsWorkflowTasks t : tasks) {
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
		serializer.registerType(TypeA.class);
	}
	
	public static PythonTestsWorkflow run(String[] args) throws Exception {
		// Parse all values into an temporary object
		PythonTestsWorkflow argsHolder = new PythonTestsWorkflow();
		JCommander.newBuilder().addObject(argsHolder).build().parse(args);
		
		// Extract values to construct new object
		PythonTestsWorkflow app = new PythonTestsWorkflow(argsHolder.getMode(), argsHolder.getParallelization());
		JCommander.newBuilder().addObject(app).build().parse(args);
		app.run();
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		// Parse all values into an temporary object
		PythonTestsWorkflow argsHolder = new PythonTestsWorkflow();
		JCommander jct = JCommander.newBuilder().addObject(argsHolder).build();
		
		try {
			jct.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(argsHolder.isHelp()) {
			jct.setProgramName("PythonTestsWorkflow");
			jct.usage();
			System.exit(0);
			}
			
		run(args);
	}
	
	protected boolean cancelLocalJobs(String id) {
		boolean ret = false;
		return ret;				
	}
	
}	
