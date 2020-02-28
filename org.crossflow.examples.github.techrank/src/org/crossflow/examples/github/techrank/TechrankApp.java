package org.crossflow.examples.github.techrank;

import java.io.File;

import org.crossflow.runtime.DirectoryCache;
import org.crossflow.runtime.FailedJob;
import org.crossflow.runtime.InternalException;

public class TechrankApp {
	
	public static void main(String[] args) throws Exception {
		
		TechrankWorkflow workflow = new TechrankWorkflow();
		workflow.setCache(new DirectoryCache(new File("cache")));
		workflow.getRepositorySearchDispatcher().setCacheable(false);
		workflow.getRepositorySearcher().setCacheable(false);
		
		workflow.run();
		
		while (!workflow.hasTerminated()) {
			Thread.sleep(100);
		}
		
		for (InternalException ex : workflow.getInternalExceptions()) {
			System.err.println(ex.getStacktrace());
		}
		
		for (FailedJob failed : workflow.getFailedJobs()) {
			System.err.println(failed.getStacktrace());
		}
		
		System.out.println("Done");
		
		//System.exit(0);
	}
	
}
