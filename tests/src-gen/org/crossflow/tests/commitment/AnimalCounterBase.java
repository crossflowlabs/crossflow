/** This class was automatically generated and should not be modified */
package org.crossflow.tests.commitment;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2022-01-05T17:43:30.189489Z")
public abstract class AnimalCounterBase extends Task  implements AnimalsConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final CommitmentWorkflowTasks TASK = CommitmentWorkflowTasks.ANIMAL_COUNTER; 
	
	protected CommitmentWorkflow workflow;
	
 
	/* 
	 * Animals stream fields/methods
	 */
	protected Animals animals;
	protected boolean hasSentToAnimals = false;

	protected Animals getAnimals() {
		return animals;
	}

	protected void setAnimals(Animals animals) {
		this.animals = animals;
	}

	public void sendToAnimals(Animal animal) {
		animal.addRootId(activeRootIds);
		animal.setCacheable(cacheable);
		getAnimals().send(animal, TASK.getTaskName());
	}
	
	
	public void setWorkflow(CommitmentWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public CommitmentWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public CommitmentWorkflowTasks getTaskEnum() {
		return TASK;
	}

	@Override
	public void consumeAnimalsWithNotifications(Animal animal) {
		try {
			workflow.getAnimalCounters().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = animal.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(animal.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
				Animal result = consumeAnimals(animal);
				if(result != null) {
					if(isCacheable()) result.setCorrelationId(animal.getJobId());				
					result.setTransactional(false);
					sendToAnimals(result);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeAnimalsTimeout(animal);
					}
					if (sendFailed) {
						animal.setFailures(animal.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(animal, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.getAnimalCounters().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.getAnimalCounters().getExecutor().submit(consumer);
		activeRunnables = Collections.singletonMap(animal, future);	
		
		
		long timeout = animal.getTimeout() > 0 ? animal.getTimeout() : this.timeout;
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
	 * @param animal original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeAnimalsTimeout(Animal animal) throws Exception {
		return true;
	}
		
	public abstract Animal consumeAnimals(Animal animal) throws Exception;

}

