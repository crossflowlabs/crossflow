/** This class was automatically generated and should not be modified */
package org.crossflow.tests.exceptions;

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

@Generated(value = "org.crossflow.java.Workflow2Class", date = "2022-01-05T17:43:32.243142Z")
public class ExceptionsWorkflow extends Workflow<ExceptionsWorkflowTasks> {
	@Parameter(names = { "-failOnNumber"}, description = "failOnNumber workflow parameter")
	protected int failOnNumber;
	
	public void setFailOnNumber(int failOnNumber) {
		this.failOnNumber = failOnNumber;
	}
	
	public int getFailOnNumber() {
		return failOnNumber;
	}
	
	// Streams
	protected Numbers numbers;
	protected Results results;
	
	// Tasks
	protected NumberSource numberSource;
	protected ResultsSink resultsSink;
	protected ParallelTaskList<NumberCopier> numberCopiers = new ParallelTaskList<>();

	protected Set<ExceptionsWorkflowTasks> tasksToExclude = EnumSet.noneOf(ExceptionsWorkflowTasks.class);

	public ExceptionsWorkflow() {
		this(Mode.MASTER, 1);
	}
	
	public ExceptionsWorkflow(Mode m) {
		this(m, 1);
	}
	
	public ExceptionsWorkflow(Mode mode, int parallelization) {
		super();
			
		this.parallelization = parallelization;	
			
		this.name = "ExceptionsWorkflow";
		this.mode = mode;
		
		if (isMaster()) {
			numberSource = new NumberSource();
			numberSource.setWorkflow(this);
			tasks.add(numberSource);
			resultsSink = new ResultsSink();
			resultsSink.setWorkflow(this);
			tasks.add(resultsSink);
		}
		
		if (isWorker()) {
			if (!tasksToExclude.contains(ExceptionsWorkflowTasks.NUMBER_COPIER)) {
				for(int i=1;i<=parallelization;i++){
					NumberCopier task = new NumberCopier();
					task.setWorkflow(this);
					tasks.add(task);
					numberCopiers.add(task);
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
		
			numberCopiers.init(this);
	
			numbers = new Numbers(ExceptionsWorkflow.this, enablePrefetch);
			activeStreams.add(numbers);
			results = new Results(ExceptionsWorkflow.this, enablePrefetch);
			activeStreams.add(results);
		
			if (isMaster()) {
					numberSource.setNumbers(numbers);
					results.addConsumer(resultsSink, "ResultsSink");			
			}

			connect();
			
			if (isWorker()) {
				if (!tasksToExclude.contains(ExceptionsWorkflowTasks.NUMBER_COPIER)) {
						for(int i = 0; i <numberCopiers.size(); i++){
							NumberCopier task = numberCopiers.get(i);
							numbers.addConsumer(task, "NumberCopier");			
							task.setResults(results);
						}
				}
			}
			
			if (isMaster()){
				// run all sources in parallel threads
				Thread numberSourceThread = new Thread(() -> {
					try {
						setTaskInProgess(numberSource);
						numberSource.produce();
						setTaskWaiting(numberSource);
					} catch (Exception ex) {
						reportInternalException(ex);
						terminate();
					}finally {
						activeSources.remove(Thread.currentThread());
					}
				});
				activeSources.add(numberSourceThread);
				numberSourceThread.start();	
				sendConfigurations();
			}
					
		} catch (Exception e) {
			log(LogLevel.ERROR, e.getMessage());
		}
	}				
	
	public void sendConfigurations(){
	}
	
	public Numbers getNumbers() {
		return numbers;
	}
	public Results getResults() {
		return results;
	}
	
	public NumberSource getNumberSource() {
		return numberSource;
	}
	public NumberCopier getNumberCopier() {
		if(numberCopiers.size()>0)
			return numberCopiers.get(0);
		else 
			return null;
	}
	public ParallelTaskList<NumberCopier> getNumberCopiers() {
		return numberCopiers;	
	}	
	public ResultsSink getResultsSink() {
		return resultsSink;
	}
	
	public ExceptionsWorkflow createWorker() {
		ExceptionsWorkflow worker = new ExceptionsWorkflow(Mode.WORKER,parallelization);
		worker.setInstanceId(instanceId);
		return worker;
	}
	
	@Override
	public ExceptionsWorkflow excludeTask(ExceptionsWorkflowTasks task) {
		if (task == null) throw new IllegalArgumentException("task cannot be null");
		this.tasksToExclude.add(task);
		return this;
	}
	
	@Override
	public ExceptionsWorkflow excludeTasks(Collection<ExceptionsWorkflowTasks> tasks) {
		for (ExceptionsWorkflowTasks t : tasks) {
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
		serializer.registerType(Number.class);
	}
	
	public static ExceptionsWorkflow run(String[] args) throws Exception {
		// Parse all values into an temporary object
		ExceptionsWorkflow argsHolder = new ExceptionsWorkflow();
		JCommander.newBuilder().addObject(argsHolder).build().parse(args);
		
		// Extract values to construct new object
		ExceptionsWorkflow app = new ExceptionsWorkflow(argsHolder.getMode(), argsHolder.getParallelization());
		JCommander.newBuilder().addObject(app).build().parse(args);
		app.run();
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		// Parse all values into an temporary object
		ExceptionsWorkflow argsHolder = new ExceptionsWorkflow();
		JCommander jct = JCommander.newBuilder().addObject(argsHolder).build();
		
		try {
			jct.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(argsHolder.isHelp()) {
			jct.setProgramName("ExceptionsWorkflow");
			jct.usage();
			System.exit(0);
			}
			
		run(args);
	}
	
	protected boolean cancelLocalJobs(String id) {
		boolean ret = false;
		for(NumberCopier v : numberCopiers){
			ret = v.cancelJob(id) || ret;
		}
		return ret;				
	}
	
}	
