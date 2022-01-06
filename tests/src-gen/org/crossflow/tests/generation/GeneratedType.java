package org.crossflow.tests.generation;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2022-01-05T17:43:35.307113Z")
public class GeneratedType extends Job {
		
	protected String strProp;
	protected int intProp;
	protected Collection<String> manyStrProp = new ArrayList<String>();
	protected Collection<Integer> manyIntProp = new ArrayList<Integer>();
	protected EnumProp enumProp;
	protected Collection<ManyEnumProp> manyEnumProp = new ArrayList<ManyEnumProp>();
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public GeneratedType() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public GeneratedType(String strProp, int intProp, Collection<String> manyStrProp, Collection<Integer> manyIntProp, EnumProp enumProp, Collection<ManyEnumProp> manyEnumProp) {
		this.strProp = strProp;
		this.intProp = intProp;
		this.manyStrProp = manyStrProp;
		this.manyIntProp = manyIntProp;
		this.enumProp = enumProp;
		this.manyEnumProp = manyEnumProp;
	}
	
	 
	public GeneratedType(String strProp, int intProp, Collection<String> manyStrProp, Collection<Integer> manyIntProp, EnumProp enumProp, Collection<ManyEnumProp> manyEnumProp, Job correlation) {
		this.strProp = strProp;
		this.intProp = intProp;
		this.manyStrProp = manyStrProp;
		this.manyIntProp = manyIntProp;
		this.enumProp = enumProp;
		this.manyEnumProp = manyEnumProp;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public String getStrProp() {
		return this.strProp;
	}
	
	public void setStrProp(String strProp) {
		this.strProp = strProp;
	}
	
	public int getIntProp() {
		return this.intProp;
	}
	
	public void setIntProp(int intProp) {
		this.intProp = intProp;
	}
	
	public Collection<String> getManyStrProp() {
		return this.manyStrProp;
	}
	
	public void setManyStrProp(Collection<String> manyStrProp) {
		this.manyStrProp = manyStrProp;
	}
	
	public Collection<Integer> getManyIntProp() {
		return this.manyIntProp;
	}
	
	public void setManyIntProp(Collection<Integer> manyIntProp) {
		this.manyIntProp = manyIntProp;
	}
	
	public EnumProp getEnumProp() {
		return this.enumProp;
	}
	
	public void setEnumProp(EnumProp enumProp) {
		this.enumProp = enumProp;
	}
	
	public Collection<ManyEnumProp> getManyEnumProp() {
		return this.manyEnumProp;
	}
	
	public void setManyEnumProp(Collection<ManyEnumProp> manyEnumProp) {
		this.manyEnumProp = manyEnumProp;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[6];
	 	ret[0] = getStrProp();
	 	ret[1] = getIntProp();
	 	ret[2] = getManyStrProp();
	 	ret[3] = getManyIntProp();
	 	ret[4] = getEnumProp();
	 	ret[5] = getManyEnumProp();
		return ret;
	}
	
	public String toString() {
		return "GeneratedType (" + " strProp=" + strProp + " intProp=" + intProp + " manyStrProp=" + manyStrProp + " manyIntProp=" + manyIntProp + " enumProp=" + enumProp + " manyEnumProp=" + manyEnumProp + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
	public enum EnumProp {
		A,
		B,
		C;}
	
	public enum ManyEnumProp {
		A,
		B,
		C;}
	
}

