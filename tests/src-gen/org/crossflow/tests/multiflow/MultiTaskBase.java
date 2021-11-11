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
public abstract class MultiTaskBase extends Task  implements In1Consumer,In2Consumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final MultiflowTasks TASK = MultiflowTasks.MULTI_TASK; 
	
	protected Multiflow workflow;
	
 
	/* 
	 * Out1 stream fields/methods
	 */
	protected Out1 out1;
	protected boolean hasSentToOut1 = false;

	protected Out1 getOut1() {
		return out1;
	}

	protected void setOut1(Out1 out1) {
		this.out1 = out1;
	}

	public void sendToOut1(Number1 number1) {
		number1.addRootId(activeRootIds);
		number1.setCacheable(cacheable);
		hasSentToOut1 = true;
		getOut1().send(number1, TASK.getTaskName());
	}
	
	/* 
	 * Out2 stream fields/methods
	 */
	protected Out2 out2;
	protected boolean hasSentToOut2 = false;

	protected Out2 getOut2() {
		return out2;
	}

	protected void setOut2(Out2 out2) {
		this.out2 = out2;
	}

	public void sendToOut2(Number2 number2) {
		number2.addRootId(activeRootIds);
		number2.setCacheable(cacheable);
		hasSentToOut2 = true;
		getOut2().send(number2, TASK.getTaskName());
	}
	
	public int getTotalOutputs() {
		int count = 0;
		if (hasSentToOut1) count++;
		if (hasSentToOut2) count++;
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

	@Override
	public void consumeIn1WithNotifications(Number2 number2) {
		try {
			workflow.getMultiTasks().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = number2.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(number2.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		hasSentToOut1 = false;
		hasSentToOut2 = false;
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumeIn1(number2);
				
				// Send confirmation to Out1
				Number1 confirmationOut1 = new Number1();
				confirmationOut1.setCorrelationId(number2.getJobId());
				confirmationOut1.setIsTransactionSuccessMessage(true);
				confirmationOut1.setTotalOutputs(getTotalOutputs());
				if (hasSentToOut1) {
					sendToOut1(confirmationOut1);
				}
				
				// Send confirmation to Out2
				Number2 confirmationOut2 = new Number2();
				confirmationOut2.setCorrelationId(number2.getJobId());
				confirmationOut2.setIsTransactionSuccessMessage(true);
				confirmationOut2.setTotalOutputs(getTotalOutputs());
				if (hasSentToOut2) {
					sendToOut2(confirmationOut2);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeIn1Timeout(number2);
					}
					if (sendFailed) {
						number2.setFailures(number2.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(number2, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.getMultiTasks().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.getMultiTasks().getExecutor().submit(consumer);
		activeRunnables = Collections.singletonMap(number2, future);	
		
		
		long timeout = number2.getTimeout() > 0 ? number2.getTimeout() : this.timeout;
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
	 * @param number2 original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeIn1Timeout(Number2 number2) throws Exception {
		return true;
	}
		
	public abstract void consumeIn1(Number2 number2) throws Exception;
	@Override
	public void consumeIn2WithNotifications(Number1 number1) {
		try {
			workflow.getMultiTasks().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = number1.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(number1.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		hasSentToOut1 = false;
		hasSentToOut2 = false;
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumeIn2(number1);
				
				// Send confirmation to Out1
				Number1 confirmationOut1 = new Number1();
				confirmationOut1.setCorrelationId(number1.getJobId());
				confirmationOut1.setIsTransactionSuccessMessage(true);
				confirmationOut1.setTotalOutputs(getTotalOutputs());
				if (hasSentToOut1) {
					sendToOut1(confirmationOut1);
				}
				
				// Send confirmation to Out2
				Number2 confirmationOut2 = new Number2();
				confirmationOut2.setCorrelationId(number1.getJobId());
				confirmationOut2.setIsTransactionSuccessMessage(true);
				confirmationOut2.setTotalOutputs(getTotalOutputs());
				if (hasSentToOut2) {
					sendToOut2(confirmationOut2);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeIn2Timeout(number1);
					}
					if (sendFailed) {
						number1.setFailures(number1.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(number1, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.getMultiTasks().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.getMultiTasks().getExecutor().submit(consumer);
		activeRunnables = Collections.singletonMap(number1, future);	
		
		
		long timeout = number1.getTimeout() > 0 ? number1.getTimeout() : this.timeout;
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
	 * @param number1 original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeIn2Timeout(Number1 number1) throws Exception {
		return true;
	}
		
	public abstract void consumeIn2(Number1 number1) throws Exception;

}

