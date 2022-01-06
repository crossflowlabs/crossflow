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
public abstract class AnimalSourceBase extends Task {

	/**
	 * Enum Identifier of this Task
	 */
	public static final CommitmentWorkflowTasks TASK = CommitmentWorkflowTasks.ANIMAL_SOURCE; 
	
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
		animal.setTransactional(false);
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

	public abstract void produce() throws Exception;
	

}

