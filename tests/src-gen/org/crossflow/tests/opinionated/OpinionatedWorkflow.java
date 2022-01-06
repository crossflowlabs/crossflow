/** This class was automatically generated and should not be modified */
package org.crossflow.tests.opinionated;

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

@Generated(value = "org.crossflow.java.Workflow2Class", date = "2022-01-05T17:43:34.159906Z")
public class OpinionatedWorkflow extends Workflow<OpinionatedWorkflowTasks> {
	
	// Streams
	protected Words words;
	protected Occ occ;
	
	// Tasks
	protected WordSource wordSource;
	protected Sink sink;
	protected ParallelTaskList<OccurencesMonitor> occurencesMonitors = new ParallelTaskList<>();

	protected Set<OpinionatedWorkflowTasks> tasksToExclude = EnumSet.noneOf(OpinionatedWorkflowTasks.class);

	public OpinionatedWorkflow() {
		this(Mode.MASTER, 1);
	}
	
	public OpinionatedWorkflow(Mode m) {
		this(m, 1);
	}
	
	public OpinionatedWorkflow(Mode mode, int parallelization) {
		super();
			
		this.parallelization = parallelization;	
			
		this.name = "OpinionatedWorkflow";
		this.mode = mode;
		
		if (isMaster()) {
			wordSource = new WordSource();
			wordSource.setWorkflow(this);
			tasks.add(wordSource);
			sink = new Sink();
			sink.setWorkflow(this);
			tasks.add(sink);
		}
		
		if (isWorker()) {
			if (!tasksToExclude.contains(OpinionatedWorkflowTasks.OCCURENCES_MONITOR)) {
				for(int i=1;i<=parallelization;i++){
					OccurencesMonitor task = new OccurencesMonitor();
					task.setWorkflow(this);
					tasks.add(task);
					occurencesMonitors.add(task);
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
		
			occurencesMonitors.init(this);
	
			words = new Words(OpinionatedWorkflow.this, enablePrefetch);
			activeStreams.add(words);
			occ = new Occ(OpinionatedWorkflow.this, enablePrefetch);
			activeStreams.add(occ);
		
			if (isMaster()) {
					wordSource.setWords(words);
					occ.addConsumer(sink, "Sink");			
			}

			connect();
			
			if (isWorker()) {
				if (!tasksToExclude.contains(OpinionatedWorkflowTasks.OCCURENCES_MONITOR)) {
						for(int i = 0; i <occurencesMonitors.size(); i++){
							OccurencesMonitor task = occurencesMonitors.get(i);
							words.addConsumer(task, "OccurencesMonitor");			
							task.setOcc(occ);
						}
				}
			}
			
			if (isMaster()){
				// run all sources in parallel threads
				Thread wordSourceThread = new Thread(() -> {
					try {
						setTaskInProgess(wordSource);
						wordSource.produce();
						setTaskWaiting(wordSource);
					} catch (Exception ex) {
						reportInternalException(ex);
						terminate();
					}finally {
						activeSources.remove(Thread.currentThread());
					}
				});
				activeSources.add(wordSourceThread);
				wordSourceThread.start();	
				sendConfigurations();
			}
					
		} catch (Exception e) {
			log(LogLevel.ERROR, e.getMessage());
		}
	}				
	
	public void sendConfigurations(){
	}
	
	public Words getWords() {
		return words;
	}
	public Occ getOcc() {
		return occ;
	}
	
	public WordSource getWordSource() {
		return wordSource;
	}
	public OccurencesMonitor getOccurencesMonitor() {
		if(occurencesMonitors.size()>0)
			return occurencesMonitors.get(0);
		else 
			return null;
	}
	public ParallelTaskList<OccurencesMonitor> getOccurencesMonitors() {
		return occurencesMonitors;	
	}	
	public Sink getSink() {
		return sink;
	}
	
	public OpinionatedWorkflow createWorker() {
		OpinionatedWorkflow worker = new OpinionatedWorkflow(Mode.WORKER,parallelization);
		worker.setInstanceId(instanceId);
		return worker;
	}
	
	@Override
	public OpinionatedWorkflow excludeTask(OpinionatedWorkflowTasks task) {
		if (task == null) throw new IllegalArgumentException("task cannot be null");
		this.tasksToExclude.add(task);
		return this;
	}
	
	@Override
	public OpinionatedWorkflow excludeTasks(Collection<OpinionatedWorkflowTasks> tasks) {
		for (OpinionatedWorkflowTasks t : tasks) {
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
		serializer.registerType(Word.class);
		serializer.registerType(Occs.class);
	}
	
	public static OpinionatedWorkflow run(String[] args) throws Exception {
		// Parse all values into an temporary object
		OpinionatedWorkflow argsHolder = new OpinionatedWorkflow();
		JCommander.newBuilder().addObject(argsHolder).build().parse(args);
		
		// Extract values to construct new object
		OpinionatedWorkflow app = new OpinionatedWorkflow(argsHolder.getMode(), argsHolder.getParallelization());
		JCommander.newBuilder().addObject(app).build().parse(args);
		app.run();
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		// Parse all values into an temporary object
		OpinionatedWorkflow argsHolder = new OpinionatedWorkflow();
		JCommander jct = JCommander.newBuilder().addObject(argsHolder).build();
		
		try {
			jct.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(argsHolder.isHelp()) {
			jct.setProgramName("OpinionatedWorkflow");
			jct.usage();
			System.exit(0);
			}
			
		run(args);
	}
	
	protected boolean cancelLocalJobs(String id) {
		boolean ret = false;
		for(OccurencesMonitor v : occurencesMonitors){
			ret = v.cancelJob(id) || ret;
		}
		return ret;				
	}
	
}	
