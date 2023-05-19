package org.crossflow.tests.terasort;


import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class Merger extends CommitmentMergerBase {

	private final FileSystem fileSystem = HadoopConfiguration.getFileSystem();

	private final String HDFSInputFolder = "hdfs://localhost:9000/sorted_files";
	private final String HDFSOutputFolder = "hdfs://localhost:9000/sorted_files";

	private List<Integer> getFileChunkIds(String filename) {
		String fileChunks = filename.substring(filename.indexOf("_") + 1);
		return Arrays.stream(fileChunks.split("-"))
				.map(Integer::parseInt)
				.sorted()
				.collect(Collectors.toList());
	}

	private String constructMergedFilename(SortedFilePath file1, SortedFilePath file2) {
		List<Integer> chunks1 = getFileChunkIds(file1.fileName);
		List<Integer> chunks2 = getFileChunkIds(file2.fileName);
		StringJoiner joiner = new StringJoiner("-", "sorted_", "");
		List<Integer> merged = new ArrayList<>();
		merged.addAll(chunks1);
		merged.addAll(chunks2);
		merged.stream()
				.sorted()
				.forEach(elem -> joiner.add(Integer.toString(elem)));
		return joiner.toString();
	}

	@Override
	public SortedFilePath consumeFilesForMerge(SortedFilePair sortedFilePair) throws Exception {

		long startTime = System.currentTimeMillis();

		SortedFilePath first = sortedFilePair.firstSortedFile;
		SortedFilePath second = sortedFilePair.secondSortedFile;
		mergeFiles(first, second);

		String mergedFilename = constructMergedFilename(first, second);

		SortedFilePath sortedFilePath = new SortedFilePath();
		sortedFilePath.setFileName(mergedFilename);
		sortedFilePath.setLineCount(first.lineCount + second.lineCount);

		getWorkflow().addWorkTime(System.currentTimeMillis() - startTime);
		return sortedFilePath;

	}

	private void mergeFiles(SortedFilePath first, SortedFilePath second) {
		String mergedFilename = constructMergedFilename(first, second);
		try (
				BufferedReader secondReader = getBufferedReader(second.fileName);
				BufferedReader firstReader = getBufferedReader(first.fileName);
				FSDataOutputStream currentFile = fileSystem.create(new Path(HDFSOutputFolder, mergedFilename))
		) {

			String firstFileLine = firstReader.readLine();
			String secondFileLine = secondReader.readLine();

			while (firstFileLine != null && secondFileLine != null) {
				if (firstFileLine.compareTo(secondFileLine) < 0) {
					currentFile.writeBytes(firstFileLine + "\n");
					firstFileLine = firstReader.readLine();
				} else {
					currentFile.writeBytes(secondFileLine + "\n");
					secondFileLine = secondReader.readLine();
				}
			}

			while (firstFileLine != null) {
				currentFile.writeBytes(firstFileLine + "\n");
				firstFileLine = firstReader.readLine();
			}

			while (secondFileLine != null) {
				currentFile.writeBytes(secondFileLine + "\n");
				secondFileLine = secondReader.readLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BufferedReader getBufferedReader(String fileName) throws IOException {
		InputStream fileInputStream = getFileInputStream(fileName);
		return new BufferedReader(new InputStreamReader(fileInputStream));
	}

	private InputStream getFileInputStream(String fileName) {
		try {
			Path hadoopFile = new Path(HDFSInputFolder, fileName);
			return fileSystem.open(hadoopFile);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean hasCommited(SortedFilePair sortedFilePair) {
//		return true;
		try {
			TerasortWorkflowWorkerExt workerExt = (TerasortWorkflowWorkerExt) getWorkflow();
			SortedFilePath largerFile = sortedFilePair.firstSortedFile.lineCount > sortedFilePair.secondSortedFile.lineCount ? sortedFilePair.firstSortedFile : sortedFilePair.secondSortedFile;
			String highestRankForPath = workerExt.getHighestRankForPath(new Path(HDFSInputFolder, largerFile.fileName));
			if (workerExt.getHostname().equals(highestRankForPath)) {
				return true;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return commitments.contains(sortedFilePair.getJobHash());
	}


}
