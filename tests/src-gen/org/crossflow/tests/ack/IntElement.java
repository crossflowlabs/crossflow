package org.crossflow.tests.ack;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2021-10-26T15:08:21.360808Z")
public class IntElement extends Job {
		
	protected int value;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public IntElement() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public IntElement(int value) {
		this.value = value;
	}
	
	 
	public IntElement(int value, Job correlation) {
		this.value = value;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public int getValue() {
		return this.value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getValue();
		return ret;
	}
	
	public String toString() {
		return "IntElement (" + " value=" + value + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

