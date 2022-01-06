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
public abstract class AdderBase extends Task  implements AdditionsConsumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final NoLanguageAdditionWorkflowTasks TASK = NoLanguageAdditionWorkflowTasks.ADDER; 
	
	protected NoLanguageAdditionWorkflow workflow;
	
 
	/* 
	 * AdditionResults stream fields/methods
	 */
	protected AdditionResults additionResults;
	protected boolean hasSentToAdditionResults = false;

	protected AdditionResults getAdditionResults() {
		return additionResults;
	}

	protected void setAdditionResults(AdditionResults additionResults) {
		this.additionResults = additionResults;
	}

	public void sendToAdditionResults(Number number) {
		number.addRootId(activeRootIds);
		number.setCacheable(cacheable);
		getAdditionResults().send(number, TASK.getTaskName());
	}
	
	
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
	public void consumeAdditionsWithNotifications(NumberPair numberPair) {
		try {
			workflow.getAdders().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = numberPair.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(numberPair.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
				Number result = consumeAdditions(numberPair);
				if(result != null) {
					if(isCacheable()) result.setCorrelationId(numberPair.getJobId());				
					result.setTransactional(false);
					sendToAdditionResults(result);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeAdditionsTimeout(numberPair);
					}
					if (sendFailed) {
						numberPair.setFailures(numberPair.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(numberPair, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.getAdders().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.getAdders().getExecutor().submit(consumer);
		activeRunnables = Collections.singletonMap(numberPair, future);	
		
		
		long timeout = numberPair.getTimeout() > 0 ? numberPair.getTimeout() : this.timeout;
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
	 * @param numberPair original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeAdditionsTimeout(NumberPair numberPair) throws Exception {
		return true;
	}
		
	public abstract Number consumeAdditions(NumberPair numberPair) throws Exception;

}

