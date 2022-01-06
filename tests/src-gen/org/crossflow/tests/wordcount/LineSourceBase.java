/** This class was automatically generated and should not be modified */
package org.crossflow.tests.wordcount;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2022-01-05T17:43:35.469246Z")
public abstract class LineSourceBase extends Task {

	/**
	 * Enum Identifier of this Task
	 */
	public static final WordCountWorkflowTasks TASK = WordCountWorkflowTasks.LINE_SOURCE; 
	
	protected WordCountWorkflow workflow;
	
 
	/* 
	 * Lines stream fields/methods
	 */
	protected Lines lines;
	protected boolean hasSentToLines = false;

	protected Lines getLines() {
		return lines;
	}

	protected void setLines(Lines lines) {
		this.lines = lines;
	}

	public void sendToLines(Line line) {
		line.addRootId(activeRootIds);
		line.setCacheable(cacheable);
		line.setTransactional(false);
		getLines().send(line, TASK.getTaskName());
	}
	
	
	public void setWorkflow(WordCountWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public WordCountWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public WordCountWorkflowTasks getTaskEnum() {
		return TASK;
	}

	public abstract void produce() throws Exception;
	

}

