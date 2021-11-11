package org.crossflow.tests.ack;

import javax.annotation.Generated;

/**
 * Specification of available tasks in this workflow as Enumerates types.
 * <p>
 * Each Enum is comprised:
 * <ul>
 * <li>The original task name as defined in the workflow model</li>
 * <li>boolean flag denoting whether it is a Java task or not</li>
 * </ul>
 * </p>
 */
@Generated(value = "org.crossflow.java.Workflow2TaskEnum", date = "2021-10-26T15:08:21.360808Z")
public enum AckWorkflowTasks {
	
	SOURCE("Source", true),
	PROCESSING_TASK("ProcessingTask", true),
	SINK("Sink", true);
	
	private final String taskName;
	private final boolean isJavaTask;

	private AckWorkflowTasks(String taskName, boolean isJavaTask) {
		this.taskName = taskName;
		this.isJavaTask = isJavaTask;
	}
	
	/**
	 * Gets the task name as defined in the original workflow model
	 * 
	 * @return the task name
	 */
	public String getTaskName() {
		return this.taskName;
	}
	
	/**
	 * @return {@code true} if the task has a corresponding Java class,
	 *         {@code false} otherwise
	 */
	public boolean isJavaTask() {
		return this.isJavaTask;
	}
	
	@Override
	public String toString() {
		return getTaskName();
	}
		
	/**
	 * Reverse lookup function to retrieve the corresponding Enum from
	 * the original task name
	 *
	 * @return the corresponding enum from the task name
	 * @throws IllegalArgumentException if no such enum exists
	 */
	public static AckWorkflowTasks fromTaskName(String taskName) {
		for (AckWorkflowTasks t : values()) {
			if (t.taskName.equals(taskName)) return t;
		}
		throw new IllegalArgumentException("No AckWorkflowTasks exist with taskName " + taskName);
	}
	
}