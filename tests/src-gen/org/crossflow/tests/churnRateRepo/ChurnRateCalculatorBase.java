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
public abstract class ChurnRateCalculatorBase extends Task  implements URLsConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final ChurnRateWorkflowTasks TASK = ChurnRateWorkflowTasks.CHURN_RATE_CALCULATOR; 
	
	protected ChurnRateWorkflow workflow;
	
 
	/* 
	 * ChurnRates stream fields/methods
	 */
	protected ChurnRates churnRates;
	protected boolean hasSentToChurnRates = false;

	protected ChurnRates getChurnRates() {
		return churnRates;
	}

	protected void setChurnRates(ChurnRates churnRates) {
		this.churnRates = churnRates;
	}

	public void sendToChurnRates(ChurnRate churnRate) {
		churnRate.addRootId(activeRootIds);
		churnRate.setCacheable(cacheable);
		hasSentToChurnRates = true;
		getChurnRates().send(churnRate, TASK.getTaskName());
	}
	
	public int getTotalOutputs() {
		int count = 0;
		if (hasSentToChurnRates) count++;
		return count;
	}
	
	
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
	public void consumeURLsWithNotifications(URL uRL) {
		try {
			workflow.getChurnRateCalculators().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = uRL.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(uRL.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		hasSentToChurnRates = false;
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumeURLs(uRL);
				
				// Send confirmation to ChurnRates
				ChurnRate confirmationChurnRates = new ChurnRate();
				confirmationChurnRates.setCorrelationId(uRL.getJobId());
				confirmationChurnRates.setIsTransactionSuccessMessage(true);
				confirmationChurnRates.setTotalOutputs(getTotalOutputs());
				if (hasSentToChurnRates) {
					sendToChurnRates(confirmationChurnRates);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeURLsTimeout(uRL);
					}
					if (sendFailed) {
						uRL.setFailures(uRL.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(uRL, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.getChurnRateCalculators().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.getChurnRateCalculators().getExecutor().submit(consumer);
		activeRunnables = Collections.singletonMap(uRL, future);	
		
		
		long timeout = uRL.getTimeout() > 0 ? uRL.getTimeout() : this.timeout;
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
	 * @param uRL original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeURLsTimeout(URL uRL) throws Exception {
		return true;
	}
		
	public abstract void consumeURLs(URL uRL) throws Exception;

}

