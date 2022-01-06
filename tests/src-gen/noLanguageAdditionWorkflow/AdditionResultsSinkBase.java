/** This class was automatically generated and should not be modified */
package noLanguageAdditionWorkflow;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2022-01-05T17:43:33.877651Z")
public abstract class AdditionResultsSinkBase extends Task  implements AdditionResultsConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final NoLanguageAdditionWorkflowTasks TASK = NoLanguageAdditionWorkflowTasks.ADDITION_RESULTS_SINK; 
	
	protected NoLanguageAdditionWorkflow workflow;
	
	
	public void setWorkflow(NoLanguageAdditionWorkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public NoLanguageAdditionWorkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public NoLanguageAdditionWorkflowTasks getTaskEnum() {
		return TASK;
	}

	@Override
	public void consumeAdditionResultsWithNotifications(Number number) {
			try {
				workflow.setTaskInProgess(this);
		
				// Perform the actual processing
				consumeAdditionResults(number);
				
				
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeAdditionResultsTimeout(number);
					}
					if (sendFailed) {
						number.setFailures(number.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(number, new Exception(ex), this));
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
	 * @param number original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeAdditionResultsTimeout(Number number) throws Exception {
		return true;
	}
		
	public abstract void consumeAdditionResults(Number number) throws Exception;

}

