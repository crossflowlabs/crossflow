package org.crossflow.tests.concurrency;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2021-10-26T15:08:24.113079Z")
public class Result extends SleepTime {
		
	protected boolean completed;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public Result() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public Result(boolean completed, int seconds) {
		this.completed = completed;
		this.seconds = seconds;
	}
	
	 
	public Result(boolean completed, int seconds, Job correlation) {
		this.completed = completed;
		this.seconds = seconds;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public Result(SleepTime sleepTime) {
		this.setSeconds(sleepTime.getSeconds());
		this.correlationId = sleepTime.getJobId();
	}
		
	
	public boolean getCompleted() {
		return this.completed;
	}
	
	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getCompleted();
		return ret;
	}
	
	public String toString() {
		return "Result (" + " completed=" + completed + " getSeconds()=" + getSeconds() + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

