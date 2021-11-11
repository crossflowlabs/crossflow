package org.crossflow.tests.opinionated;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.FailedJob;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.OpinionatedTask2BaseClass", date = "2021-10-26T15:08:26.810003Z")
public abstract class OpinionatedOccurencesMonitorBase extends OccurencesMonitorBase {
	
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
					Occs result = null;
					
					if(acceptInput(word))
						result = consumeWords(word);
					else 	
						workflow.getWords().send(word,this.getClass().getName());
					
					if(result != null){
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

	/*
	 * Return whether this instance of OpinionatedOccurencesMonitor will accept the task 'input' for processing.
	 */	
	public abstract boolean acceptInput(Word input);	
	

}