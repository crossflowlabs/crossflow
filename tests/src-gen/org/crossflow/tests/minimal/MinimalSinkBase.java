/** This class was automatically generated and should not be modified */
package org.crossflow.tests.minimal;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2021-10-26T15:08:25.860216Z")
public abstract class MinimalSinkBase extends Task  implements OutputConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final MinimalWorkflowTasks TASK = MinimalWorkflowTasks.MINIMAL_SINK; 
	
	protected MinimalWorkflow workflow;
	
	
	public void setWorkflow(MinimalWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public MinimalWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public MinimalWorkflowTasks getTaskEnum() {
		return TASK;
	}

	@Override
	public void consumeOutputWithNotifications(Number number) {
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumeOutput(number);
				
				
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeOutputTimeout(number);
					}
					if (sendFailed) {
						number.setFailures(number.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(number, new Exception(ex), this));
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
	 * @param number original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeOutputTimeout(Number number) throws Exception {
		return true;
	}
		
	public abstract void consumeOutput(Number number) throws Exception;

}

