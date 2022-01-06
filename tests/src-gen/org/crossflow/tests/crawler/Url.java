package org.crossflow.tests.crawler;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2022-01-05T17:43:31.905448Z")
public class Url extends Job {
		
	protected String location;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public Url() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public Url(String location) {
		this.location = location;
	}
	
	 
	public Url(String location, Job correlation) {
		this.location = location;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public String getLocation() {
		return this.location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getLocation();
		return ret;
	}
	
	public String toString() {
		return "Url (" + " location=" + location + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

