package org.crossflow.runtime;

import java.util.List;

public interface Cache {

	List<Job> getCachedOutputs(Job input);

	boolean hasCachedOutputs(Job input);

	void cache(Job output);

	void setWorkflow(Workflow<?> workflow);

	void cacheTransactionally(Job output);

	boolean clear();

	boolean clear(String queueName);

}
