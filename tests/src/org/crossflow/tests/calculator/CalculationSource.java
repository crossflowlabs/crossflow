package org.crossflow.tests.calculator;

import org.apache.commons.csv.CSVRecord;
import org.crossflow.runtime.utils.CsvParser;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class CalculationSource extends CalculationSourceBase {
	
	@Override
	public void produce() throws Exception {
		CsvParser parser = new CsvParser(new File(workflow.getInputDirectory(), "input.csv").getAbsolutePath());

		for (CSVRecord record : parser.getRecordsList()) {
			Calculation calculation = new Calculation();
			calculation.setA(Integer.parseInt(record.get(0)));
			calculation.setOperator(record.get(1));
			calculation.setB(Integer.parseInt(record.get(2)));
			sendToCalculations(calculation);
		}
	}
}
