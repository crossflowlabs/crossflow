package typeInheritanceAdditionWorkflow;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2021-10-26T15:08:22.434314Z")
public class Printable extends Job {
		
	protected String print;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public Printable() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public Printable(String print) {
		this.print = print;
	}
	
	 
	public Printable(String print, Job correlation) {
		this.print = print;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public String getPrint() {
		return this.print;
	}
	
	public void setPrint(String print) {
		this.print = print;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getPrint();
		return ret;
	}
	
	public String toString() {
		return "Printable (" + " print=" + print + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

