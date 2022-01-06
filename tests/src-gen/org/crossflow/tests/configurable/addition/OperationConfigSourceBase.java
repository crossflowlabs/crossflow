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

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2022-01-05T17:43:31.251449Z")
public abstract class OperationConfigSourceBase extends Task {

	/**
	 * Enum Identifier of this Task
	 */
	public static final AdditionWorkflowTasks TASK = AdditionWorkflowTasks.OPERATION_CONFIG_SOURCE; 
	
	protected AdditionWorkflow workflow;
	
 
	/* 
	 * OperationConfigTopic stream fields/methods
	 */
	protected OperationConfigTopic operationConfigTopic;
	protected boolean hasSentToOperationConfigTopic = false;

	protected OperationConfigTopic getOperationConfigTopic() {
		return operationConfigTopic;
	}

	protected void setOperationConfigTopic(OperationConfigTopic operationConfigTopic) {
		this.operationConfigTopic = operationConfigTopic;
	}

	public void sendToOperationConfigTopic(Operation operation) {
		operation.addRootId(activeRootIds);
		operation.setCacheable(cacheable);
		operation.setTransactional(false);
		getOperationConfigTopic().send(operation, TASK.getTaskName());
	}
	
	
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

	public abstract void produce() throws Exception;
	

}

