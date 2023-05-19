package org.crossflow.runtime;

import java.util.Collection;

public interface Stream {
	
	void stop() throws Exception;

	boolean isBroadcast();
	
	Collection<String> getDestinationNames();
	
}
