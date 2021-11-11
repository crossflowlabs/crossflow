package org.crossflow.tests.configurable.addition;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2021-10-26T15:08:24.624249Z")
public class NumberPair extends Job {
		
	protected int a;
	protected int b;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public NumberPair() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public NumberPair(int a, int b) {
		this.a = a;
		this.b = b;
	}
	
	 
	public NumberPair(int a, int b, Job correlation) {
		this.a = a;
		this.b = b;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public int getA() {
		return this.a;
	}
	
	public void setA(int a) {
		this.a = a;
	}
	
	public int getB() {
		return this.b;
	}
	
	public void setB(int b) {
		this.b = b;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[2];
	 	ret[0] = getA();
	 	ret[1] = getB();
		return ret;
	}
	
	public String toString() {
		return "NumberPair (" + " a=" + a + " b=" + b + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

