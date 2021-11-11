package org.crossflow.tests.transactionalcaching;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2021-10-26T15:08:27.566934Z")
public class Element extends Job {
		
	protected String s;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public Element() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public Element(String s) {
		this.s = s;
	}
	
	 
	public Element(String s, Job correlation) {
		this.s = s;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public String getS() {
		return this.s;
	}
	
	public void setS(String s) {
		this.s = s;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getS();
		return ret;
	}
	
	public String toString() {
		return "Element (" + " s=" + s + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

