package org.crossflow.tests.sumsquares;

import org.apache.commons.csv.CSVRecord;
import org.crossflow.runtime.utils.CsvParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Timer;
import java.util.TimerTask;

public class SumSquareSource extends SumSquareSourceBase {

	@Override
	public void produce() throws Exception {
		BufferedReader parser = new BufferedReader(new FileReader(workflow.getInputDirectory() +  "/input.txt"));

		String line;
		while ((line = parser.readLine())!=null){
			Number number = new Number();
			number.setA(Integer.parseInt(line));
			sendToNumbers(number);
		}
	}
}
