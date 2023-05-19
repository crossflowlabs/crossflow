package org.crossflow.tests.techrank;

import org.crossflow.runtime.Mode;

import java.io.File;
import java.util.ArrayList;

public class TechRankWorkflowWorkerApp {

    public static final String INPUT_FOLDER = "experiment/techrank/in";
    public static final String OUTPUT_FOLDER = "experiment/techrank/out";

    public static void main(String[] args) throws Exception {
        String workerName = "worker";
        var env = System.getenv();
        if (env.containsKey("WORKER_NAME")) {
            workerName = env.get("WORKER_NAME");
        }

        ArrayList<TechrankWorkflowExt> workers = new ArrayList<>();

        TechrankWorkflowExt worker = new TechrankWorkflowExt(Mode.WORKER);
        worker.setName(workerName);
        worker.setInstanceId("techrank");
        worker.setMaster("localhost");
        worker.setInputDirectory(new File(INPUT_FOLDER));
        worker.setOutputDirectory(new File(OUTPUT_FOLDER));
        worker.loadDownloadedRepositories();

        long init = System.currentTimeMillis();

        worker.run();

        while (!worker.hasTerminated()) {
            Thread.sleep(100);
        }

        System.out.println("completed in " + (System.currentTimeMillis() - init) + " ms.");
    }
}
