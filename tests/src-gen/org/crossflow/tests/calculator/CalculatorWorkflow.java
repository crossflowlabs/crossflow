/** This class was automatically generated and should not be modified */
package org.crossflow.tests.calculator;

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

@Generated(value = "org.crossflow.java.Workflow2Class", date = "2022-01-05T17:43:28.963438Z")
public class CalculatorWorkflow extends Workflow<CalculatorWorkflowTasks> {
	
	// Streams
	protected Calculations calculations;
	protected CalculationResults calculationResults;
	
	// Tasks
	protected CalculationSource calculationSource;
	protected CalculationResultsSink calculationResultsSink;
	protected ParallelTaskList<Calculator> calculators = new ParallelTaskList<>();

	protected Set<CalculatorWorkflowTasks> tasksToExclude = EnumSet.noneOf(CalculatorWorkflowTasks.class);

	public CalculatorWorkflow() {
		this(Mode.MASTER, 1);
	}
	
	public CalculatorWorkflow(Mode m) {
		this(m, 1);
	}
	
	public CalculatorWorkflow(Mode mode, int parallelization) {
		super();
			
		this.parallelization = parallelization;	
			
		this.name = "CalculatorWorkflow";
		this.mode = mode;
		
		if (isMaster()) {
			calculationSource = new CalculationSource();
			calculationSource.setWorkflow(this);
			tasks.add(calculationSource);
			calculationResultsSink = new CalculationResultsSink();
			calculationResultsSink.setWorkflow(this);
			tasks.add(calculationResultsSink);
		}
		
		if (isWorker()) {
			if (!tasksToExclude.contains(CalculatorWorkflowTasks.CALCULATOR)) {
				for(int i=1;i<=parallelization;i++){
					Calculator task = new Calculator();
					task.setWorkflow(this);
					tasks.add(task);
					calculators.add(task);
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
		
			calculators.init(this);
	
			calculations = new Calculations(CalculatorWorkflow.this, enablePrefetch);
			activeStreams.add(calculations);
			calculationResults = new CalculationResults(CalculatorWorkflow.this, enablePrefetch);
			activeStreams.add(calculationResults);
		
			if (isMaster()) {
					calculationSource.setCalculations(calculations);
					calculationResults.addConsumer(calculationResultsSink, "CalculationResultsSink");			
			}

			connect();
			
			if (isWorker()) {
				if (!tasksToExclude.contains(CalculatorWorkflowTasks.CALCULATOR)) {
						for(int i = 0; i <calculators.size(); i++){
							Calculator task = calculators.get(i);
							calculations.addConsumer(task, "Calculator");			
							task.setCalculationResults(calculationResults);
						}
				}
			}
			
			if (isMaster()){
				// run all sources in parallel threads
				Thread calculationSourceThread = new Thread(() -> {
					try {
						setTaskInProgess(calculationSource);
						calculationSource.produce();
						setTaskWaiting(calculationSource);
					} catch (Exception ex) {
						reportInternalException(ex);
						terminate();
					}finally {
						activeSources.remove(Thread.currentThread());
					}
				});
				activeSources.add(calculationSourceThread);
				calculationSourceThread.start();	
				sendConfigurations();
			}
					
		} catch (Exception e) {
			log(LogLevel.ERROR, e.getMessage());
		}
	}				
	
	public void sendConfigurations(){
	}
	
	public Calculations getCalculations() {
		return calculations;
	}
	public CalculationResults getCalculationResults() {
		return calculationResults;
	}
	
	public CalculationSource getCalculationSource() {
		return calculationSource;
	}
	public Calculator getCalculator() {
		if(calculators.size()>0)
			return calculators.get(0);
		else 
			return null;
	}
	public ParallelTaskList<Calculator> getCalculators() {
		return calculators;	
	}	
	public CalculationResultsSink getCalculationResultsSink() {
		return calculationResultsSink;
	}
	
	public CalculatorWorkflow createWorker() {
		CalculatorWorkflow worker = new CalculatorWorkflow(Mode.WORKER,parallelization);
		worker.setInstanceId(instanceId);
		return worker;
	}
	
	@Override
	public CalculatorWorkflow excludeTask(CalculatorWorkflowTasks task) {
		if (task == null) throw new IllegalArgumentException("task cannot be null");
		this.tasksToExclude.add(task);
		return this;
	}
	
	@Override
	public CalculatorWorkflow excludeTasks(Collection<CalculatorWorkflowTasks> tasks) {
		for (CalculatorWorkflowTasks t : tasks) {
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
		serializer.registerType(Calculation.class);
		serializer.registerType(CalculationResult.class);
		serializer.registerType(CalculationResult.WorkerLang.class);
	}
	
	public static CalculatorWorkflow run(String[] args) throws Exception {
		// Parse all values into an temporary object
		CalculatorWorkflow argsHolder = new CalculatorWorkflow();
		JCommander.newBuilder().addObject(argsHolder).build().parse(args);
		
		// Extract values to construct new object
		CalculatorWorkflow app = new CalculatorWorkflow(argsHolder.getMode(), argsHolder.getParallelization());
		JCommander.newBuilder().addObject(app).build().parse(args);
		app.run();
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		// Parse all values into an temporary object
		CalculatorWorkflow argsHolder = new CalculatorWorkflow();
		JCommander jct = JCommander.newBuilder().addObject(argsHolder).build();
		
		try {
			jct.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(argsHolder.isHelp()) {
			jct.setProgramName("CalculatorWorkflow");
			jct.usage();
			System.exit(0);
			}
			
		run(args);
	}
	
	protected boolean cancelLocalJobs(String id) {
		boolean ret = false;
		for(Calculator v : calculators){
			ret = v.cancelJob(id) || ret;
		}
		return ret;				
	}
	
}	
