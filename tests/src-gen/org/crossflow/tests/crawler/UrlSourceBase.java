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
public abstract class UrlSourceBase extends Task {

	/**
	 * Enum Identifier of this Task
	 */
	public static final CrawlerWorkflowTasks TASK = CrawlerWorkflowTasks.URL_SOURCE; 
	
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
		url.setTransactional(false);
		getUrls().send(url, TASK.getTaskName());
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

	public abstract void produce() throws Exception;
	

}

