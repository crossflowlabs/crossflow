/** This class was automatically generated and should not be modified */
package org.crossflow.tests.ack;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2021-10-26T15:08:21.360808Z")
public abstract class SinkBase extends Task  implements ResultsConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final AckWorkflowTasks TASK = AckWorkflowTasks.SINK; 
	
	protected AckWorkflow workflow;
	
	
	public void setWorkflow(AckWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public AckWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public AckWorkflowTasks getTaskEnum() {
		return TASK;
	}

	@Override
	public void consumeResultsWithNotifications(StringElement stringElement) {
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumeResults(stringElement);
				
				
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeResultsTimeout(stringElement);
					}
					if (sendFailed) {
						stringElement.setFailures(stringElement.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(stringElement, new Exception(ex), this));
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
	 * @param stringElement original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeResultsTimeout(StringElement stringElement) throws Exception {
		return true;
	}
		
	public abstract void consumeResults(StringElement stringElement) throws Exception;

}

