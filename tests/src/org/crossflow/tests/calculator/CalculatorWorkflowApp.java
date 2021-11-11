package org.crossflow.tests.calculator;

import org.crossflow.runtime.Mode;

import java.io.File;

public class CalculatorWorkflowApp {

	public static void main(String[] args) throws Exception {

		System.out.println("Let's do this!");
		
		CalculatorWorkflow master = new CalculatorWorkflow(Mode.MASTER);
		master.createBroker(true);
		master.setMaster("localhost");
		
		//master.setParallelization(4);
		
		master.setInputDirectory(new File("experiment/calculator/in"));
		master.setOutputDirectory(new File("experiment/calculator/out"));
		
		master.setInstanceId("Example CalculatorWorkflow Instance");
		master.setName("CalculatorWorkflow");
		
		master.run();
		
		master.awaitTermination();
		
		System.out.println("Done");
		
	}
	
}
