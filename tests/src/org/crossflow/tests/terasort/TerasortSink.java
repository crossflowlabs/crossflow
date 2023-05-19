package org.crossflow.tests.terasort;


import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class TerasortSink extends TerasortSinkBase {


	private static final String HDFSOutputFolder = "hdfs://localhost:9000/sorted_files";
	private static final String HDFSInputFolder = "hdfs://localhost:9000/unsorted_files";
	private static final String HDFSResultFolder = "hdfs://localhost:9000/tera_result";
	@Override
	public void consumeTerasortResults(TerasortResult terasortResult) throws Exception {
		// TODO: Add implementation that instantiates, sets, and submits result objects (example below)
		long startTime = System.currentTimeMillis();

		TerasortWorkflowExt ext = (TerasortWorkflowExt) getWorkflow();
		ext.timer.cancel();

		FileSystem fileSystem = HadoopConfiguration.getFileSystem();
		Path resultPath = new Path(HDFSResultFolder);
		if (!fileSystem.exists(resultPath)) fileSystem.mkdirs(resultPath);
		fileSystem.rename(new Path(HDFSOutputFolder, terasortResult.sortedOutputFile.fileName), new Path(HDFSResultFolder, "teraResult"));

		Path sorted = new Path(HDFSOutputFolder);
		Path unsorted = new Path(HDFSInputFolder);

		if (fileSystem.exists(sorted)){
		   fileSystem.delete(sorted, true);
		}
		if (fileSystem.exists(unsorted)) {
		   fileSystem.delete(unsorted, true);
		}

		getWorkflow().addWorkTime(System.currentTimeMillis() - startTime);
	}
}
