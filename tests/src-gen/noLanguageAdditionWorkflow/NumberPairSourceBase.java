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

@Generated(value = "org.crossflow.java.Task2BaseClass", date = "2021-10-26T15:08:26.572309Z")
public abstract class NumberPairSourceBase extends Task {

	/**
	 * Enum Identifier of this Task
	 */
	public static final NoLanguageAdditionWorkflowTasks TASK = NoLanguageAdditionWorkflowTasks.NUMBER_PAIR_SOURCE; 
	
	protected NoLanguageAdditionWorkflow workflow;
	
 
	/* 
	 * Additions stream fields/methods
	 */
	protected Additions additions;
	protected boolean hasSentToAdditions = false;

	protected Additions getAdditions() {
		return additions;
	}

	protected void setAdditions(Additions additions) {
		this.additions = additions;
	}

	public void sendToAdditions(NumberPair numberPair) {
		numberPair.addRootId(activeRootIds);
		numberPair.setCacheable(cacheable);
		numberPair.setTransactional(false);
		getAdditions().send(numberPair, TASK.getTaskName());
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

	public abstract void produce() throws Exception;
	

}

