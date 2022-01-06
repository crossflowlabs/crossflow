package org.crossflow.tests.opinionated;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2022-01-05T17:43:34.159906Z")
public class Occs extends Job {
		
	protected Integer i;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public Occs() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public Occs(Integer i) {
		this.i = i;
	}
	
	 
	public Occs(Integer i, Job correlation) {
		this.i = i;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public Integer getI() {
		return this.i;
	}
	
	public void setI(Integer i) {
		this.i = i;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getI();
		return ret;
	}
	
	public String toString() {
		return "Occs (" + " i=" + i + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

