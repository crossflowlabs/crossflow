/** This class was automatically generated and should not be modified */
package org.crossflow.tests.multiflow;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2021-10-26T15:08:26.086642Z")
public abstract class MultiSinkBase extends Task  implements Out1Consumer,Out2Consumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final MultiflowTasks TASK = MultiflowTasks.MULTI_SINK; 
	
	protected Multiflow workflow;
	
	
	public void setWorkflow(Multiflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public Multiflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public MultiflowTasks getTaskEnum() {
		return TASK;
	}

	@Override
	public void consumeOut1WithNotifications(Number1 number1) {
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumeOut1(number1);
				
				
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeOut1Timeout(number1);
					}
					if (sendFailed) {
						number1.setFailures(number1.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(number1, new Exception(ex), this));
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
	 * @param number1 original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeOut1Timeout(Number1 number1) throws Exception {
		return true;
	}
		
	public abstract void consumeOut1(Number1 number1) throws Exception;
	@Override
	public void consumeOut2WithNotifications(Number2 number2) {
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumeOut2(number2);
				
				
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeOut2Timeout(number2);
					}
					if (sendFailed) {
						number2.setFailures(number2.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(number2, new Exception(ex), this));
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
	 * @param number2 original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeOut2Timeout(Number2 number2) throws Exception {
		return true;
	}
		
	public abstract void consumeOut2(Number2 number2) throws Exception;

}

