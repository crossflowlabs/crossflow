/** This class was automatically generated and should not be modified */
package orphanqueuesworkflow;

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

@Generated(value = "org.crossflow.java.Workflow2Class", date = "2022-01-05T17:43:34.478356Z")
public class orphanqueuesworkflow extends Workflow<orphanqueuesworkflowTasks> {
	
	// Streams
	protected BuiltinStream<ty1> q0;
	protected q1 q1;
	protected BuiltinStream<ty1> q2;
	
	// Tasks
	protected ParallelTaskList<t1> t1s = new ParallelTaskList<>();

	protected Set<orphanqueuesworkflowTasks> tasksToExclude = EnumSet.noneOf(orphanqueuesworkflowTasks.class);

	public orphanqueuesworkflow() {
		this(Mode.MASTER, 1);
	}
	
	public orphanqueuesworkflow(Mode m) {
		this(m, 1);
	}
	
	public orphanqueuesworkflow(Mode mode, int parallelization) {
		super();
			
		this.parallelization = parallelization;	
			
		this.name = "orphanqueuesworkflow";
		this.mode = mode;
		
		if (isMaster()) {
		}
		
		if (isWorker()) {
			if (!tasksToExclude.contains(orphanqueuesworkflowTasks.T1)) {
				for(int i=1;i<=parallelization;i++){
					t1 task = new t1();
					task.setWorkflow(this);
					tasks.add(task);
					t1s.add(task);
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
		
			t1s.init(this);
	
			q0 = new BuiltinStream<ty1>(this, "q0", false);
			q0.init();
			activeStreams.add(q0);
			q1 = new q1(orphanqueuesworkflow.this, enablePrefetch);
			activeStreams.add(q1);
			q2 = new BuiltinStream<ty1>(this, "q2", false);
			q2.init();
			activeStreams.add(q2);
		
			if (isMaster()) {
			}

			connect();
			
			if (isWorker()) {
				if (!tasksToExclude.contains(orphanqueuesworkflowTasks.T1)) {
						for(int i = 0; i <t1s.size(); i++){
							t1 task = t1s.get(i);
							q1.addConsumer(task, "t1");			
							task.setq2(q2);
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
	
	public BuiltinStream<ty1> getq0() {
		return q0;
	}
	public q1 getq1() {
		return q1;
	}
	public BuiltinStream<ty1> getq2() {
		return q2;
	}
	
	public t1 gett1() {
		if(t1s.size()>0)
			return t1s.get(0);
		else 
			return null;
	}
	public ParallelTaskList<t1> gett1s() {
		return t1s;	
	}	
	
	public orphanqueuesworkflow createWorker() {
		orphanqueuesworkflow worker = new orphanqueuesworkflow(Mode.WORKER,parallelization);
		worker.setInstanceId(instanceId);
		return worker;
	}
	
	@Override
	public orphanqueuesworkflow excludeTask(orphanqueuesworkflowTasks task) {
		if (task == null) throw new IllegalArgumentException("task cannot be null");
		this.tasksToExclude.add(task);
		return this;
	}
	
	@Override
	public orphanqueuesworkflow excludeTasks(Collection<orphanqueuesworkflowTasks> tasks) {
		for (orphanqueuesworkflowTasks t : tasks) {
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
		serializer.registerType(ty1.class);
	}
	
	public static orphanqueuesworkflow run(String[] args) throws Exception {
		// Parse all values into an temporary object
		orphanqueuesworkflow argsHolder = new orphanqueuesworkflow();
		JCommander.newBuilder().addObject(argsHolder).build().parse(args);
		
		// Extract values to construct new object
		orphanqueuesworkflow app = new orphanqueuesworkflow(argsHolder.getMode(), argsHolder.getParallelization());
		JCommander.newBuilder().addObject(app).build().parse(args);
		app.run();
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		// Parse all values into an temporary object
		orphanqueuesworkflow argsHolder = new orphanqueuesworkflow();
		JCommander jct = JCommander.newBuilder().addObject(argsHolder).build();
		
		try {
			jct.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(argsHolder.isHelp()) {
			jct.setProgramName("orphanqueuesworkflow");
			jct.usage();
			System.exit(0);
			}
			
		run(args);
	}
	
	protected boolean cancelLocalJobs(String id) {
		boolean ret = false;
		for(t1 v : t1s){
			ret = v.cancelJob(id) || ret;
		}
		return ret;				
	}
	
}	
