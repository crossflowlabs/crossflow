package org.crossflow.tests.calculator;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.FileUtils;
import org.crossflow.runtime.DirectoryCache;
import org.crossflow.runtime.Mode;
import org.crossflow.runtime.utils.CsvParser;
import org.crossflow.tests.WorkflowTests;
import org.crossflow.tests.ccalculator.CommitCalculatorWorkflow;
import org.junit.Before;
import org.junit.Test;

public class CalculatorTests extends WorkflowTests {

    private static final int NUMBER_OF_WORKERS = 3;
    public static final String INPUT_FOLDER = "experiment/calculator/in";
    public static final String OUTPUT_FOLDER = "experiment/calculator/out";

    @Before
    public void deleteDir() throws IOException {
        File output = new File(OUTPUT_FOLDER);
        if (output.exists())
            FileUtils.deleteDirectory(output);
    }


    @Test
    public void testOutput() throws Exception {



        CalculatorWorkflow workflow = new CalculatorWorkflow();
        workflow.createBroker(createBroker);
        workflow.setName("master");
        workflow.setInputDirectory(new File(INPUT_FOLDER));
        workflow.setOutputDirectory(new File(OUTPUT_FOLDER));
        workflow.run();

        waitFor(workflow);


        File output = new File(OUTPUT_FOLDER + "/output.csv");
        CsvParser parser = new CsvParser(output.getAbsolutePath());
        assertEquals("8", parser.getRecordsList().get(0).get(3));

    }

    @Test
    public void testCache() throws Exception {
        CalculatorWorkflow workflow = new CalculatorWorkflow();
        workflow.createBroker(createBroker);
        workflow.setInputDirectory(new File(INPUT_FOLDER));
        workflow.setOutputDirectory(new File(OUTPUT_FOLDER));
        DirectoryCache cache = new DirectoryCache();
        workflow.setCache(cache);

        workflow.run();
        waitFor(workflow);

        workflow = new CalculatorWorkflow();
        workflow.setInputDirectory(new File(INPUT_FOLDER));
        workflow.setOutputDirectory(new File(OUTPUT_FOLDER));
        workflow.setCache(new DirectoryCache(cache.getDirectory()));

        workflow.run();
        waitFor(workflow);

        assertEquals(0, workflow.getCache().hashCode());
    }

    @Test
    public void testDedicatedQueues() throws Exception {

        CalculatorWorkflow master = new CalculatorWorkflow(Mode.MASTER_BARE);
        master.createBroker(false);
        master.setMaster("localhost");
        master.setInputDirectory(new File(INPUT_FOLDER));
        master.setOutputDirectory(new File(OUTPUT_FOLDER));
        master.setInstanceId("dedicated-calculator");
        master.setName("DedicatedCalculatorWorkflow");


        List<CalculatorWorkflow> workers = new ArrayList<>();
        CalculatorWorkflow worker;

        for (int i = 1; i <= NUMBER_OF_WORKERS; i++) {
            worker = new CalculatorWorkflow(Mode.WORKER);
            worker.setName("Worker" + i);
            worker.getCalculator().setDelay(200);
            worker.setInstanceId("dedicated-calculator");
            workers.add(worker);
        }


        master.run();

        for (CalculatorWorkflow calculatorWorkflow : workers) {
            calculatorWorkflow.run(0);
        }


        waitFor(master);

        CsvParser parser = new CsvParser(new File(OUTPUT_FOLDER + "/output.csv").getAbsolutePath());
        assertEquals(779, parser.getRecordsList().size());
    }

}
