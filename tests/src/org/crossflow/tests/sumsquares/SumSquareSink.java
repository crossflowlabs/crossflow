package org.crossflow.tests.sumsquares;


import org.crossflow.runtime.utils.CsvWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SumSquareSink extends SumSquareSinkBase {

	protected PrintWriter writer = null;

	@Override
	public void consumeSquares(Square square) throws Exception {

		if (writer == null) {
			Path path = Paths.get(workflow.getOutputDirectory() + "/output.txt");
			if (Files.exists(path)) Files.delete(path);
			if (!Files.exists(workflow.getOutputDirectory().toPath())) {
				Files.createDirectories(workflow.getOutputDirectory().toPath());
			}
			/*File output = new File(workflow.getOutputDirectory(), "/output.txt");
			if (output.exists()) {
				output.delete();
			}*/
			writer = new PrintWriter(new FileWriter(path.toFile()));
		}

		writer.println(square.getSquare());
		writer.flush();
	}

	@Override
	public void close() {
		if (writer != null) {
			writer.close();
		}
		super.close();
	}


}
