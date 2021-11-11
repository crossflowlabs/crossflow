package typeInheritanceAdditionWorkflow;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2021-10-26T15:08:22.434314Z")
public class Number extends Printable {
		
	protected int n;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public Number() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public Number(int n, String print) {
		this.n = n;
		this.print = print;
	}
	
	 
	public Number(int n, String print, Job correlation) {
		this.n = n;
		this.print = print;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public Number(Printable printable) {
		this.setPrint(printable.getPrint());
		this.correlationId = printable.getJobId();
	}
		
	
	public int getN() {
		return this.n;
	}
	
	public void setN(int n) {
		this.n = n;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getN();
		return ret;
	}
	
	public String toString() {
		return "Number (" + " n=" + n + " getPrint()=" + getPrint() + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

