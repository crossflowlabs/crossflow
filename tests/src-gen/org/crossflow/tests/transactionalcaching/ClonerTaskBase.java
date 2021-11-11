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
public abstract class ClonerTaskBase extends Task  implements InputConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final MinimalWorkflowTasks TASK = MinimalWorkflowTasks.CLONER_TASK; 
	
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

	public void sendToOutput(Element element) {
		element.addRootId(activeRootIds);
		element.setCacheable(cacheable);
		hasSentToOutput = true;
		getOutput().send(element, TASK.getTaskName());
	}
	
	public int getTotalOutputs() {
		int count = 0;
		if (hasSentToOutput) count++;
		return count;
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
	public void consumeInputWithNotifications(Element element) {
		try {
			workflow.getClonerTasks().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = element.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(element.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		hasSentToOutput = false;
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumeInput(element);
				
				// Send confirmation to Output
				Element confirmationOutput = new Element();
				confirmationOutput.setCorrelationId(element.getJobId());
				confirmationOutput.setIsTransactionSuccessMessage(true);
				confirmationOutput.setTotalOutputs(getTotalOutputs());
				if (hasSentToOutput) {
					sendToOutput(confirmationOutput);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeInputTimeout(element);
					}
					if (sendFailed) {
						element.setFailures(element.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(element, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.getClonerTasks().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.getClonerTasks().getExecutor().submit(consumer);
		activeRunnables = Collections.singletonMap(element, future);	
		
		
		long timeout = element.getTimeout() > 0 ? element.getTimeout() : this.timeout;
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
	 * @param element original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeInputTimeout(Element element) throws Exception {
		return true;
	}
		
	public abstract void consumeInput(Element element) throws Exception;

}

