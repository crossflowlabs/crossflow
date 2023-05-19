package org.crossflow.tests.techrank;

import org.crossflow.runtime.Mode;

public class TechrankWorkflowApp {

	public static void main(String[] args) throws Exception {
		
		TechrankWorkflow master = new TechrankWorkflow(Mode.MASTER_BARE);
		master.createBroker(false);
		master.setMaster("localhost");
		
		//master.setParallelization(4);
		
//		master.setInputDirectory(new File("experiment/techrank/in"));
//		master.setOutputDirectory(new File("experiment/techrank/out"));

		
		master.setInstanceId("Example TechrankWorkflow Instance");
		master.setName("TechrankWorkflow");
		
		master.run();
		
		master.awaitTermination();
		
		System.out.println("Done");
		
	}
	
}
