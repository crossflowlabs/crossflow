/** This class was automatically generated and should not be modified */
package org.crossflow.tests.concurrency;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2022-01-05T17:43:30.516005Z")
public abstract class SleeperBase extends Task  implements SleepTimesConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final ConcurrencyWorkflowTasks TASK = ConcurrencyWorkflowTasks.SLEEPER; 
	
	protected ConcurrencyWorkflow workflow;
	
 
	/* 
	 * Results stream fields/methods
	 */
	protected BuiltinStream<Result> results;
	protected boolean hasSentToResults = false;

	protected BuiltinStream<Result> getResults() {
		return results;
	}

	protected void setResults(BuiltinStream<Result> results) {
		this.results = results;
	}

	public void sendToResults(Result result) throws Exception {
		result.addRootId(activeRootIds);
		result.setCacheable(cacheable);
		getResults().send(result);
	}
	
	
	public void setWorkflow(ConcurrencyWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public ConcurrencyWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public ConcurrencyWorkflowTasks getTaskEnum() {
		return TASK;
	}

	@Override
	public void consumeSleepTimesWithNotifications(SleepTime sleepTime) {
		try {
			workflow.getSleepers().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = sleepTime.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(sleepTime.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
				Result result = consumeSleepTimes(sleepTime);
				if(result != null) {
					if(isCacheable()) result.setCorrelationId(sleepTime.getJobId());				
					result.setTransactional(false);
					sendToResults(result);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeSleepTimesTimeout(sleepTime);
					}
					if (sendFailed) {
						sleepTime.setFailures(sleepTime.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(sleepTime, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.getSleepers().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.getSleepers().getExecutor().submit(consumer);
		activeRunnables = Collections.singletonMap(sleepTime, future);	
		
		
		long timeout = sleepTime.getTimeout() > 0 ? sleepTime.getTimeout() : this.timeout;
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
	 * @param sleepTime original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeSleepTimesTimeout(SleepTime sleepTime) throws Exception {
		return true;
	}
		
	public abstract Result consumeSleepTimes(SleepTime sleepTime) throws Exception;

}

