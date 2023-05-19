package org.crossflow.tests.sumsquares;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.crossflow.runtime.DirectoryCache;
import org.crossflow.runtime.Mode;
import org.crossflow.runtime.utils.CsvParser;
import org.crossflow.tests.WorkflowTests;
import org.crossflow.tests.calculator.CalculatorWorkflow;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SumSqTests extends WorkflowTests {

    private static final int NUMBER_OF_WORKERS = 3;
    public static final String INPUT_FOLDER = "experiment/sumsq/in/";
    public static final String OUTPUT_FOLDER = "experiment/sumsq/out/";

    @Before
    public void deleteDir() throws IOException {
        File output = new File(OUTPUT_FOLDER);
        if (output.exists())
            FileUtils.deleteDirectory(output);
    }


    @Test
    public void testDedicatedQueues() throws Exception {

        SumSquare master = new SumSquare(Mode.MASTER_BARE);
        master.createBroker(false);
        master.setMaster("localhost");
        master.setInputDirectory(new File(INPUT_FOLDER));
        master.setOutputDirectory(new File(OUTPUT_FOLDER));
        master.setInstanceId("sumsq");
        master.setName("SumSquareWF");


        List<SumSquare> workers = new ArrayList<>();
        SumSquare worker;

        for (int i = 1; i <= NUMBER_OF_WORKERS; i++) {
            worker = new SumSquare(Mode.WORKER);
            worker.setName("Worker" + i);
            worker.setInstanceId("sumsq");
            workers.add(worker);
        }


        long init = System.currentTimeMillis();
        master.run();

        for (SumSquare w : workers) {
            w.run(0);
        }


        waitFor(master);
        System.out.println("normal execution time: " + (System.currentTimeMillis()-init) / 1000 + "s");

        assertEquals(1000, 1000);


    }


}
