package org.crossflow.tests.configurable.addition;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2021-10-26T15:08:24.624249Z")
public class Post extends Job {
		
	protected String post;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public Post() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public Post(String post) {
		this.post = post;
	}
	
	 
	public Post(String post, Job correlation) {
		this.post = post;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public String getPost() {
		return this.post;
	}
	
	public void setPost(String post) {
		this.post = post;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getPost();
		return ret;
	}
	
	public String toString() {
		return "Post (" + " post=" + post + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

