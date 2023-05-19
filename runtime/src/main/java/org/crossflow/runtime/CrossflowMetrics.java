package org.crossflow.runtime;

import java.io.Serializable;
import java.time.LocalDateTime;

public class CrossflowMetrics implements Serializable {


    private final String repositoryName;
    private final long repositorySizeBytes;
    private final boolean cacheMiss;
    private final long bytesLoaded;
    private final String workerId;
    private final long downloadDuration;
    private final long ioProcessingDuration;
    private final LocalDateTime jobStartTime;
    private final double currentLatency;

    public CrossflowMetrics(long repositorySizeBytes, boolean cacheMiss, long bytesLoaded, String workerId, long downloadDuration, long ioProcessingDuration, String repositoryName, LocalDateTime jobStartTime, double currentLatency) {
        this.repositorySizeBytes = repositorySizeBytes;
        this.cacheMiss = cacheMiss;
        this.bytesLoaded = bytesLoaded;
        this.workerId = workerId;
        this.downloadDuration = downloadDuration;
        this.ioProcessingDuration = ioProcessingDuration;
        this.repositoryName = repositoryName;
        this.jobStartTime = jobStartTime;
        this.currentLatency = currentLatency;
    }

    public static String CSV_HEADER = "repositoryName,repositorySizeBytes,cacheMiss,bytesLoaded,workerId,downloadDuration,ioProcessingDuration,jobStartTime,currentLatency,job_conf,worker_conf,iteration,algorithm";

    public String toCsv() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", repositoryName, repositorySizeBytes, cacheMiss, bytesLoaded, workerId, downloadDuration, ioProcessingDuration, jobStartTime, currentLatency);
    }

}
