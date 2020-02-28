package org.crossflow.tests.util;

import java.util.ArrayList;
import java.util.List;

import org.crossflow.runtime.BuiltinStreamConsumer;

public class BuiltinStreamRecorder<T> implements BuiltinStreamConsumer<T> {
	
	protected List<T> recorded = new ArrayList<>();
	
	@Override
	public void consume(T t) {
		recorded.add(t);
	}

	public List<T> getRecorded() {
		return recorded;
	}
	
}
