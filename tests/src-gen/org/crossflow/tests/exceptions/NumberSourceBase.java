/** This class was automatically generated and should not be modified */
package org.crossflow.tests.exceptions;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2022-01-05T17:43:32.243142Z")
public abstract class NumberSourceBase extends Task {

	/**
	 * Enum Identifier of this Task
	 */
	public static final ExceptionsWorkflowTasks TASK = ExceptionsWorkflowTasks.NUMBER_SOURCE; 
	
	protected ExceptionsWorkflow workflow;
	
 
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

	public void sendToNumbers(Number number) {
		number.addRootId(activeRootIds);
		number.setCacheable(cacheable);
		number.setTransactional(false);
		getNumbers().send(number, TASK.getTaskName());
	}
	
	
	public void setWorkflow(ExceptionsWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public ExceptionsWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public ExceptionsWorkflowTasks getTaskEnum() {
		return TASK;
	}

	public abstract void produce() throws Exception;
	

}

