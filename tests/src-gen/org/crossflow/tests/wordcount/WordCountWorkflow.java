/** This class was automatically generated and should not be modified */
package org.crossflow.tests.wordcount;

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
import org.crossflow.runtime.serialization.XstreamSerializer;import org.crossflow.runtime.utils.ControlSignal;
import org.crossflow.runtime.utils.ControlSignal.ControlSignals;
import org.crossflow.runtime.utils.LogLevel;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

@Generated(value = "org.crossflow.java.Workflow2Class", date = "2022-01-05T17:43:35.469246Z")
public class WordCountWorkflow extends Workflow<WordCountWorkflowTasks> {
	
	// Streams
	protected Lines lines;
	protected WordFrequencies wordFrequencies;
	
	// Tasks
	protected LineSource lineSource;
	protected WordCountSink wordCountSink;
	protected ParallelTaskList<WordCounter> wordCounters = new ParallelTaskList<>();

	protected Set<WordCountWorkflowTasks> tasksToExclude = EnumSet.noneOf(WordCountWorkflowTasks.class);

	public WordCountWorkflow() {
		this(Mode.MASTER, 1);
	}
	
	public WordCountWorkflow(Mode m) {
		this(m, 1);
	}
	
	public WordCountWorkflow(Mode mode, int parallelization) {
		super();
			
		this.parallelization = parallelization;	
			
		this.name = "WordCountWorkflow";
		this.mode = mode;
		
		if (isMaster()) {
			lineSource = new LineSource();
			lineSource.setWorkflow(this);
			tasks.add(lineSource);
			wordCountSink = new WordCountSink();
			wordCountSink.setWorkflow(this);
			tasks.add(wordCountSink);
		}
		
		if (isWorker()) {
			if (!tasksToExclude.contains(WordCountWorkflowTasks.WORD_COUNTER)) {
				for(int i=1;i<=parallelization;i++){
					WordCounter task = new WordCounter();
					task.setWorkflow(this);
					tasks.add(task);
					wordCounters.add(task);
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
		
			wordCounters.init(this);
	
			lines = new Lines(WordCountWorkflow.this, enablePrefetch);
			activeStreams.add(lines);
			wordFrequencies = new WordFrequencies(WordCountWorkflow.this, enablePrefetch);
			activeStreams.add(wordFrequencies);
		
			if (isMaster()) {
					lineSource.setLines(lines);
					wordFrequencies.addConsumer(wordCountSink, "WordCountSink");			
			}

			connect();
			
			if (isWorker()) {
				if (!tasksToExclude.contains(WordCountWorkflowTasks.WORD_COUNTER)) {
						for(int i = 0; i <wordCounters.size(); i++){
							WordCounter task = wordCounters.get(i);
							lines.addConsumer(task, "WordCounter");			
							task.setWordFrequencies(wordFrequencies);
						}
				}
			}
			
			if (isMaster()){
				// run all sources in parallel threads
				Thread lineSourceThread = new Thread(() -> {
					try {
						setTaskInProgess(lineSource);
						lineSource.produce();
						setTaskWaiting(lineSource);
					} catch (Exception ex) {
						reportInternalException(ex);
						terminate();
					}finally {
						activeSources.remove(Thread.currentThread());
					}
				});
				activeSources.add(lineSourceThread);
				lineSourceThread.start();	
				sendConfigurations();
			}
					
		} catch (Exception e) {
			log(LogLevel.ERROR, e.getMessage());
		}
	}				
	
	public void sendConfigurations(){
	}
	
	public Lines getLines() {
		return lines;
	}
	public WordFrequencies getWordFrequencies() {
		return wordFrequencies;
	}
	
	public LineSource getLineSource() {
		return lineSource;
	}
	public WordCounter getWordCounter() {
		if(wordCounters.size()>0)
			return wordCounters.get(0);
		else 
			return null;
	}
	public ParallelTaskList<WordCounter> getWordCounters() {
		return wordCounters;	
	}	
	public WordCountSink getWordCountSink() {
		return wordCountSink;
	}
	
	public WordCountWorkflow createWorker() {
		WordCountWorkflow worker = new WordCountWorkflow(Mode.WORKER,parallelization);
		worker.setInstanceId(instanceId);
		return worker;
	}
	
	@Override
	public WordCountWorkflow excludeTask(WordCountWorkflowTasks task) {
		if (task == null) throw new IllegalArgumentException("task cannot be null");
		this.tasksToExclude.add(task);
		return this;
	}
	
	@Override
	public WordCountWorkflow excludeTasks(Collection<WordCountWorkflowTasks> tasks) {
		for (WordCountWorkflowTasks t : tasks) {
			excludeTask(t);
		}
		return this;
	}
	
	@Override
	protected Serializer createSerializer() {
		return new XstreamSerializer();
	}
	
	@Override
	protected void registerCustomSerializationTypes(Serializer serializer) {
		checkNotNull(serializer);
		serializer.registerType(Line.class);
		serializer.registerType(WordFrequency.class);
	}
	
	public static WordCountWorkflow run(String[] args) throws Exception {
		// Parse all values into an temporary object
		WordCountWorkflow argsHolder = new WordCountWorkflow();
		JCommander.newBuilder().addObject(argsHolder).build().parse(args);
		
		// Extract values to construct new object
		WordCountWorkflow app = new WordCountWorkflow(argsHolder.getMode(), argsHolder.getParallelization());
		JCommander.newBuilder().addObject(app).build().parse(args);
		app.run();
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		// Parse all values into an temporary object
		WordCountWorkflow argsHolder = new WordCountWorkflow();
		JCommander jct = JCommander.newBuilder().addObject(argsHolder).build();
		
		try {
			jct.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(argsHolder.isHelp()) {
			jct.setProgramName("WordCountWorkflow");
			jct.usage();
			System.exit(0);
			}
			
		run(args);
	}
	
	protected boolean cancelLocalJobs(String id) {
		boolean ret = false;
		for(WordCounter v : wordCounters){
			ret = v.cancelJob(id) || ret;
		}
		return ret;				
	}
	
}	
