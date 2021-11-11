package org.crossflow.tests.calculator;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2021-10-26T15:08:22.866352Z")
public class Calculation extends Job {
		
	protected int a;
	protected int b;
	protected String operator;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public Calculation() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public Calculation(int a, int b, String operator) {
		this.a = a;
		this.b = b;
		this.operator = operator;
	}
	
	 
	public Calculation(int a, int b, String operator, Job correlation) {
		this.a = a;
		this.b = b;
		this.operator = operator;
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
	
	public String getOperator() {
		return this.operator;
	}
	
	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[3];
	 	ret[0] = getA();
	 	ret[1] = getB();
	 	ret[2] = getOperator();
		return ret;
	}
	
	public String toString() {
		return "Calculation (" + " a=" + a + " b=" + b + " operator=" + operator + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

