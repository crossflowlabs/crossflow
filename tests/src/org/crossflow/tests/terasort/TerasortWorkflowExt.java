package org.crossflow.tests.terasort;

import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.crossflow.runtime.Mode;
import org.crossflow.runtime.utils.KVSRepository;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TerasortWorkflowExt extends TerasortWorkflow {

    // FileName -> JobHash
    private Map<String, JobMetadata> jobHashMap = new ConcurrentHashMap<>();
    private static final String HDFSResultFolder = "hdfs://localhost:9000/tera_result";
    private FileSystem fileSystem = HadoopConfiguration.getFileSystem();

    protected Timer timer;

    public TerasortWorkflowExt(Mode m) {
        super(m);
    }

    @Override
    public void run(long delay) throws Exception {
        super.run(delay);
        fileSystem.delete(new Path(HDFSResultFolder), true);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateFileLocations();
            }
        }, 140_000, Duration.ofSeconds(140).toMillis());
    }

    private void updateFileLocations() {
       // System.out.println("Updating file locations.");
        for (String filename : jobHashMap.keySet()) {
            JobMetadata jobMetadata = getJobMetadata(filename);
            Path path = new Path(jobMetadata.getHdfsFileLocation());
            try {
                Map<String, Integer> rankedHosts = rankFilePath(path);
                updatePreferredWorkersForJob(jobMetadata.getJobHash(), rankedHosts);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
       // System.out.println("Done updating file locations.");
    }

    private void updatePreferredWorkersForJob(String jobHash, Map<String, Integer> rankedHosts) {
        KVSRepository repository = getRepository();
        repository.setWorkerIdsForJob(jobHash, rankedHosts);
    }

    private Map<String, Integer> rankFilePath(Path path) throws IOException {
        Map<String, Integer> ranks = new HashMap<>();
        for (BlockLocation fileBlockLocation : fileSystem.getFileBlockLocations(path, 0, path.depth())) {
            int highestRank = fileBlockLocation.getHosts().length;
            for (String host: fileBlockLocation.getHosts()) {
                int currRank = ranks.getOrDefault(host, 0);
                ranks.put(host, currRank + highestRank);
                highestRank--;
            }
        }
        return ranks;
    }

    public void saveJobHash(String fileName, JobMetadata jobMetadata) {
        jobHashMap.put(fileName, jobMetadata);
    }

    public JobMetadata getJobMetadata(String filename) {
        return jobHashMap.get(filename);
    }

}
