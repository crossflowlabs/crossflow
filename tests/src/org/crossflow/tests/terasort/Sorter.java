package org.crossflow.tests.terasort;


import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Sorter extends CommitmentSorterBase {
    private FileSystem fileSystem = HadoopConfiguration.getFileSystem();

    private String workerName = "worker";

    private final String HDFSInputFolder = "hdfs://localhost:9000/unsorted_files";
    private final String HDFSOutputFolder = "hdfs://localhost:9000/sorted_files";


    @Override
    public SortedFilePath consumeFilesForSort(UnsortedFilePath unsortedFilePath) throws Exception {
        long startTime = System.currentTimeMillis();

        InputStream fileInputStream = getFileInputStream(unsortedFilePath);

        String sortedFileName = unsortedFilePath.getResultFileName();
        Path hadoopFile = new Path(HDFSOutputFolder, sortedFileName);

        FSDataOutputStream currentFile = fileSystem.create(hadoopFile);

        BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
        reader.lines().sorted().forEach(line -> {
            try {
                currentFile.writeBytes(line + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        reader.close();
        currentFile.close();

        SortedFilePath sortedFilePathInst = new SortedFilePath();
        sortedFilePathInst.setFileName(sortedFileName);
        sortedFilePathInst.setLineCount(unsortedFilePath.lineCount);

        long totalTime = System.currentTimeMillis() - startTime;
        getWorkflow().addWorkTime(totalTime);
        return sortedFilePathInst;
    }



    private InputStream getFileInputStream(UnsortedFilePath unsortedFilePath) {
        try {
            Path hadoopFile = new Path(HDFSInputFolder, unsortedFilePath.fileName);
            return fileSystem.open(hadoopFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean hasCommited(UnsortedFilePath unsortedFilePath) {

        try {
            TerasortWorkflowWorkerExt workerExt = (TerasortWorkflowWorkerExt) getWorkflow();
            Path fullFilePath = new Path(HDFSInputFolder, unsortedFilePath.fileName);
            String highestRankForPath = workerExt.getHighestRankForPath(fullFilePath);
            if (workerExt.getHostname().equals(highestRankForPath)) {
                return true;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return commitments.contains(unsortedFilePath.getJobHash());
    }

}
