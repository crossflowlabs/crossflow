package org.crossflow.tests.commitment;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.Generated;

import org.crossflow.runtime.FailedJob;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

@Generated(value = "org.crossflow.java.CommitmentTask2BaseClass", date = "2021-10-26T15:08:23.869383Z")
public abstract class CommitmentAnimalCounterBase extends AnimalCounterBase {
	
	protected volatile int rejections = 0;
	protected volatile int occurences = 0;
	protected Map<String,Integer> seen = new ConcurrentHashMap<>();
	protected Set<String> commitments = ConcurrentHashMap.newKeySet();	
	
	
	
@Override
public void consumeAnimalsWithNotifications(Animal animal) {
	try {
		workflow.getAnimalCounters().getSemaphore().acquire();
		
		// Task Instance State
		activeRootIds = animal.getRootIds();
		if (activeRootIds.isEmpty()) activeRootIds = Collections.singleton(animal.getJobId());
	} catch (Exception e) {
		workflow.reportInternalException(e);
	}
	
	Runnable consumer = () -> {		
		try {
			workflow.setTaskInProgess(this);
				Animal result = null;
				if (commitments.contains(animal.getName())) {
					occurences++;
					result = consumeAnimals(animal);
				} else if (seen.containsKey(animal.getJobId()) && seen.get(animal.getJobId())>=1) {
					commitments.add(animal.getJobId());
					result = consumeAnimals(animal);
				} else {
					if(!seen.containsKey(animal.getJobId()))
						seen.put(animal.getJobId(),1);
					seen.put(animal.getJobId(),seen.get(animal.getJobId())+1);
					rejections++;
					workflow.getAnimals().send(animal,this.getClass().getName());
				}			
				if(result != null){
					result.setTransactional(false);
					sendToAnimals(result);
				}

		} catch (Throwable ex) {
			try {
				boolean sendFailed = true;
				if (ex instanceof InterruptedException) {
					sendFailed = onConsumeAnimalsTimeout(animal);
				}
				if (sendFailed) {
					animal.setFailures(animal.getFailures()+1);
					workflow.getFailedJobsTopic().send(new FailedJob(animal, new Exception(ex), this));
				}
			} catch (Exception e) {
				workflow.reportInternalException(e);
			}
		} finally {
			try {
				// cleanup instance
				activeRootIds = Collections.emptySet();
				activeRunnables = Collections.emptyMap();
				
				workflow.getAnimalCounters().getSemaphore().release();
				workflow.setTaskWaiting(this);
			} catch (Exception e) {
				workflow.reportInternalException(e);
			}
		}
		
	};
	
	//track current job execution (for cancellation)
	ListenableFuture<?> future = workflow.getAnimalCounters().getExecutor().submit(consumer);
	activeRunnables = Collections.singletonMap(animal, future);	
	
	
	long timeout = animal.getTimeout() > 0 ? animal.getTimeout() : this.timeout;
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
 * @param animal original input
 * @return {@code true} if a failed job should be registered, {@code false}
 * otherwise.
 */
public boolean onConsumeAnimalsTimeout(Animal animal) throws Exception {
	return true;
}
	
public abstract Animal consumeAnimals(Animal animal) throws Exception;

	public int getRejections() {
		return rejections;
	}

	public int getOccurences() {
		return occurences;
	}
	
	public int getCommitmentSize() {
		return commitments.size();
	}

}