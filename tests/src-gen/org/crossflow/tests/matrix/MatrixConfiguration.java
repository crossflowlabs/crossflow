package org.crossflow.tests.matrix;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2021-10-26T15:08:25.592432Z")
public class MatrixConfiguration extends Job {
		
	protected int rows;
	protected int columns;
	protected int initialValue;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public MatrixConfiguration() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public MatrixConfiguration(int rows, int columns, int initialValue) {
		this.rows = rows;
		this.columns = columns;
		this.initialValue = initialValue;
	}
	
	 
	public MatrixConfiguration(int rows, int columns, int initialValue, Job correlation) {
		this.rows = rows;
		this.columns = columns;
		this.initialValue = initialValue;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public int getRows() {
		return this.rows;
	}
	
	public void setRows(int rows) {
		this.rows = rows;
	}
	
	public int getColumns() {
		return this.columns;
	}
	
	public void setColumns(int columns) {
		this.columns = columns;
	}
	
	public int getInitialValue() {
		return this.initialValue;
	}
	
	public void setInitialValue(int initialValue) {
		this.initialValue = initialValue;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[3];
	 	ret[0] = getRows();
	 	ret[1] = getColumns();
	 	ret[2] = getInitialValue();
		return ret;
	}
	
	public String toString() {
		return "MatrixConfiguration (" + " rows=" + rows + " columns=" + columns + " initialValue=" + initialValue + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

