/** This class was automatically generated and should not be modified */
package org.crossflow.tests.concurrency;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2022-01-05T17:43:30.516005Z")
public abstract class SleepTimeSourceBase extends Task {

	/**
	 * Enum Identifier of this Task
	 */
	public static final ConcurrencyWorkflowTasks TASK = ConcurrencyWorkflowTasks.SLEEP_TIME_SOURCE; 
	
	protected ConcurrencyWorkflow workflow;
	
 
	/* 
	 * SleepTimes stream fields/methods
	 */
	protected SleepTimes sleepTimes;
	protected boolean hasSentToSleepTimes = false;

	protected SleepTimes getSleepTimes() {
		return sleepTimes;
	}

	protected void setSleepTimes(SleepTimes sleepTimes) {
		this.sleepTimes = sleepTimes;
	}

	public void sendToSleepTimes(SleepTime sleepTime) {
		sleepTime.addRootId(activeRootIds);
		sleepTime.setCacheable(cacheable);
		sleepTime.setTransactional(false);
		getSleepTimes().send(sleepTime, TASK.getTaskName());
	}
	
	
	public void setWorkflow(ConcurrencyWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public ConcurrencyWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public ConcurrencyWorkflowTasks getTaskEnum() {
		return TASK;
	}

	public abstract void produce() throws Exception;
	

}

