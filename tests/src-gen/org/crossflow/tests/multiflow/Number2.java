package org.crossflow.tests.multiflow;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2021-10-26T15:08:26.086642Z")
public class Number2 extends Job {
		
	protected int n2;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public Number2() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public Number2(int n2) {
		this.n2 = n2;
	}
	
	 
	public Number2(int n2, Job correlation) {
		this.n2 = n2;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public int getN2() {
		return this.n2;
	}
	
	public void setN2(int n2) {
		this.n2 = n2;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getN2();
		return ret;
	}
	
	public String toString() {
		return "Number2 (" + " n2=" + n2 + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

