package org.crossflow.tests.python;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2021-10-26T15:08:27.215083Z")
public class TypeA extends Job {
		
	protected String foo;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public TypeA() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public TypeA(String foo) {
		this.foo = foo;
	}
	
	 
	public TypeA(String foo, Job correlation) {
		this.foo = foo;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public String getFoo() {
		return this.foo;
	}
	
	public void setFoo(String foo) {
		this.foo = foo;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getFoo();
		return ret;
	}
	
	public String toString() {
		return "TypeA (" + " foo=" + foo + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

