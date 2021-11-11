package org.crossflow.tests.matrix;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2021-10-26T15:08:25.592432Z")
public class Row implements Serializable {
		
	protected Collection<Integer> cells = new ArrayList<Integer>();
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public Row() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public Row(Collection<Integer> cells) {
		this.cells = cells;
	}
	
	public Collection<Integer> getCells() {
		return this.cells;
	}
	
	public void setCells(Collection<Integer> cells) {
		this.cells = cells;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getCells();
		return ret;
	}
	
	public String toString() {
		return "Row (" + " cells=" + cells + ")";
	}
	
}

