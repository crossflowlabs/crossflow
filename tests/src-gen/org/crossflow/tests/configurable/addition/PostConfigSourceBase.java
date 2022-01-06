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
public abstract class PostConfigSourceBase extends Task {

	/**
	 * Enum Identifier of this Task
	 */
	public static final AdditionWorkflowTasks TASK = AdditionWorkflowTasks.POST_CONFIG_SOURCE; 
	
	protected AdditionWorkflow workflow;
	
 
	/* 
	 * PostConfigTopic stream fields/methods
	 */
	protected PostConfigTopic postConfigTopic;
	protected boolean hasSentToPostConfigTopic = false;

	protected PostConfigTopic getPostConfigTopic() {
		return postConfigTopic;
	}

	protected void setPostConfigTopic(PostConfigTopic postConfigTopic) {
		this.postConfigTopic = postConfigTopic;
	}

	public void sendToPostConfigTopic(Post post) {
		post.addRootId(activeRootIds);
		post.setCacheable(cacheable);
		post.setTransactional(false);
		getPostConfigTopic().send(post, TASK.getTaskName());
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

