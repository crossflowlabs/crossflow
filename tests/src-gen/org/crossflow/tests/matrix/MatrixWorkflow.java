/** This class was automatically generated and should not be modified */
package org.crossflow.tests.matrix;

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

@Generated(value = "org.crossflow.java.Workflow2Class", date = "2021-10-26T15:08:25.592432Z")
public class MatrixWorkflow extends Workflow<MatrixWorkflowTasks> {
	
	// Streams
	protected MatrixConfigurations matrixConfigurations;
	protected Matrices matrices;
	
	// Tasks
	protected MatrixConfigurationSource matrixConfigurationSource;
	protected MatrixSink matrixSink;
	protected ParallelTaskList<MatrixConstructor> matrixConstructors = new ParallelTaskList<>();

	protected Set<MatrixWorkflowTasks> tasksToExclude = EnumSet.noneOf(MatrixWorkflowTasks.class);

	public MatrixWorkflow() {
		this(Mode.MASTER, 1);
	}
	
	public MatrixWorkflow(Mode m) {
		this(m, 1);
	}
	
	public MatrixWorkflow(Mode mode, int parallelization) {
		super();
			
		this.parallelization = parallelization;	
			
		this.name = "MatrixWorkflow";
		this.mode = mode;
		
		if (isMaster()) {
			matrixConfigurationSource = new MatrixConfigurationSource();
			matrixConfigurationSource.setWorkflow(this);
			tasks.add(matrixConfigurationSource);
			matrixSink = new MatrixSink();
			matrixSink.setWorkflow(this);
			tasks.add(matrixSink);
		}
		
		if (isWorker()) {
			if (!tasksToExclude.contains(MatrixWorkflowTasks.MATRIX_CONSTRUCTOR)) {
				for(int i=1;i<=parallelization;i++){
					MatrixConstructor task = new MatrixConstructor();
					task.setWorkflow(this);
					tasks.add(task);
					matrixConstructors.add(task);
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
		
			matrixConstructors.init(this);
	
			matrixConfigurations = new MatrixConfigurations(MatrixWorkflow.this, enablePrefetch);
			activeStreams.add(matrixConfigurations);
			matrices = new Matrices(MatrixWorkflow.this, enablePrefetch);
			activeStreams.add(matrices);
		
			if (isMaster()) {
					matrixConfigurationSource.setMatrixConfigurations(matrixConfigurations);
					matrices.addConsumer(matrixSink, "MatrixSink");			
			}

			connect();
			
			if (isWorker()) {
				if (!tasksToExclude.contains(MatrixWorkflowTasks.MATRIX_CONSTRUCTOR)) {
						for(int i = 0; i <matrixConstructors.size(); i++){
							MatrixConstructor task = matrixConstructors.get(i);
							matrixConfigurations.addConsumer(task, "MatrixConstructor");			
							task.setMatrices(matrices);
						}
				}
			}
			
			if (isMaster()){
				// run all sources in parallel threads
				Thread matrixConfigurationSourceThread = new Thread(() -> {
					try {
						setTaskInProgess(matrixConfigurationSource);
						matrixConfigurationSource.produce();
						setTaskWaiting(matrixConfigurationSource);
					} catch (Exception ex) {
						reportInternalException(ex);
						terminate();
					}finally {
						activeSources.remove(Thread.currentThread());
					}
				});
				activeSources.add(matrixConfigurationSourceThread);
				matrixConfigurationSourceThread.start();	
				sendConfigurations();
			}
					
		} catch (Exception e) {
			log(LogLevel.ERROR, e.getMessage());
		}
	}				
	
	public void sendConfigurations(){
	}
	
	public MatrixConfigurations getMatrixConfigurations() {
		return matrixConfigurations;
	}
	public Matrices getMatrices() {
		return matrices;
	}
	
	public MatrixConfigurationSource getMatrixConfigurationSource() {
		return matrixConfigurationSource;
	}
	public MatrixConstructor getMatrixConstructor() {
		if(matrixConstructors.size()>0)
			return matrixConstructors.get(0);
		else 
			return null;
	}
	public ParallelTaskList<MatrixConstructor> getMatrixConstructors() {
		return matrixConstructors;	
	}	
	public MatrixSink getMatrixSink() {
		return matrixSink;
	}
	
	public MatrixWorkflow createWorker() {
		MatrixWorkflow worker = new MatrixWorkflow(Mode.WORKER,parallelization);
		worker.setInstanceId(instanceId);
		return worker;
	}
	
	@Override
	public MatrixWorkflow excludeTask(MatrixWorkflowTasks task) {
		if (task == null) throw new IllegalArgumentException("task cannot be null");
		this.tasksToExclude.add(task);
		return this;
	}
	
	@Override
	public MatrixWorkflow excludeTasks(Collection<MatrixWorkflowTasks> tasks) {
		for (MatrixWorkflowTasks t : tasks) {
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
		serializer.registerType(MatrixConfiguration.class);
		serializer.registerType(Matrix.class);
		serializer.registerType(Row.class);
	}
	
	public static MatrixWorkflow run(String[] args) throws Exception {
		// Parse all values into an temporary object
		MatrixWorkflow argsHolder = new MatrixWorkflow();
		JCommander.newBuilder().addObject(argsHolder).build().parse(args);
		
		// Extract values to construct new object
		MatrixWorkflow app = new MatrixWorkflow(argsHolder.getMode(), argsHolder.getParallelization());
		JCommander.newBuilder().addObject(app).build().parse(args);
		app.run();
		return app;
	}
	
	public static void main(String[] args) throws Exception {
		// Parse all values into an temporary object
		MatrixWorkflow argsHolder = new MatrixWorkflow();
		JCommander jct = JCommander.newBuilder().addObject(argsHolder).build();
		
		try {
			jct.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		if(argsHolder.isHelp()) {
			jct.setProgramName("MatrixWorkflow");
			jct.usage();
			System.exit(0);
			}
			
		run(args);
	}
	
	protected boolean cancelLocalJobs(String id) {
		boolean ret = false;
		for(MatrixConstructor v : matrixConstructors){
			ret = v.cancelJob(id) || ret;
		}
		return ret;				
	}
	
}	
