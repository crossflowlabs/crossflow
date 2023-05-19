package org.crossflow.tests.terasort;

import org.crossflow.runtime.Mode;
import org.crossflow.tests.WorkflowTests;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TerasortTests extends WorkflowTests {

    private static final int NUMBER_OF_WORKERS = 5;
    public static final String INPUT_FOLDER = "";
    public static final String OUTPUT_FOLDER = "";

    @Test
    public void testTerasortExample() throws Exception {

        System.setProperty("HADOOP_USER_NAME", "root");
        System.setProperty("hadoop.home.dir", "/");
        System.setProperty("dfs.datanode.use.datanode.hostname", "true");
        System.setProperty("dfs.client.use.datanode.hostname", "true");
        long init = System.currentTimeMillis();

        TerasortWorkflow master = new TerasortWorkflowExt(Mode.MASTER_BARE);
        master.createBroker(false);
        master.setMaster("localhost");
        master.setInstanceId("terasort");
        master.setName("MasterTerasort");


        List<TerasortWorkflow> workers = new ArrayList<>();
        TerasortWorkflowWorkerExt worker;

        for (int i = 1; i <= NUMBER_OF_WORKERS; i++) {
            worker = new TerasortWorkflowWorkerExt(Mode.WORKER);
            worker.setName("Worker" + i);
            worker.setInstanceId("terasort");
            worker.setHostname(String.format("datanode%d.company.com", i));
            workers.add(worker);
        }

        master.run();

        for (TerasortWorkflow terasortWorkflow : workers) {
            terasortWorkflow.run(0);
        }
        waitFor(master);
        System.out.println("normal execution time: " + (System.currentTimeMillis()-init) / 1000 + "s");
        assertTrue(master.hasTerminated());
    }


}
