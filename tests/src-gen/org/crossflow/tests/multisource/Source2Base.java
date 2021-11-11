/** This class was automatically generated and should not be modified */
package org.crossflow.tests.multisource;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2021-10-26T15:08:26.363101Z")
public abstract class Source2Base extends Task {

	/**
	 * Enum Identifier of this Task
	 */
	public static final MSWorkflowTasks TASK = MSWorkflowTasks.SOURCE2; 
	
	protected MSWorkflow workflow;
	
 
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
		stringElement.setTransactional(false);
		getResults().send(stringElement, TASK.getTaskName());
	}
	
	
	public void setWorkflow(MSWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public MSWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public MSWorkflowTasks getTaskEnum() {
		return TASK;
	}

	public abstract void produce() throws Exception;
	

}

