package org.crossflow.tests.multiflow;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2022-01-05T17:43:33.257764Z")
public class Number1 extends Job {
		
	protected int n1;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public Number1() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public Number1(int n1) {
		this.n1 = n1;
	}
	
	 
	public Number1(int n1, Job correlation) {
		this.n1 = n1;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public int getN1() {
		return this.n1;
	}
	
	public void setN1(int n1) {
		this.n1 = n1;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getN1();
		return ret;
	}
	
	public String toString() {
		return "Number1 (" + " n1=" + n1 + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

