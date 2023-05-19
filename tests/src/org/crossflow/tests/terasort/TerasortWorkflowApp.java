package org.crossflow.tests.terasort;

import org.crossflow.runtime.Mode;

public class TerasortWorkflowApp {

	public static void main(String[] args) throws Exception {

		TerasortWorkflowExt master = new TerasortWorkflowExt(Mode.MASTER);
		master.createBroker(false);
		master.setMaster("localhost");

		System.setProperty("HADOOP_USER_NAME", "root");
		System.setProperty("hadoop.home.dir", "/");
		System.setProperty("dfs.datanode.use.datanode.hostname", "true");
		System.setProperty("dfs.client.use.datanode.hostname", "true");

		// List<Datanode> nodes = namenode.getDatanodes()
		/* for (Datanode node: nodes) {
			TerasortWorkflow worker = new TerasortWorkflow(Mode.WORKER);
			worker.setName(node.getHostname());
		} */
		
		//master.setParallelization(4);
		
//		master.setInputDirectory(new File("experiment/in"));
//		master.setOutputDirectory(new File("experiment/out"));
		
		master.setInstanceId("terasort");

		
		master.run();
		
		master.awaitTermination();

		
	}
	
}
