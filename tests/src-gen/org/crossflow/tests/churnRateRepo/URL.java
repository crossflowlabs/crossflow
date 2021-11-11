package org.crossflow.tests.churnRateRepo;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2021-10-26T15:08:23.558892Z")
public class URL extends Job {
		
	protected String url;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public URL() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public URL(String url) {
		this.url = url;
	}
	
	 
	public URL(String url, Job correlation) {
		this.url = url;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getUrl();
		return ret;
	}
	
	public String toString() {
		return "URL (" + " url=" + url + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

