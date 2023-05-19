package org.crossflow.tests.ccalculator;

import org.apache.commons.csv.CSVRecord;
import org.crossflow.runtime.utils.CsvParser;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class CalculatorSource extends CalculatorSourceBase {

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