package org.crossflow.tests.calculator;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2021-10-26T15:08:22.866352Z")
public class CalculationResult extends Calculation {
		
	protected String worker;
	protected String result;
	protected WorkerLang workerLang;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public CalculationResult() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public CalculationResult(String worker, String result, WorkerLang workerLang, int a, int b, String operator) {
		this.worker = worker;
		this.result = result;
		this.workerLang = workerLang;
		this.a = a;
		this.b = b;
		this.operator = operator;
	}
	
	 
	public CalculationResult(String worker, String result, WorkerLang workerLang, int a, int b, String operator, Job correlation) {
		this.worker = worker;
		this.result = result;
		this.workerLang = workerLang;
		this.a = a;
		this.b = b;
		this.operator = operator;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public CalculationResult(Calculation calculation) {
		this.setA(calculation.getA());
		this.setB(calculation.getB());
		this.setOperator(calculation.getOperator());
		this.correlationId = calculation.getJobId();
	}
		
	
	public String getWorker() {
		return this.worker;
	}
	
	public void setWorker(String worker) {
		this.worker = worker;
	}
	
	public String getResult() {
		return this.result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public WorkerLang getWorkerLang() {
		return this.workerLang;
	}
	
	public void setWorkerLang(WorkerLang workerLang) {
		this.workerLang = workerLang;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[3];
	 	ret[0] = getWorker();
	 	ret[1] = getResult();
	 	ret[2] = getWorkerLang();
		return ret;
	}
	
	public String toString() {
		return "CalculationResult (" + " worker=" + worker + " result=" + result + " workerLang=" + workerLang + " getA()=" + getA() + " getB()=" + getB() + " getOperator()=" + getOperator() + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
	public enum WorkerLang {
		PYTHON,
		JAVA;}
	
}

