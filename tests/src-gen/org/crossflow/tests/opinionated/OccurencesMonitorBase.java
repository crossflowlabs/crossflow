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
public abstract class OccurencesMonitorBase extends Task  implements WordsConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final OpinionatedWorkflowTasks TASK = OpinionatedWorkflowTasks.OCCURENCES_MONITOR; 
	
	protected OpinionatedWorkflow workflow;
	
 
	/* 
	 * Occ stream fields/methods
	 */
	protected Occ occ;
	protected boolean hasSentToOcc = false;

	protected Occ getOcc() {
		return occ;
	}

	protected void setOcc(Occ occ) {
		this.occ = occ;
	}

	public void sendToOcc(Occs occs) {
		occs.addRootId(activeRootIds);
		occs.setCacheable(cacheable);
		getOcc().send(occs, TASK.getTaskName());
	}
	
	
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
	public void consumeWordsWithNotifications(Word word) {
		try {
			workflow.getOccurencesMonitors().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = word.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(word.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
				Occs result = consumeWords(word);
				if(result != null) {
					if(isCacheable()) result.setCorrelationId(word.getJobId());				
					result.setTransactional(false);
					sendToOcc(result);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeWordsTimeout(word);
					}
					if (sendFailed) {
						word.setFailures(word.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(word, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.getOccurencesMonitors().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.getOccurencesMonitors().getExecutor().submit(consumer);
		activeRunnables = Collections.singletonMap(word, future);	
		
		
		long timeout = word.getTimeout() > 0 ? word.getTimeout() : this.timeout;
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
	 * @param word original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeWordsTimeout(Word word) throws Exception {
		return true;
	}
		
	public abstract Occs consumeWords(Word word) throws Exception;

}

