/** This class was automatically generated and should not be modified */
package org.crossflow.tests.exceptions;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2022-01-05T17:43:32.243142Z")
public abstract class NumberCopierBase extends Task  implements NumbersConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final ExceptionsWorkflowTasks TASK = ExceptionsWorkflowTasks.NUMBER_COPIER; 
	
	protected ExceptionsWorkflow workflow;
	
 
	/* 
	 * Results stream fields/methods
	 */
	protected Results results;
	protected boolean hasSentToResults = false;

	protected Results getResults() {
		return results;
	}

	protected void setResults(Results results) {
		this.results = results;
	}

	public void sendToResults(Number number) {
		number.addRootId(activeRootIds);
		number.setCacheable(cacheable);
		getResults().send(number, TASK.getTaskName());
	}
	
	
	public void setWorkflow(ExceptionsWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public ExceptionsWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public ExceptionsWorkflowTasks getTaskEnum() {
		return TASK;
	}

	@Override
	public void consumeNumbersWithNotifications(Number number) {
		try {
			workflow.getNumberCopiers().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = number.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(number.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
				Number result = consumeNumbers(number);
				if(result != null) {
					if(isCacheable()) result.setCorrelationId(number.getJobId());				
					result.setTransactional(false);
					sendToResults(result);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeNumbersTimeout(number);
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
					
					workflow.getNumberCopiers().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.getNumberCopiers().getExecutor().submit(consumer);
		activeRunnables = Collections.singletonMap(number, future);	
		
		
		long timeout = number.getTimeout() > 0 ? number.getTimeout() : this.timeout;
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
	 * @param number original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeNumbersTimeout(Number number) throws Exception {
		return true;
	}
		
	public abstract Number consumeNumbers(Number number) throws Exception;

}

