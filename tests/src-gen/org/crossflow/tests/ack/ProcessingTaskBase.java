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

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2022-01-05T17:43:27.231342Z")
public abstract class ProcessingTaskBase extends Task  implements NumbersConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final AckWorkflowTasks TASK = AckWorkflowTasks.PROCESSING_TASK; 
	
	protected AckWorkflow workflow;
	
 
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

	public void sendToResults(StringElement stringElement) {
		stringElement.addRootId(activeRootIds);
		stringElement.setCacheable(cacheable);
		getResults().send(stringElement, TASK.getTaskName());
	}
	
	
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
	public void consumeNumbersWithNotifications(IntElement intElement) {
		try {
			workflow.getProcessingTasks().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = intElement.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(intElement.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
				StringElement result = consumeNumbers(intElement);
				if(result != null) {
					if(isCacheable()) result.setCorrelationId(intElement.getJobId());				
					result.setTransactional(false);
					sendToResults(result);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeNumbersTimeout(intElement);
					}
					if (sendFailed) {
						intElement.setFailures(intElement.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(intElement, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.getProcessingTasks().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.getProcessingTasks().getExecutor().submit(consumer);
		activeRunnables = Collections.singletonMap(intElement, future);	
		
		
		long timeout = intElement.getTimeout() > 0 ? intElement.getTimeout() : this.timeout;
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
	 * @param intElement original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeNumbersTimeout(IntElement intElement) throws Exception {
		return true;
	}
		
	public abstract StringElement consumeNumbers(IntElement intElement) throws Exception;

}

