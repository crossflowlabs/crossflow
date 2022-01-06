package org.crossflow.tests.wordcount;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2022-01-05T17:43:35.469246Z")
public class Line extends Job {
		
	protected String text;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public Line() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public Line(String text) {
		this.text = text;
	}
	
	 
	public Line(String text, Job correlation) {
		this.text = text;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public String getText() {
		return this.text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getText();
		return ret;
	}
	
	public String toString() {
		return "Line (" + " text=" + text + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

