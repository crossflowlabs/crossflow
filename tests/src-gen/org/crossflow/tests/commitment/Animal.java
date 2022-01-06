package org.crossflow.tests.commitment;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2022-01-05T17:43:30.189489Z")
public class Animal extends Job {
		
	protected String name;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public Animal() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public Animal(String name) {
		this.name = name;
	}
	
	 
	public Animal(String name, Job correlation) {
		this.name = name;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getName();
		return ret;
	}
	
	public String toString() {
		return "Animal (" + " name=" + name + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

