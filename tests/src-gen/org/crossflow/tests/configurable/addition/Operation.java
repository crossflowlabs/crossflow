package org.crossflow.tests.configurable.addition;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2022-01-05T17:43:31.251449Z")
public class Operation extends Job {
		
	protected String operation;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public Operation() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public Operation(String operation) {
		this.operation = operation;
	}
	
	 
	public Operation(String operation, Job correlation) {
		this.operation = operation;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public String getOperation() {
		return this.operation;
	}
	
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getOperation();
		return ret;
	}
	
	public String toString() {
		return "Operation (" + " operation=" + operation + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

