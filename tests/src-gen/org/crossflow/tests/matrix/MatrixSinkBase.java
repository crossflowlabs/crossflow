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
public abstract class MatrixSinkBase extends Task  implements MatricesConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final MatrixWorkflowTasks TASK = MatrixWorkflowTasks.MATRIX_SINK; 
	
	protected MatrixWorkflow workflow;
	
	
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
	public void consumeMatricesWithNotifications(Matrix matrix) {
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumeMatrices(matrix);
				
				
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeMatricesTimeout(matrix);
					}
					if (sendFailed) {
						matrix.setFailures(matrix.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(matrix, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
	}
	
	/**
	 * Cleanup callback in the event of a timeout.
	 *
	 * If this method returns {@code true} then a failed job will be registered by
	 * crossflow
	 *
	 * @param matrix original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeMatricesTimeout(Matrix matrix) throws Exception {
		return true;
	}
		
	public abstract void consumeMatrices(Matrix matrix) throws Exception;

}

