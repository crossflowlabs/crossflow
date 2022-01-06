package org.crossflow.tests.churnRateRepo;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2022-01-05T17:43:29.814671Z")
public class ChurnRate extends Job {
		
	protected String url;
	protected String branch;
	protected Double churnRate;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public ChurnRate() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public ChurnRate(String url, String branch, Double churnRate) {
		this.url = url;
		this.branch = branch;
		this.churnRate = churnRate;
	}
	
	 
	public ChurnRate(String url, String branch, Double churnRate, Job correlation) {
		this.url = url;
		this.branch = branch;
		this.churnRate = churnRate;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getBranch() {
		return this.branch;
	}
	
	public void setBranch(String branch) {
		this.branch = branch;
	}
	
	public Double getChurnRate() {
		return this.churnRate;
	}
	
	public void setChurnRate(Double churnRate) {
		this.churnRate = churnRate;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[3];
	 	ret[0] = getUrl();
	 	ret[1] = getBranch();
	 	ret[2] = getChurnRate();
		return ret;
	}
	
	public String toString() {
		return "ChurnRate (" + " url=" + url + " branch=" + branch + " churnRate=" + churnRate + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

