/** This class was automatically generated and should not be modified */
package org.crossflow.tests.churnRateRepo;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2022-01-05T17:43:29.814671Z")
public abstract class ResultsSinkBase extends Task  implements ChurnRatesConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final ChurnRateWorkflowTasks TASK = ChurnRateWorkflowTasks.RESULTS_SINK; 
	
	protected ChurnRateWorkflow workflow;
	
	
	public void setWorkflow(ChurnRateWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public ChurnRateWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public ChurnRateWorkflowTasks getTaskEnum() {
		return TASK;
	}

	@Override
	public void consumeChurnRatesWithNotifications(ChurnRate churnRate) {
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumeChurnRates(churnRate);
				
				
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeChurnRatesTimeout(churnRate);
					}
					if (sendFailed) {
						churnRate.setFailures(churnRate.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(churnRate, new Exception(ex), this));
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
	 * @param churnRate original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeChurnRatesTimeout(ChurnRate churnRate) throws Exception {
		return true;
	}
		
	public abstract void consumeChurnRates(ChurnRate churnRate) throws Exception;

}

