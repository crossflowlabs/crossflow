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
public abstract class remoteURLSourceBase extends Task {

	/**
	 * Enum Identifier of this Task
	 */
	public static final ChurnRateWorkflowTasks TASK = ChurnRateWorkflowTasks.REMOTE_UR_LSOURCE; 
	
	protected ChurnRateWorkflow workflow;
	
 
	/* 
	 * URLs stream fields/methods
	 */
	protected URLs uRLs;
	protected boolean hasSentToURLs = false;

	protected URLs getURLs() {
		return uRLs;
	}

	protected void setURLs(URLs uRLs) {
		this.uRLs = uRLs;
	}

	public void sendToURLs(URL uRL) {
		uRL.addRootId(activeRootIds);
		uRL.setCacheable(cacheable);
		uRL.setTransactional(false);
		getURLs().send(uRL, TASK.getTaskName());
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

	public abstract void produce() throws Exception;
	

}

