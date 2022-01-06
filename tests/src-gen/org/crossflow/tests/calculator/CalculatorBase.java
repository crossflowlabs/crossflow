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
public abstract class CalculatorBase extends Task  implements CalculationsConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final CalculatorWorkflowTasks TASK = CalculatorWorkflowTasks.CALCULATOR; 
	
	protected CalculatorWorkflow workflow;
	
 
	/* 
	 * CalculationResults stream fields/methods
	 */
	protected CalculationResults calculationResults;
	protected boolean hasSentToCalculationResults = false;

	protected CalculationResults getCalculationResults() {
		return calculationResults;
	}

	protected void setCalculationResults(CalculationResults calculationResults) {
		this.calculationResults = calculationResults;
	}

	public void sendToCalculationResults(CalculationResult calculationResult) {
		calculationResult.addRootId(activeRootIds);
		calculationResult.setCacheable(cacheable);
		getCalculationResults().send(calculationResult, TASK.getTaskName());
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

	@Override
	public void consumeCalculationsWithNotifications(Calculation calculation) {
		try {
			workflow.getCalculators().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = calculation.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(calculation.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
				CalculationResult result = consumeCalculations(calculation);
				if(result != null) {
					if(isCacheable()) result.setCorrelationId(calculation.getJobId());				
					result.setTransactional(false);
					sendToCalculationResults(result);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeCalculationsTimeout(calculation);
					}
					if (sendFailed) {
						calculation.setFailures(calculation.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(calculation, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.getCalculators().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.getCalculators().getExecutor().submit(consumer);
		activeRunnables = Collections.singletonMap(calculation, future);	
		
		
		long timeout = calculation.getTimeout() > 0 ? calculation.getTimeout() : this.timeout;
		if (timeout > 0) {
			Futures.withTimeout(future, timeout, TimeUnit.SECONDS, workflow.getTimeoutManager());
		}
	
	}
	
	/**
	 * Cleanup callback in the event of a timeout.
	 *
	 * If this method returns {@code true} then a failed job will be registered by
	 * crossflow
	 *
	 * @param calculation original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeCalculationsTimeout(Calculation calculation) throws Exception {
		return true;
	}
		
	public abstract CalculationResult consumeCalculations(Calculation calculation) throws Exception;

}

