package org.crossflow.tests.techrank;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.crossflow.runtime.Mode;
import org.crossflow.tests.WorkflowTests;
import org.crossflow.tests.sumsquares.SumSquare;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class TechrankTests extends WorkflowTests {

    private static final int NUMBER_OF_WORKERS = 3;
    public static final String INPUT_FOLDER = "experiment/techrank/in";
    public static final String OUTPUT_FOLDER = "experiment/techrank/out";

    @Before
    public void deleteDir() throws IOException {
        File output = new File(OUTPUT_FOLDER);
        if (output.exists())
            FileUtils.deleteDirectory(output);
    }

    @Test
    public void testReadJson() throws Exception {
        TechrankWorkflowExt master = new TechrankWorkflowExt(Mode.MASTER_BARE);
        master.createBroker(false);
        master.setMaster("localhost");
        master.setInputDirectory(new File(INPUT_FOLDER));
        master.setOutputDirectory(new File(OUTPUT_FOLDER));
        master.setInstanceId("techrank");
        master.setName("techrank-master");
//        master.run(0);
        master.getRepository().flushAll();
        System.out.println("Done");
    }

    private static TechrankWorkerConfiguration[] getTechrankConfigurationsFromJson(String confFileName) throws IOException {
        String filePath = String.format("experiment/techrank/in/%s", confFileName);
        Gson gson = new Gson();
        return gson.fromJson(FileUtils.readFileToString(new File(filePath)), TechrankWorkerConfiguration[].class);
    }



    @Test
    public void testConfAllEq40_40_40() throws Exception {
        for (int i = 0; i < 3; i++) {
            TechrankWorkflowExt master = new TechrankWorkflowExt(Mode.MASTER_BARE);
            master.createBroker(false);
            master.setMaster("localhost");
            master.setInputDirectory(new File(INPUT_FOLDER));
            master.setOutputDirectory(new File(OUTPUT_FOLDER));
            master.setInstanceId("techrank");
            master.setName("techrank-master");
            if (i == 0) master.getRepository().flushAll();

            TechrankWorkerConfiguration[] configurations = getTechrankConfigurationsFromJson("conf_one_fast.json");

            List<TechrankWorkflowExt> workers = new ArrayList<>();
            TechrankWorkflowExt worker;

            for (TechrankWorkerConfiguration configuration : configurations) {
                worker = new TechrankWorkflowExt(Mode.WORKER);
                worker.setName(configuration.getName());
                worker.setInstanceId("techrank");
                worker.setInputDirectory(new File(INPUT_FOLDER));
                worker.setOutputDirectory(new File(OUTPUT_FOLDER));
                worker.setNet_bytesPerSecond(configuration.getNetSpeedBps());
                worker.setIo_bytesPerSecond(configuration.getIoSpeedBps());
                worker.loadDownloadedRepositories();
                worker.repoInputFile = "experiment/techrank/in/80_percent_large_10_10_100.csv";
                workers.add(worker);
            }

            long init = System.currentTimeMillis();
            master.run();

            for (TechrankWorkflow w : workers) {
                w.run(0);
            }

            waitFor(master);
            System.out.println("normal execution time: " + (System.currentTimeMillis()-init) / 1000 + "s");

            assertEquals(1000, 1000);

        }
    }

    private void confAllEq40_40_40() throws Exception {
        TechrankWorkflowExt master = new TechrankWorkflowExt(Mode.MASTER_BARE);
        master.createBroker(false);
        master.setMaster("localhost");
        master.setInputDirectory(new File(INPUT_FOLDER));
        master.setOutputDirectory(new File(OUTPUT_FOLDER));
        master.setInstanceId("techrank");
        master.setName("techrank-master");

        TechrankWorkerConfiguration[] configurations = getTechrankConfigurationsFromJson("conf_one_fast.json");

        List<TechrankWorkflowExt> workers = new ArrayList<>();
        TechrankWorkflowExt worker;

        for (TechrankWorkerConfiguration configuration : configurations) {
            worker = new TechrankWorkflowExt(Mode.WORKER);
            worker.setName(configuration.getName());
            worker.setInstanceId("techrank");
            worker.setInputDirectory(new File(INPUT_FOLDER));
            worker.setOutputDirectory(new File(OUTPUT_FOLDER));
            worker.setNet_bytesPerSecond(configuration.getNetSpeedBps());
            worker.setIo_bytesPerSecond(configuration.getIoSpeedBps());
            worker.loadDownloadedRepositories();
            worker.repoInputFile = "experiment/techrank/in/80_posto_velikih_10_10_100.csv";
            workers.add(worker);
        }

        long init = System.currentTimeMillis();
        master.run();

        for (TechrankWorkflow w : workers) {
            w.run(0);
        }

        waitFor(master);
        System.out.println("normal execution time: " + (System.currentTimeMillis()-init) / 1000 + "s");

        assertEquals(1000, 1000);
    }

    @Test
    public void testAllEqualSpeed() throws Exception {

        TechrankWorkflowExt master = new TechrankWorkflowExt(Mode.MASTER_BARE);
        master.createBroker(false);
        master.setMaster("localhost");
        master.setInputDirectory(new File(INPUT_FOLDER));
        master.setOutputDirectory(new File(OUTPUT_FOLDER));
        master.setInstanceId("techrank");
        master.setName("techrank-master");

        TechrankWorkerConfiguration[] configurations = getTechrankConfigurationsFromJson("conf_one_fast.json");

        List<TechrankWorkflowExt> workers = new ArrayList<>();
        TechrankWorkflowExt worker;

        for (TechrankWorkerConfiguration configuration : configurations) {
            worker = new TechrankWorkflowExt(Mode.WORKER);
            worker.setName(configuration.getName());
            worker.setInstanceId("techrank");
            worker.setInputDirectory(new File(INPUT_FOLDER));
            worker.setOutputDirectory(new File(OUTPUT_FOLDER));
            worker.setNet_bytesPerSecond(configuration.getNetSpeedBps());
            worker.setIo_bytesPerSecond(configuration.getIoSpeedBps());
            worker.loadDownloadedRepositories();
            worker.repoInputFile = "experiment/techrank/in/80_posto_velikih_10_10_100.csv";
            workers.add(worker);
        }

        long init = System.currentTimeMillis();
        master.run();

        for (TechrankWorkflow w : workers) {
            w.run(0);
        }

        waitFor(master);
        System.out.println("normal execution time: " + (System.currentTimeMillis()-init) / 1000 + "s");

        assertEquals(1000, 1000);

    }


    @Test
    public void testDedicatedQueues() throws Exception {

        TechrankWorkflowExt master = new TechrankWorkflowExt(Mode.MASTER_BARE);
        master.createBroker(false);
        master.setMaster("localhost");
        master.setInputDirectory(new File(INPUT_FOLDER));
        master.setOutputDirectory(new File(OUTPUT_FOLDER));
        master.setInstanceId("techrank");
        master.setName("techrank-master");


        List<TechrankWorkflowExt> workers = new ArrayList<>();
        TechrankWorkflowExt worker;

        for (int i = 1; i <= NUMBER_OF_WORKERS; i++) {
            worker = new TechrankWorkflowExt(Mode.WORKER);
            worker.setName("Worker" + i);
            worker.setInstanceId("techrank");
            worker.setInputDirectory(new File(INPUT_FOLDER));
            worker.setOutputDirectory(new File(OUTPUT_FOLDER));
            workers.add(worker);
        }


        long init = System.currentTimeMillis();
        master.run();

        for (TechrankWorkflow w : workers) {
            w.run(0);
        }


        waitFor(master);
        System.out.println("normal execution time: " + (System.currentTimeMillis()-init) / 1000 + "s");

        assertEquals(1000, 1000);


    }


}
