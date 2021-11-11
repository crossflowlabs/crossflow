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

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2021-10-26T15:08:27.918003Z")
public abstract class WordCountSinkBase extends Task  implements WordFrequenciesConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final WordCountWorkflowTasks TASK = WordCountWorkflowTasks.WORD_COUNT_SINK; 
	
	protected WordCountWorkflow workflow;
	
	
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

	@Override
	public void consumeWordFrequenciesWithNotifications(WordFrequency wordFrequency) {
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumeWordFrequencies(wordFrequency);
				
				
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeWordFrequenciesTimeout(wordFrequency);
					}
					if (sendFailed) {
						wordFrequency.setFailures(wordFrequency.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(wordFrequency, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
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
	 * @param wordFrequency original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeWordFrequenciesTimeout(WordFrequency wordFrequency) throws Exception {
		return true;
	}
		
	public abstract void consumeWordFrequencies(WordFrequency wordFrequency) throws Exception;

}

