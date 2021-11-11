package org.crossflow.tests.concurrency;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2021-10-26T15:08:24.113079Z")
public class SleepTime extends Job {
		
	protected int seconds;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public SleepTime() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public SleepTime(int seconds) {
		this.seconds = seconds;
	}
	
	 
	public SleepTime(int seconds, Job correlation) {
		this.seconds = seconds;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public int getSeconds() {
		return this.seconds;
	}
	
	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getSeconds();
		return ret;
	}
	
	public String toString() {
		return "SleepTime (" + " seconds=" + seconds + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

