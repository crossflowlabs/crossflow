/** This class was automatically generated and should not be modified */
package org.crossflow.tests.crawler;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2022-01-05T17:43:31.905448Z")
public abstract class UrlCollectorBase extends Task  implements UrlsConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final CrawlerWorkflowTasks TASK = CrawlerWorkflowTasks.URL_COLLECTOR; 
	
	protected CrawlerWorkflow workflow;
	
 
	/* 
	 * UrlsToAnalyse stream fields/methods
	 */
	protected UrlsToAnalyse urlsToAnalyse;
	protected boolean hasSentToUrlsToAnalyse = false;

	protected UrlsToAnalyse getUrlsToAnalyse() {
		return urlsToAnalyse;
	}

	protected void setUrlsToAnalyse(UrlsToAnalyse urlsToAnalyse) {
		this.urlsToAnalyse = urlsToAnalyse;
	}

	public void sendToUrlsToAnalyse(Url url) {
		url.addRootId(activeRootIds);
		url.setCacheable(cacheable);
		getUrlsToAnalyse().send(url, TASK.getTaskName());
	}
	
	
	public void setWorkflow(CrawlerWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public CrawlerWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public CrawlerWorkflowTasks getTaskEnum() {
		return TASK;
	}

	@Override
	public void consumeUrlsWithNotifications(Url url) {
		try {
			workflow.getUrlCollectors().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = url.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(url.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
				Url result = consumeUrls(url);
				if(result != null) {
					if(isCacheable()) result.setCorrelationId(url.getJobId());				
					result.setTransactional(false);
					sendToUrlsToAnalyse(result);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeUrlsTimeout(url);
					}
					if (sendFailed) {
						url.setFailures(url.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(url, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.getUrlCollectors().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.getUrlCollectors().getExecutor().submit(consumer);
		activeRunnables = Collections.singletonMap(url, future);	
		
		
		long timeout = url.getTimeout() > 0 ? url.getTimeout() : this.timeout;
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
	 * @param url original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeUrlsTimeout(Url url) throws Exception {
		return true;
	}
		
	public abstract Url consumeUrls(Url url) throws Exception;

}

