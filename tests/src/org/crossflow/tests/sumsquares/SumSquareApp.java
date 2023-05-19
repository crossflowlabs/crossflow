package org.crossflow.tests.sumsquares;

import org.crossflow.runtime.Mode;

public class SumSquareApp {

	public static void main(String[] args) throws Exception {
		
		SumSquare master = new SumSquare(Mode.MASTER);
		master.createBroker(true);
		master.setMaster("localhost");
		
		//master.setParallelization(4);
		
		//master.setInputDirectory(new File("experiment/in"));
		//master.setOutputDirectory(new File("experiment/out"));
		
		master.setInstanceId("Example SumSquare Instance");
		master.setName("SumSquare");
		
		master.run();
		
		master.awaitTermination();
		
		//System.out.println("Done");
		
	}
	
}
