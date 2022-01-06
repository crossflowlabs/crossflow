package orphanqueuesworkflow;

import javax.annotation.Generated;
import java.io.Serializable;
import java.util.Collection;
import java.util.ArrayList;
import org.crossflow.runtime.Job;

@Generated(value = "org.crossflow.java.Type2Class", date = "2022-01-05T17:43:34.478356Z")
public class ty1 extends Job {
		
	protected String f0;
	
	/**
	 * Default Constructor
	 * <p>
	 * Deserialization requires an empty instance to modify
	 * </p>
	 */
	public ty1() {
		;
	}
	
	/**
	 * Constructor allow initialization of all declared fields
	 */
	public ty1(String f0) {
		this.f0 = f0;
	}
	
	 
	public ty1(String f0, Job correlation) {
		this.f0 = f0;
		this.correlationId = correlation.getCorrelationId();
	}
	
	public String getF0() {
		return this.f0;
	}
	
	public void setF0(String f0) {
		this.f0 = f0;
	}
	
	public Object[] toObjectArray() {
		Object[] ret = new Object[1];
	 	ret[0] = getF0();
		return ret;
	}
	
	public String toString() {
		return "ty1 (" + " f0=" + f0 + " jobId=" + jobId + " correlationId=" + correlationId + " destination=" + destination + ")";
	}
	
}

