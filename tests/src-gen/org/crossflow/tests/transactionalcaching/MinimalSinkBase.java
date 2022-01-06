/** This class was automatically generated and should not be modified */
package org.crossflow.tests.transactionalcaching;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2022-01-05T17:43:35.045518Z")
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
	public void consumeOutputWithNotifications(Element element) {
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumeOutput(element);
				
				
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeOutputTimeout(element);
					}
					if (sendFailed) {
						element.setFailures(element.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(element, new Exception(ex), this));
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
	 * @param element original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeOutputTimeout(Element element) throws Exception {
		return true;
	}
		
	public abstract void consumeOutput(Element element) throws Exception;

}

