/** This class was automatically generated and should not be modified */
package org.crossflow.tests.multiflow;

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

@Generated(value = "org.crossflow.java.Workflow2Class", date = "2022-01-05T17:43:33.257764Z")
public class Multiflow extends Workflow<MultiflowTasks> {
	
	// Streams
	protected In1 in1;
	protected In2 in2;
	protected Out1 out1;
	protected Out2 out2;
	
	// Tasks
	protected MultiSource multiSource;
	protected MultiSink multiSink;
	protected ParallelTaskList<MultiTask> multiTasks = new ParallelTaskList<>();

	protected Set<MultiflowTasks> tasksToExclude = EnumSet.noneOf(MultiflowTasks.class);

	public Multiflow() {
		this(Mode.MASTER, 1);
	}
	
	public Multiflow(Mode m) {
		this(m, 1);
	}
	
	public Multiflow(Mode mode, int parallelization) {
		super();
			
		this.parallelization = parallelization;	
			
		this.name = "Multiflow";
		this.mode = mode;
		
		if (isMaster()) {
			multiSource = new MultiSource();
			multiSource.setWorkflow(this);
			tasks.add(multiSource);
			multiSink = new MultiSink();
			multiSink.setWorkflow(this);
			tasks.add(multiSink);
		}
		
		if (isWorker()) {
			if (!tasksToExclude.contains(MultiflowTasks.MULTI_TASK)) {
				for(int i=1;i<=parallelization;i++){
					MultiTask task = new MultiTask();
					task.setWorkflow(this);
					tasks.add(task);
					multiTasks.add(task);
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
		
			multiTasks.init(this);
	
			in1 = new In1(Multiflow.this, enablePrefetch);
			activeStreams.add(in1);
			in2 = new In2(Multiflow.this, enablePrefetch);
			activeStreams.add(in2);
			out1 = new Out1(Multiflow.this, enablePrefetch);
			activeStreams.add(out1);
			out2 = new Out2(Multiflow.this, enablePrefetch);
			activeStreams.add(out2);
		
			if (isMaster()) {
					multiSource.setIn1(in1);
					multiSource.setIn2(in2);
					out1.addConsumer(multiSink, "MultiSink");			
					out2.addConsumer(multiSink, "MultiSink");			
			}

			connect();
			
			if (isWorker()) {
				if (!tasksToExclude.contains(MultiflowTasks.MULTI_TASK)) {
						for(int i = 0; i <multiTasks.size(); i++){
							MultiTask task = multiTasks.get(i);
							in1.addConsumer(task, "MultiTask");			
							in2.addConsumer(task, "MultiTask");			
							task.setOut1(out1);
							task.setOut2(out2);
						}
				}
			}
			
			if (isMaster()){
				// run all sources in parallel threads
				Thread multiSourceThread = new Thread(() -> {
					try {
						setTaskInProgess(multiSource);
						multiSource.produce();
						setTaskWaiting(multiSource);
					} catch (Exception ex) {
						reportInternalException(ex);
						terminate();
					}finally {
						activeSources.remove(Thread.currentThread());
					}
				});
				activeSources.add(multiSourceThread);
				multiSourceThread.start();	
				sendConfigurations();
			}
					
		} catch (Exception e) {
			log(LogLevel.ERROR, e.getMessage());
		}
	}				
	
	public void sendConfigurations(){
	}
	
	public In1 getIn1() {
		return in1;
	}
	public In2 getIn2() {
		return in2;
	}
	public Out1 getOut1() {
		return out1;
	}
	public Out2 getOut2() {
		return out2;
	}
	
	public MultiSource getMultiSource() {
		return multiSource;
	}
	public MultiTask getMultiTask() {
		if(multiTasks.size()>0)
			return multiTasks.get(0);
		else 
			return null;
	}
	public ParallelTaskList<MultiTask> getMultiTasks() {
		return multiTasks;	
	}	
	public MultiSink getMultiSink() {
		return multiSink;
	}
	
	public Multiflow createWorker() {
		Multiflow worker = new Multiflow(Mode.WORKER,parallelization);
		worker.setInstanceId(instanceId);
		return worker;
	}
	
	@Override
	public Multiflow excludeTask(MultiflowTasks task) {
		if (task == null) throw new IllegalArgumentException("task cannot be null");
		this.tasksToExclude.add(task);
		return this;
	}
	
	@Override
	public Multiflow excludeTasks(Collection<MultiflowTasks> tasks) {
		for (MultiflowTasks t : tasks) {
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
		serializer.registerType(Number1.class);
		serializer.registerType(Number2.class);
	}
	
	public static Multiflow run(String[] args) throws Exception {
		// Parse all values into an temporary object
		Multiflow argsHolder = new Multiflow();
		JCommander.newBuilder().addObject(argsHolder).build().parse(args);
		
		// Extract values to construct new object
		Multiflow app = new Multiflow(argsHolder.getMode(), argsHolder.getParallelization());
		JCommander.newBuilder().addObject(app).build().parse(args);
		app.run();
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		// Parse all values into an temporary object
		Multiflow argsHolder = new Multiflow();
		JCommander jct = JCommander.newBuilder().addObject(argsHolder).build();
		
		try {
			jct.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(argsHolder.isHelp()) {
			jct.setProgramName("Multiflow");
			jct.usage();
			System.exit(0);
			}
			
		run(args);
	}
	
	protected boolean cancelLocalJobs(String id) {
		boolean ret = false;
		for(MultiTask v : multiTasks){
			ret = v.cancelJob(id) || ret;
		}
		return ret;				
	}
	
}	
