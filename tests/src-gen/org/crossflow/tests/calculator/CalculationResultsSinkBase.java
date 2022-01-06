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

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2022-01-05T17:43:28.963438Z")
public abstract class CalculationResultsSinkBase extends Task  implements CalculationResultsConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final CalculatorWorkflowTasks TASK = CalculatorWorkflowTasks.CALCULATION_RESULTS_SINK; 
	
	protected CalculatorWorkflow workflow;
	
	
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

	@Override
	public void consumeCalculationResultsWithNotifications(CalculationResult calculationResult) {
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumeCalculationResults(calculationResult);
				
				
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeCalculationResultsTimeout(calculationResult);
					}
					if (sendFailed) {
						calculationResult.setFailures(calculationResult.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(calculationResult, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
	}
	
	/**
	 * Cleanup callback in the event of a timeout.
	 *
	 * If this method returns {@code true} then a failed job will be registered by
	 * crossflow
	 *
	 * @param calculationResult original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeCalculationResultsTimeout(CalculationResult calculationResult) throws Exception {
		return true;
	}
		
	public abstract void consumeCalculationResults(CalculationResult calculationResult) throws Exception;

}

