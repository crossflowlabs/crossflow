/** This class was automatically generated and should not be modified */
package org.crossflow.tests.calculator;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2021-10-26T15:08:22.866352Z")
public abstract class CalculationSourceBase extends Task {

	/**
	 * Enum Identifier of this Task
	 */
	public static final CalculatorWorkflowTasks TASK = CalculatorWorkflowTasks.CALCULATION_SOURCE; 
	
	protected CalculatorWorkflow workflow;
	
 
	/* 
	 * Calculations stream fields/methods
	 */
	protected Calculations calculations;
	protected boolean hasSentToCalculations = false;

	protected Calculations getCalculations() {
		return calculations;
	}

	protected void setCalculations(Calculations calculations) {
		this.calculations = calculations;
	}

	public void sendToCalculations(Calculation calculation) {
		calculation.addRootId(activeRootIds);
		calculation.setCacheable(cacheable);
		calculation.setTransactional(false);
		getCalculations().send(calculation, TASK.getTaskName());
	}
	
	
	public void setWorkflow(CalculatorWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public CalculatorWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public CalculatorWorkflowTasks getTaskEnum() {
		return TASK;
	}

	public abstract void produce() throws Exception;
	

}

