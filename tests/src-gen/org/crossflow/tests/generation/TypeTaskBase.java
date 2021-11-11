/** This class was automatically generated and should not be modified */
package org.crossflow.tests.generation;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2021-10-26T15:08:27.785747Z")
public abstract class TypeTaskBase extends Task  implements AConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final TypeGenerationWorkflowTasks TASK = TypeGenerationWorkflowTasks.TYPE_TASK; 
	
	protected TypeGenerationWorkflow workflow;
	
 
	/* 
	 * A stream fields/methods
	 */
	protected A a;
	protected boolean hasSentToA = false;

	protected A getA() {
		return a;
	}

	protected void setA(A a) {
		this.a = a;
	}

	public void sendToA(GeneratedType generatedType) {
		generatedType.addRootId(activeRootIds);
		generatedType.setCacheable(cacheable);
		getA().send(generatedType, TASK.getTaskName());
	}
	
	
	public void setWorkflow(TypeGenerationWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public TypeGenerationWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public TypeGenerationWorkflowTasks getTaskEnum() {
		return TASK;
	}

	@Override
	public void consumeAWithNotifications(GeneratedType generatedType) {
		try {
			workflow.getTypeTasks().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = generatedType.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(generatedType.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
				GeneratedType result = consumeA(generatedType);
				if(result != null) {
					if(isCacheable()) result.setCorrelationId(generatedType.getJobId());				
					result.setTransactional(false);
					sendToA(result);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeATimeout(generatedType);
					}
					if (sendFailed) {
						generatedType.setFailures(generatedType.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(generatedType, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.getTypeTasks().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.getTypeTasks().getExecutor().submit(consumer);
		activeRunnables = Collections.singletonMap(generatedType, future);	
		
		
		long timeout = generatedType.getTimeout() > 0 ? generatedType.getTimeout() : this.timeout;
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
	 * @param generatedType original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeATimeout(GeneratedType generatedType) throws Exception {
		return true;
	}
		
	public abstract GeneratedType consumeA(GeneratedType generatedType) throws Exception;

}

