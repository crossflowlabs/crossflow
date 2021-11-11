/** This class was automatically generated and should not be modified */
package org.crossflow.tests.configurable.addition;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2021-10-26T15:08:24.624249Z")
public abstract class AdderBase extends Task  implements AdditionsConsumer,OperationConfigTopicConsumer,PostConfigTopicConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final AdditionWorkflowTasks TASK = AdditionWorkflowTasks.ADDER; 
	
	protected AdditionWorkflow workflow;
	
 
	/* 
	 * AdditionResults stream fields/methods
	 */
	protected AdditionResults additionResults;
	protected boolean hasSentToAdditionResults = false;

	protected AdditionResults getAdditionResults() {
		return additionResults;
	}

	protected void setAdditionResults(AdditionResults additionResults) {
		this.additionResults = additionResults;
	}

	public void sendToAdditionResults(Number number) {
		number.addRootId(activeRootIds);
		number.setCacheable(cacheable);
		getAdditionResults().send(number, TASK.getTaskName());
	}
	
	// Configuration Received Flags
	boolean hasProcessedOperationConfigTopic = false;
	boolean hasProcessedPostConfigTopic = false;
	
	public void setWorkflow(AdditionWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public AdditionWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public AdditionWorkflowTasks getTaskEnum() {
		return TASK;
	}

	@Override
	public void consumeAdditionsWithNotifications(NumberPair numberPair) {
		// Await configuration to be processed
		while(!hasProcessedOperationConfigTopic ||!hasProcessedPostConfigTopic) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				workflow.reportInternalException(e);
			}
		}
			
		try {
			workflow.getAdders().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = numberPair.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(numberPair.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
				Number result = consumeAdditions(numberPair);
				if(result != null) {
					if(isCacheable()) result.setCorrelationId(numberPair.getJobId());				
					result.setTransactional(false);
					sendToAdditionResults(result);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeAdditionsTimeout(numberPair);
					}
					if (sendFailed) {
						numberPair.setFailures(numberPair.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(numberPair, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.getAdders().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.getAdders().getExecutor().submit(consumer);
		activeRunnables = Collections.singletonMap(numberPair, future);	
		
		
		long timeout = numberPair.getTimeout() > 0 ? numberPair.getTimeout() : this.timeout;
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
	 * @param numberPair original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeAdditionsTimeout(NumberPair numberPair) throws Exception {
		return true;
	}
		
	public abstract Number consumeAdditions(NumberPair numberPair) throws Exception;
	@Override
	public void consumeOperationConfigTopicWithNotifications(Operation operation) {
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumeOperationConfigTopic(operation);
				
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeOperationConfigTopicTimeout(operation);
					}
					if (sendFailed) {
						operation.setFailures(operation.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(operation, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					hasProcessedOperationConfigTopic = true;
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
	 * @param operation original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeOperationConfigTopicTimeout(Operation operation) throws Exception {
		return true;
	}
		
	public abstract void consumeOperationConfigTopic(Operation operation) throws Exception;
	@Override
	public void consumePostConfigTopicWithNotifications(Post post) {
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumePostConfigTopic(post);
				
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumePostConfigTopicTimeout(post);
					}
					if (sendFailed) {
						post.setFailures(post.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(post, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					hasProcessedPostConfigTopic = true;
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
	 * @param post original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumePostConfigTopicTimeout(Post post) throws Exception {
		return true;
	}
		
	public abstract void consumePostConfigTopic(Post post) throws Exception;

}

