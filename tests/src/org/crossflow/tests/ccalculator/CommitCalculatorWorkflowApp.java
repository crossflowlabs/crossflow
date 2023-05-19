package org.crossflow.tests.ccalculator;

import org.crossflow.runtime.Mode;

import java.io.File;

public class CommitCalculatorWorkflowApp {

	public static void main(String[] args) throws Exception {
		
		CommitCalculatorWorkflow master = new CommitCalculatorWorkflow(Mode.MASTER);
		master.createBroker(false);
		master.setMaster("localhost");
		
		//master.setParallelization(4);

		master.setInputDirectory(new File("experiment/calculator/in"));
		master.setOutputDirectory(new File("experiment/calculator/out"));

		master.setInstanceId("commit-calculator");
		master.setName("CommitmentCalculatorWorkflow");
		
		master.run();
		
		master.awaitTermination();
		
		System.out.println("Done");
		
	}
	
}
