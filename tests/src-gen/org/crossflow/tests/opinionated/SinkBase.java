/** This class was automatically generated and should not be modified */
package org.crossflow.tests.opinionated;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2022-01-05T17:43:34.159906Z")
public abstract class SinkBase extends Task  implements OccConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final OpinionatedWorkflowTasks TASK = OpinionatedWorkflowTasks.SINK; 
	
	protected OpinionatedWorkflow workflow;
	
	
	public void setWorkflow(OpinionatedWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public OpinionatedWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public OpinionatedWorkflowTasks getTaskEnum() {
		return TASK;
	}

	@Override
	public void consumeOccWithNotifications(Occs occs) {
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumeOcc(occs);
				
				
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeOccTimeout(occs);
					}
					if (sendFailed) {
						occs.setFailures(occs.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(occs, new Exception(ex), this));
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
	 * @param occs original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeOccTimeout(Occs occs) throws Exception {
		return true;
	}
		
	public abstract void consumeOcc(Occs occs) throws Exception;

}

