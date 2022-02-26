package org.crossflow.tests.ccalculator;


import org.crossflow.runtime.utils.CsvWriter;
import org.crossflow.tests.ccalculator.CalculationResult;

import java.io.File;
import java.io.IOException;

public class CalculationResultsSink extends CalculationResultsSinkBase {

    protected CsvWriter writer = null;

    @Override
    public synchronized void consumeCalculationResults(CalculationResult result) throws Exception {
        if (writer == null) {
            File output = new File(workflow.getOutputDirectory(), "output.csv");
            if (output.exists()) {
                output.delete();
            }
            writer = new CsvWriter(output.getAbsolutePath(), "a", "operator", "b", "result", "worker", "hash");
        }

        writer.writeRecord(result.getA(), result.getOperator(), result.getB(), result.getResult(), result.getWorker(), result.getJobHash());
        writer.flush();
    }

    @Override
    public void close() {
        if (writer != null)
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        super.close();
    }



}
