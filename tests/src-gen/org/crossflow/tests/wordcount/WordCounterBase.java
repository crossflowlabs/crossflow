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
public abstract class WordCounterBase extends Task  implements LinesConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final WordCountWorkflowTasks TASK = WordCountWorkflowTasks.WORD_COUNTER; 
	
	protected WordCountWorkflow workflow;
	
 
	/* 
	 * WordFrequencies stream fields/methods
	 */
	protected WordFrequencies wordFrequencies;
	protected boolean hasSentToWordFrequencies = false;

	protected WordFrequencies getWordFrequencies() {
		return wordFrequencies;
	}

	protected void setWordFrequencies(WordFrequencies wordFrequencies) {
		this.wordFrequencies = wordFrequencies;
	}

	public void sendToWordFrequencies(WordFrequency wordFrequency) {
		wordFrequency.addRootId(activeRootIds);
		wordFrequency.setCacheable(cacheable);
		hasSentToWordFrequencies = true;
		getWordFrequencies().send(wordFrequency, TASK.getTaskName());
	}
	
	public int getTotalOutputs() {
		int count = 0;
		if (hasSentToWordFrequencies) count++;
		return count;
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

	@Override
	public void consumeLinesWithNotifications(Line line) {
		try {
			workflow.getWordCounters().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = line.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(line.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		hasSentToWordFrequencies = false;
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumeLines(line);
				
				// Send confirmation to WordFrequencies
				WordFrequency confirmationWordFrequencies = new WordFrequency();
				confirmationWordFrequencies.setCorrelationId(line.getJobId());
				confirmationWordFrequencies.setIsTransactionSuccessMessage(true);
				confirmationWordFrequencies.setTotalOutputs(getTotalOutputs());
				if (hasSentToWordFrequencies) {
					sendToWordFrequencies(confirmationWordFrequencies);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeLinesTimeout(line);
					}
					if (sendFailed) {
						line.setFailures(line.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(line, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.getWordCounters().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.getWordCounters().getExecutor().submit(consumer);
		activeRunnables = Collections.singletonMap(line, future);	
		
		
		long timeout = line.getTimeout() > 0 ? line.getTimeout() : this.timeout;
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
	 * @param line original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeLinesTimeout(Line line) throws Exception {
		return true;
	}
		
	public abstract void consumeLines(Line line) throws Exception;

}

