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
public abstract class SourceBase extends Task {

	/**
	 * Enum Identifier of this Task
	 */
	public static final AckWorkflowTasks TASK = AckWorkflowTasks.SOURCE; 
	
	protected AckWorkflow workflow;
	
 
	/* 
	 * Numbers stream fields/methods
	 */
	protected Numbers numbers;
	protected boolean hasSentToNumbers = false;

	protected Numbers getNumbers() {
		return numbers;
	}

	protected void setNumbers(Numbers numbers) {
		this.numbers = numbers;
	}

	public void sendToNumbers(IntElement intElement) {
		intElement.addRootId(activeRootIds);
		intElement.setCacheable(cacheable);
		intElement.setTransactional(false);
		getNumbers().send(intElement, TASK.getTaskName());
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

	public abstract void produce() throws Exception;
	

}

