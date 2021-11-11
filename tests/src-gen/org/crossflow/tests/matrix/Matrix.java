package org.crossflow.tests.matrix;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2021-10-26T15:08:25.592432Z")
public class Matrix extends Job {
		
	protected Collection<Row> rows = new ArrayList<Row>();
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public Matrix() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public Matrix(Collection<Row> rows) {
		this.rows = rows;
	}
	
	 
	public Matrix(Collection<Row> rows, Job correlation) {
		this.rows = rows;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public Collection<Row> getRows() {
		return this.rows;
	}
	
	public void setRows(Collection<Row> rows) {
		this.rows = rows;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getRows();
		return ret;
	}
	
	public String toString() {
		return "Matrix (" + " rows=" + rows + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

