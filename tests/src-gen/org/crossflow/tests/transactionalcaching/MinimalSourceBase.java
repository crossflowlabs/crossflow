/** This class was automatically generated and should not be modified */
package org.crossflow.tests.transactionalcaching;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2021-10-26T15:08:27.566934Z")
public abstract class MinimalSourceBase extends Task {

	/**
	 * Enum Identifier of this Task
	 */
	public static final MinimalWorkflowTasks TASK = MinimalWorkflowTasks.MINIMAL_SOURCE; 
	
	protected MinimalWorkflow workflow;
	
 
	/* 
	 * Input stream fields/methods
	 */
	protected Input input;
	protected boolean hasSentToInput = false;

	protected Input getInput() {
		return input;
	}

	protected void setInput(Input input) {
		this.input = input;
	}

	public void sendToInput(Element element) {
		element.addRootId(activeRootIds);
		element.setCacheable(cacheable);
		element.setTransactional(false);
		getInput().send(element, TASK.getTaskName());
	}
	
	
	public void setWorkflow(MinimalWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public MinimalWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public MinimalWorkflowTasks getTaskEnum() {
		return TASK;
	}

	public abstract void produce() throws Exception;
	

}

