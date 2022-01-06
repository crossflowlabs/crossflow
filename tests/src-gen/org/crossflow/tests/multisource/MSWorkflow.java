/** This class was automatically generated and should not be modified */
package org.crossflow.tests.multisource;

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

@Generated(value = "org.crossflow.java.Workflow2Class", date = "2022-01-05T17:43:33.629524Z")
public class MSWorkflow extends Workflow<MSWorkflowTasks> {
	
	// Streams
	protected Results results;
	
	// Tasks
	protected Source1 source1;
	protected Source2 source2;
	protected Sink sink;

	protected Set<MSWorkflowTasks> tasksToExclude = EnumSet.noneOf(MSWorkflowTasks.class);

	public MSWorkflow() {
		this(Mode.MASTER, 1);
	}
	
	public MSWorkflow(Mode m) {
		this(m, 1);
	}
	
	public MSWorkflow(Mode mode, int parallelization) {
		super();
			
		this.parallelization = parallelization;	
			
		this.name = "MSWorkflow";
		this.mode = mode;
		
		if (isMaster()) {
			source1 = new Source1();
			source1.setWorkflow(this);
			tasks.add(source1);
			source2 = new Source2();
			source2.setWorkflow(this);
			tasks.add(source2);
			sink = new Sink();
			sink.setWorkflow(this);
			tasks.add(sink);
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
		
	
			results = new Results(MSWorkflow.this, enablePrefetch);
			activeStreams.add(results);
		
			if (isMaster()) {
					source1.setResults(results);
					source2.setResults(results);
					results.addConsumer(sink, "Sink");			
			}

			connect();
			
			if (isWorker()) {
			}
			
			if (isMaster()){
				// run all sources in parallel threads
				Thread source1Thread = new Thread(() -> {
					try {
						setTaskInProgess(source1);
						source1.produce();
						setTaskWaiting(source1);
					} catch (Exception ex) {
						reportInternalException(ex);
						terminate();
					}finally {
						activeSources.remove(Thread.currentThread());
					}
				});
				activeSources.add(source1Thread);
				source1Thread.start();	
				// run all sources in parallel threads
				Thread source2Thread = new Thread(() -> {
					try {
						setTaskInProgess(source2);
						source2.produce();
						setTaskWaiting(source2);
					} catch (Exception ex) {
						reportInternalException(ex);
						terminate();
					}finally {
						activeSources.remove(Thread.currentThread());
					}
				});
				activeSources.add(source2Thread);
				source2Thread.start();	
				sendConfigurations();
			}
					
		} catch (Exception e) {
			log(LogLevel.ERROR, e.getMessage());
		}
	}				
	
	public void sendConfigurations(){
	}
	
	public Results getResults() {
		return results;
	}
	
	public Source1 getSource1() {
		return source1;
	}
	public Source2 getSource2() {
		return source2;
	}
	public Sink getSink() {
		return sink;
	}
	
	public MSWorkflow createWorker() {
		MSWorkflow worker = new MSWorkflow(Mode.WORKER,parallelization);
		worker.setInstanceId(instanceId);
		return worker;
	}
	
	@Override
	public MSWorkflow excludeTask(MSWorkflowTasks task) {
		if (task == null) throw new IllegalArgumentException("task cannot be null");
		this.tasksToExclude.add(task);
		return this;
	}
	
	@Override
	public MSWorkflow excludeTasks(Collection<MSWorkflowTasks> tasks) {
		for (MSWorkflowTasks t : tasks) {
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
		serializer.registerType(StringElement.class);
	}
	
	public static MSWorkflow run(String[] args) throws Exception {
		// Parse all values into an temporary object
		MSWorkflow argsHolder = new MSWorkflow();
		JCommander.newBuilder().addObject(argsHolder).build().parse(args);
		
		// Extract values to construct new object
		MSWorkflow app = new MSWorkflow(argsHolder.getMode(), argsHolder.getParallelization());
		JCommander.newBuilder().addObject(app).build().parse(args);
		app.run();
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		// Parse all values into an temporary object
		MSWorkflow argsHolder = new MSWorkflow();
		JCommander jct = JCommander.newBuilder().addObject(argsHolder).build();
		
		try {
			jct.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(argsHolder.isHelp()) {
			jct.setProgramName("MSWorkflow");
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
