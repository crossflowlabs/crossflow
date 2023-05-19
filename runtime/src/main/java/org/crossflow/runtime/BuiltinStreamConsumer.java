package org.crossflow.runtime;

public interface BuiltinStreamConsumer<T> {
	
	void consume(T t) throws Exception;
	
}
