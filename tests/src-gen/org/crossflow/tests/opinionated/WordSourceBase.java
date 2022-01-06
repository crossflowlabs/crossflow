/** This class was automatically generated and should not be modified */
package org.crossflow.tests.opinionated;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2022-01-05T17:43:34.159906Z")
public abstract class WordSourceBase extends Task {

	/**
	 * Enum Identifier of this Task
	 */
	public static final OpinionatedWorkflowTasks TASK = OpinionatedWorkflowTasks.WORD_SOURCE; 
	
	protected OpinionatedWorkflow workflow;
	
 
	/* 
	 * Words stream fields/methods
	 */
	protected Words words;
	protected boolean hasSentToWords = false;

	protected Words getWords() {
		return words;
	}

	protected void setWords(Words words) {
		this.words = words;
	}

	public void sendToWords(Word word) {
		word.addRootId(activeRootIds);
		word.setCacheable(cacheable);
		word.setTransactional(false);
		getWords().send(word, TASK.getTaskName());
	}
	
	
	public void setWorkflow(OpinionatedWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public OpinionatedWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public OpinionatedWorkflowTasks getTaskEnum() {
		return TASK;
	}

	public abstract void produce() throws Exception;
	

}

