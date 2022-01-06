/** This class was automatically generated and should not be modified */
package org.crossflow.tests.minimal;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2022-01-05T17:43:32.965002Z")
public abstract class CopierTaskBase extends Task  implements InputConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final MinimalWorkflowTasks TASK = MinimalWorkflowTasks.COPIER_TASK; 
	
	protected MinimalWorkflow workflow;
	
 
	/* 
	 * Output stream fields/methods
	 */
	protected Output output;
	protected boolean hasSentToOutput = false;

	protected Output getOutput() {
		return output;
	}

	protected void setOutput(Output output) {
		this.output = output;
	}

	public void sendToOutput(Number number) {
		number.addRootId(activeRootIds);
		number.setCacheable(cacheable);
		getOutput().send(number, TASK.getTaskName());
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

	@Override
	public void consumeInputWithNotifications(Number number) {
		try {
			workflow.getCopierTasks().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = number.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(number.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
				Number result = consumeInput(number);
				if(result != null) {
					if(isCacheable()) result.setCorrelationId(number.getJobId());				
					result.setTransactional(false);
					sendToOutput(result);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeInputTimeout(number);
					}
					if (sendFailed) {
						number.setFailures(number.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(number, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.getCopierTasks().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.getCopierTasks().getExecutor().submit(consumer);
		activeRunnables = Collections.singletonMap(number, future);	
		
		
		long timeout = number.getTimeout() > 0 ? number.getTimeout() : this.timeout;
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
	 * @param number original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeInputTimeout(Number number) throws Exception {
		return true;
	}
		
	public abstract Number consumeInput(Number number) throws Exception;

}

