package org.crossflow.tests.techrank;

import org.crossflow.runtime.Mode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TechRankWorkflowMasterApp {

    public static final String INPUT_FOLDER = "experiment/techrank/in";
    public static final String OUTPUT_FOLDER = "experiment/techrank/out";
    private static final int NUMBER_OF_WORKERS = 3;


    public static void main(String[] args) throws Exception {
        String workerName = "master";


        TechrankWorkflowExt master = new TechrankWorkflowExt(Mode.MASTER_BARE);
        master.setName(workerName);
        master.createBroker(false);
        master.setMaster("localhost");
        master.setInstanceId("techrank");
        master.setInputDirectory(new File(INPUT_FOLDER));
        master.setOutputDirectory(new File(OUTPUT_FOLDER));
        master.loadDownloadedRepositories();

        startWorkers();

        long init = System.currentTimeMillis();
        master.run(5_000);

        master.awaitTermination();

        System.out.println("master completed in " + (System.currentTimeMillis() - init) + " ms.");
    }

    private static void startWorkers() throws Exception {
        List<TechrankWorkflowExt> workers = new ArrayList<>();
        TechrankWorkflowExt worker;

        for (int i = 0; i<NUMBER_OF_WORKERS; i++) {
            worker = new TechrankWorkflowExt(Mode.WORKER);
            worker.setName("Worker"+i);
            worker.setInstanceId("techrank");
            worker.setInputDirectory(new File(INPUT_FOLDER));
            worker.setOutputDirectory(new File(OUTPUT_FOLDER));
            workers.add(worker);
        }

        for (TechrankWorkflow w : workers) {
            w.run(0);
        }

    }
}
