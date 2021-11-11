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

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2021-10-26T15:08:25.092796Z")
public abstract class UrlAnalyserBase extends Task  implements UrlsToAnalyseConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final CrawlerWorkflowTasks TASK = CrawlerWorkflowTasks.URL_ANALYSER; 
	
	protected CrawlerWorkflow workflow;
	
 
	/* 
	 * Urls stream fields/methods
	 */
	protected Urls urls;
	protected boolean hasSentToUrls = false;

	protected Urls getUrls() {
		return urls;
	}

	protected void setUrls(Urls urls) {
		this.urls = urls;
	}

	public void sendToUrls(Url url) {
		url.addRootId(activeRootIds);
		url.setCacheable(cacheable);
		hasSentToUrls = true;
		getUrls().send(url, TASK.getTaskName());
	}
	
	public int getTotalOutputs() {
		int count = 0;
		if (hasSentToUrls) count++;
		return count;
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
	public void consumeUrlsToAnalyseWithNotifications(Url url) {
		try {
			workflow.getUrlAnalysers().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = url.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(url.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		hasSentToUrls = false;
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumeUrlsToAnalyse(url);
				
				// Send confirmation to Urls
				Url confirmationUrls = new Url();
				confirmationUrls.setCorrelationId(url.getJobId());
				confirmationUrls.setIsTransactionSuccessMessage(true);
				confirmationUrls.setTotalOutputs(getTotalOutputs());
				if (hasSentToUrls) {
					sendToUrls(confirmationUrls);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeUrlsToAnalyseTimeout(url);
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
					
					workflow.getUrlAnalysers().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.getUrlAnalysers().getExecutor().submit(consumer);
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
	public boolean onConsumeUrlsToAnalyseTimeout(Url url) throws Exception {
		return true;
	}
		
	public abstract void consumeUrlsToAnalyse(Url url) throws Exception;

}

