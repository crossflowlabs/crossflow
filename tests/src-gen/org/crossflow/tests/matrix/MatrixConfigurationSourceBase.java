/** This class was automatically generated and should not be modified */
package org.crossflow.tests.matrix;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2022-01-05T17:43:32.621761Z")
public abstract class MatrixConfigurationSourceBase extends Task {

	/**
	 * Enum Identifier of this Task
	 */
	public static final MatrixWorkflowTasks TASK = MatrixWorkflowTasks.MATRIX_CONFIGURATION_SOURCE; 
	
	protected MatrixWorkflow workflow;
	
 
	/* 
	 * MatrixConfigurations stream fields/methods
	 */
	protected MatrixConfigurations matrixConfigurations;
	protected boolean hasSentToMatrixConfigurations = false;

	protected MatrixConfigurations getMatrixConfigurations() {
		return matrixConfigurations;
	}

	protected void setMatrixConfigurations(MatrixConfigurations matrixConfigurations) {
		this.matrixConfigurations = matrixConfigurations;
	}

	public void sendToMatrixConfigurations(MatrixConfiguration matrixConfiguration) {
		matrixConfiguration.addRootId(activeRootIds);
		matrixConfiguration.setCacheable(cacheable);
		matrixConfiguration.setTransactional(false);
		getMatrixConfigurations().send(matrixConfiguration, TASK.getTaskName());
	}
	
	
	public void setWorkflow(MatrixWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public MatrixWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public MatrixWorkflowTasks getTaskEnum() {
		return TASK;
	}

	public abstract void produce() throws Exception;
	

}

