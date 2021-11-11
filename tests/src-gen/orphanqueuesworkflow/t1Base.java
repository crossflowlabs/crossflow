/** This class was automatically generated and should not be modified */
package orphanqueuesworkflow;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.BuiltinStream;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.Task;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2021-10-26T15:08:27.076721Z")
public abstract class t1Base extends Task  implements q1Consumer{

	/**
	 * Enum Identifier of this Task
	 */
	public static final orphanqueuesworkflowTasks TASK = orphanqueuesworkflowTasks.T1; 
	
	protected orphanqueuesworkflow workflow;
	
 
	/* 
	 * q2 stream fields/methods
	 */
	protected BuiltinStream<ty1> q2;
	protected boolean hasSentToq2 = false;

	protected BuiltinStream<ty1> getq2() {
		return q2;
	}

	protected void setq2(BuiltinStream<ty1> q2) {
		this.q2 = q2;
	}

	public void sendToq2(ty1 ty1) throws Exception {
		ty1.addRootId(activeRootIds);
		ty1.setCacheable(cacheable);
		getq2().send(ty1);
	}
	
	
	public void setWorkflow(orphanqueuesworkflow workflow) {
		this.workflow = workflow;
	}

	@Override
	public orphanqueuesworkflow getWorkflow() {
		return workflow;
	}
	
	@Override
	public String getName() {
		return TASK.getTaskName();
	}

	public orphanqueuesworkflowTasks getTaskEnum() {
		return TASK;
	}

	@Override
	public void consumeq1WithNotifications(ty1 ty1) {
		try {
			workflow.gett1s().getSemaphore().acquire();
			
			// Task Instance State
			activeRootIds = ty1.getRootIds();
			if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(ty1.getJobId());
		} catch (Exception e) {
			workflow.reportInternalException(e);
		}
		
		Runnable consumer = () -> {		
			try {
				workflow.setTaskInProgess(this);
				ty1 result = consumeq1(ty1);
				if(result != null) {
					if(isCacheable()) result.setCorrelationId(ty1.getJobId());				
					result.setTransactional(false);
					sendToq2(result);
				}
	
			} catch (Throwable ex) {
				try {
					boolean sendFailed = true;
					if (ex instanceof InterruptedException) {
						sendFailed = onConsumeq1Timeout(ty1);
					}
					if (sendFailed) {
						ty1.setFailures(ty1.getFailures()+1);
						workflow.getFailedJobsTopic().send(new FailedJob(ty1, new Exception(ex), this));
					}
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			} finally {
				try {
					// cleanup instance
					activeRootIds = Collections.emptySet();
					activeRunnables = Collections.emptyMap();
					
					workflow.gett1s().getSemaphore().release();
					workflow.setTaskWaiting(this);
				} catch (Exception e) {
					workflow.reportInternalException(e);
				}
			}
			
		};
		
		//track current job execution (for cancellation)
		ListenableFuture<?> future = workflow.gett1s().getExecutor().submit(consumer);
		activeRunnables = Collections.singletonMap(ty1, future);	
		
		
		long timeout = ty1.getTimeout() > 0 ? ty1.getTimeout() : this.timeout;
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
	 * @param ty1 original input
	 * @return {@code true} if a failed job should be registered, {@code false}
	 * otherwise.
	 */
	public boolean onConsumeq1Timeout(ty1 ty1) throws Exception {
		return true;
	}
		
	public abstract ty1 consumeq1(ty1 ty1) throws Exception;

}

