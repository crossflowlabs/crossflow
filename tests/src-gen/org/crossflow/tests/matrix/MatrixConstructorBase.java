/** This class was automatically generated and should not be modified */
package org.crossflow.tests.matrix;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2021-10-26T15:08:25.592432Z")
public abstract class MatrixConstructorBase extends Task  implements MatrixConfigurationsConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final MatrixWorkflowTasks TASK = MatrixWorkflowTasks.MATRIX_CONSTRUCTOR; 
	
	protected MatrixWorkflow workflow;
	
 
	/* 
	 * Matrices stream fields/methods
	 */
	protected Matrices matrices;
	protected boolean hasSentToMatrices = false;

	protected Matrices getMatrices() {
		return matrices;
	}

	protected void setMatrices(Matrices matrices) {
		this.matrices = matrices;
	}

	public void sendToMatrices(Matrix matrix) {
		matrix.addRootId(activeRootIds);
		matrix.setCacheable(cacheable);
		getMatrices().send(matrix, TASK.getTaskName());
	}
	
	
	public void setWorkflow(MatrixWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public MatrixWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public MatrixWorkflowTasks getTaskEnum() {
		return TASK;
	}

	@Override
	public void consumeMatrixConfigurationsWithNotifications(MatrixConfiguration matrixConfiguration) {
		try {
			workflow.getMatrixConstructors().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = matrixConfiguration.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(matrixConfiguration.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
				Matrix result = consumeMatrixConfigurations(matrixConfiguration);
				if(result != null) {
					if(isCacheable()) result.setCorrelationId(matrixConfiguration.getJobId());				
					result.setTransactional(false);
					sendToMatrices(result);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeMatrixConfigurationsTimeout(matrixConfiguration);
					}
					if (sendFailed) {
						matrixConfiguration.setFailures(matrixConfiguration.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(matrixConfiguration, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.getMatrixConstructors().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.getMatrixConstructors().getExecutor().submit(consumer);
		activeRunnables = Collections.singletonMap(matrixConfiguration, future);	
		
		
		long timeout = matrixConfiguration.getTimeout() > 0 ? matrixConfiguration.getTimeout() : this.timeout;
		if (timeout > 0) {
			Futures.withTimeout(future, timeout, TimeUnit.SECONDS, workflow.getTimeoutManager());
		}
	
	}
	
	/**
	 * Cleanup callback in the event of a timeout.
	 *
	 * If this method returns {@code true} then a failed job will be registered by
	 * crossflow
	 *
	 * @param matrixConfiguration original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeMatrixConfigurationsTimeout(MatrixConfiguration matrixConfiguration) throws Exception {
		return true;
	}
		
	public abstract Matrix consumeMatrixConfigurations(MatrixConfiguration matrixConfiguration) throws Exception;

}

