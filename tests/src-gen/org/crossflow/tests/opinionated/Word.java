package org.crossflow.tests.opinionated;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2021-10-26T15:08:26.810003Z")
public class Word extends Job {
		
	protected String w;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public Word() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public Word(String w) {
		this.w = w;
	}
	
	 
	public Word(String w, Job correlation) {
		this.w = w;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public String getW() {
		return this.w;
	}
	
	public void setW(String w) {
		this.w = w;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getW();
		return ret;
	}
	
	public String toString() {
		return "Word (" + " w=" + w + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

