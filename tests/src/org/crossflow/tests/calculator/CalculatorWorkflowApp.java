package org.crossflow.tests.calculator;

import org.crossflow.runtime.Mode;

import java.io.File;

public class CalculatorWorkflowApp {

	public static void main(String[] args) throws Exception {

		System.out.println("Let's do this!");
		
		CalculatorWorkflow master = new CalculatorWorkflow(Mode.MASTER_BARE); //tried with MASTER_BARE mode
		master.createBroker(false);
		master.setMaster("localhost");
		
		//master.setParallelization(4);
		
		master.setInputDirectory(new File("experiment/calculator/in"));
		master.setOutputDirectory(new File("experiment/calculator/out"));
		
		master.setInstanceId("calculator");
		master.setName("CalculatorWorkflow");

		master.run();
		
		master.awaitTermination();
		
		System.out.println("Done");
		
	}
	
}
