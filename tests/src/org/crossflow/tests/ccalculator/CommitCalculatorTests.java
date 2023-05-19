package org.crossflow.tests.ccalculator;

import org.apache.commons.io.FileUtils;
import org.crossflow.runtime.Mode;
import org.crossflow.runtime.utils.CsvParser;
import org.crossflow.tests.WorkflowTests;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;

public class CommitCalculatorTests extends WorkflowTests {

    private static final int NUMBER_OF_WORKERS = 3;
    public static final String INPUT_FOLDER = "experiment/calculator/in";
    public static final String OUTPUT_FOLDER = "experiment/calculator/out";


    @Test
    public void testOutputMasterBare() throws Exception {

        File output = new File("experiment/calculator/out");
        if (output.exists())
            FileUtils.deleteDirectory(output);

        CommitCalculatorWorkflow master = new CommitCalculatorWorkflow(Mode.MASTER_BARE);
        master.createBroker(false);
        master.setMaster("localhost");
        master.setInputDirectory(new File("experiment/calculator/in"));
        master.setOutputDirectory(output);
        master.setInstanceId("commit-calculator");
        master.setName("CommitmentCalculatorWorkflow");

        CommitCalculatorWorkflow worker1 = new CommitCalculatorWorkflow(Mode.WORKER);
        //worker1.setMaster("localhost");
        worker1.setName("Worker" + ThreadLocalRandom.current().nextInt(0, 10));
        worker1.getCalculator().setDelay(500);
        worker1.setInstanceId("commit-calculator");

        CommitCalculatorWorkflow worker2 = new CommitCalculatorWorkflow(Mode.WORKER);
        //worker1.setMaster("localhost");
        worker2.setName("Worker" + ThreadLocalRandom.current().nextInt(0, 10));
        worker2.getCalculator().setDelay(500);
        worker2.setInstanceId("commit-calculator");


        master.run();
        worker1.run();
        worker2.run();
        waitFor(master);

        CsvParser parser = new CsvParser(new File(output + "/output.csv").getAbsolutePath());
        assertEquals(5, parser.getRecordsList().size());

    }

    @Test
    public void testDedicatedQueue() throws Exception{
        CommitCalculatorWorkflow master = new CommitCalculatorWorkflow(Mode.MASTER_BARE);
        master.createBroker(false);
        master.setMaster("localhost");
        master.setInputDirectory(new File(INPUT_FOLDER));
        master.setOutputDirectory(new File(OUTPUT_FOLDER));
        master.setInstanceId("dedicated-calculator");
        master.setName("DedicatedCalculatorWorkflow");


        List<CommitCalculatorWorkflow> workers = new ArrayList<>();
        CommitCalculatorWorkflow worker;

        for (int i = 1; i <= NUMBER_OF_WORKERS; i++) {
            worker = new CommitCalculatorWorkflow(Mode.WORKER);
            worker.setName("Worker" + i);
            worker.getCalculator().setDelay(200);
            worker.setInstanceId("dedicated-calculator");
            workers.add(worker);
        }


        master.run();

        for (CommitCalculatorWorkflow calculatorWorkflow : workers) {
            calculatorWorkflow.run(0);
        }


        waitFor(master);

        CsvParser parser = new CsvParser(new File(OUTPUT_FOLDER + "/output.csv").getAbsolutePath());
        assertEquals(21, parser.getRecordsList().size());
    }
}
