package org.crossflow.tests.multisource;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2022-01-05T17:43:33.629524Z")
public class StringElement extends Job {
		
	protected String value;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public StringElement() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public StringElement(String value) {
		this.value = value;
	}
	
	 
	public StringElement(String value, Job correlation) {
		this.value = value;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getValue();
		return ret;
	}
	
	public String toString() {
		return "StringElement (" + " value=" + value + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

