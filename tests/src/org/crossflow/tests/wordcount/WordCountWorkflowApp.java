package org.crossflow.tests.wordcount;

import java.io.File;

import org.crossflow.runtime.Mode;

public class WordCountWorkflowApp {

	public static void main(String[] args) throws Exception {
		
		WordCountWorkflow master = new WordCountWorkflow(Mode.MASTER);
		master.createBroker(true);
		master.setMaster("localhost");
		
		master.setInputDirectory(new File("experiment/wordcount/in"));
		master.setOutputDirectory(new File("experiment/wordcount/out"));
		
		master.setInstanceId("Example WordCountWorkflow Instance");
		master.setName("WordCountWorkflow");
		
		master.run();
		
		master.awaitTermination();
		
		System.out.println("Done");
		
	}
	
}
