package org.crossflow.tests.terasort;

import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.crossflow.runtime.Job;
import org.crossflow.runtime.Mode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class TerasortWorkflowWorkerExt extends TerasortWorkflow {

    private FileSystem fileSystem = HadoopConfiguration.getFileSystem();
    private String hostname;

    public TerasortWorkflowWorkerExt(Mode m) {
        super(m);
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getHostname() {
        return hostname;
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

    public String getHighestRankForPath(Path path) {
        try {
            Map<String, Integer> rankMap = rankFilePath(path);
            AtomicReference<String> highestHost = new AtomicReference<>("");
            AtomicInteger highestRank = new AtomicInteger(-1);
            rankMap.forEach((host, rank) -> {
                if (rank > highestRank.get()) {
                    highestHost.set(host);
                    highestRank.set(rank);
                }
            });
            return highestHost.get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
