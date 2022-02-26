package org.crossflow.tests.ccalculator;

import org.apache.commons.io.FileUtils;
import org.crossflow.runtime.Mode;
import org.crossflow.runtime.utils.CsvParser;
import org.crossflow.tests.WorkflowTests;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;

public class CommitCalculatorTests extends WorkflowTests {

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
}
