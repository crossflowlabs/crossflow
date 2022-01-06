package org.crossflow.tests.wordcount;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2022-01-05T17:43:35.469246Z")
public class WordFrequency extends Job {
		
	protected String word;
	protected int frequency;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public WordFrequency() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public WordFrequency(String word, int frequency) {
		this.word = word;
		this.frequency = frequency;
	}
	
	 
	public WordFrequency(String word, int frequency, Job correlation) {
		this.word = word;
		this.frequency = frequency;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public String getWord() {
		return this.word;
	}
	
	public void setWord(String word) {
		this.word = word;
	}
	
	public int getFrequency() {
		return this.frequency;
	}
	
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[2];
	 	ret[0] = getWord();
	 	ret[1] = getFrequency();
		return ret;
	}
	
	public String toString() {
		return "WordFrequency (" + " word=" + word + " frequency=" + frequency + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

