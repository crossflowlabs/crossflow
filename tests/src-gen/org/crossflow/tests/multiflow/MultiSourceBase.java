/** This class was automatically generated and should not be modified */
package org.crossflow.tests.multiflow;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2021-10-26T15:08:26.086642Z")
public abstract class MultiSourceBase extends Task {

	/**
	 * Enum Identifier of this Task
	 */
	public static final MultiflowTasks TASK = MultiflowTasks.MULTI_SOURCE; 
	
	protected Multiflow workflow;
	
 
	/* 
	 * In1 stream fields/methods
	 */
	protected In1 in1;
	protected boolean hasSentToIn1 = false;

	protected In1 getIn1() {
		return in1;
	}

	protected void setIn1(In1 in1) {
		this.in1 = in1;
	}

	public void sendToIn1(Number2 number2) {
		number2.addRootId(activeRootIds);
		number2.setCacheable(cacheable);
		number2.setTransactional(false);
		hasSentToIn1 = true;
		getIn1().send(number2, TASK.getTaskName());
	}
	
	/* 
	 * In2 stream fields/methods
	 */
	protected In2 in2;
	protected boolean hasSentToIn2 = false;

	protected In2 getIn2() {
		return in2;
	}

	protected void setIn2(In2 in2) {
		this.in2 = in2;
	}

	public void sendToIn2(Number1 number1) {
		number1.addRootId(activeRootIds);
		number1.setCacheable(cacheable);
		number1.setTransactional(false);
		hasSentToIn2 = true;
		getIn2().send(number1, TASK.getTaskName());
	}
	
	public int getTotalOutputs() {
		int count = 0;
		if (hasSentToIn1) count++;
		if (hasSentToIn2) count++;
		return count;
	}
	
	
	public void setWorkflow(Multiflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public Multiflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public MultiflowTasks getTaskEnum() {
		return TASK;
	}

	public abstract void produce() throws Exception;
	

}

