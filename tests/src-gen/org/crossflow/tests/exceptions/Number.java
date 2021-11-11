package org.crossflow.tests.exceptions;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2021-10-26T15:08:25.358431Z")
public class Number extends Job {
		
	protected int n;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public Number() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public Number(int n) {
		this.n = n;
	}
	
	 
	public Number(int n, Job correlation) {
		this.n = n;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public int getN() {
		return this.n;
	}
	
	public void setN(int n) {
		this.n = n;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getN();
		return ret;
	}
	
	public String toString() {
		return "Number (" + " n=" + n + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

