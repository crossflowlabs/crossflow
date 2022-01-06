package org.crossflow.tests.churnRateRepo;

import org.crossflow.runtime.Mode;

public class ChurnRateWorkflowApp {

	public static void main(String[] args) throws Exception {
		
		ChurnRateWorkflow master = new ChurnRateWorkflow(Mode.MASTER);
		master.createBroker(true);
		master.setMaster("localhost");
		
		//master.setParallelization(4);
		
		//master.setInputDirectory(new File("experiment/in"));
		//master.setOutputDirectory(new File("experiment/out"));
		
		master.setInstanceId("Example ChurnRateWorkflow Instance");
		master.setName("ChurnRateWorkflow");
		
		master.run();
		
		master.awaitTermination();
		
		System.out.println("Done");
		
	}
	
}
