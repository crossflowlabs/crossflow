package org.crossflow.examples.techanalysis;

import java.io.File;

import org.apache.commons.csv.CSVRecord;
import org.crossflow.runtime.utils.CsvParser;
import org.crossflow.runtime.utils.LogLevel;

public class TechnologySource extends TechnologySourceBase {

	protected Iterable<CSVRecord> records;

	@Override
	public void produce() {
		try {
			final CsvParser parser = new CsvParser(
					new File(workflow.getInputDirectory(), "input.csv").getAbsolutePath());
			records = parser.getRecordsIterable();

			for (CSVRecord record : records) {
				Technology technologyTuple = new Technology();
				technologyTuple.setFileExt(record.get(0));
				technologyTuple.setTechKey(record.get(1));
				if (!technologyTuple.getFileExt().startsWith("//")) {
					System.out.println("sending technology: " + "fileExt=" + technologyTuple.fileExt + " techKey="
							+ technologyTuple.techKey);
					sendToTechnologies(technologyTuple);
				}
			}
		} catch (Exception e) {
			workflow.log(LogLevel.ERROR, e.getMessage());
		}
	}

	public Iterable<CSVRecord> getRecords() {
		return records;
	}

}