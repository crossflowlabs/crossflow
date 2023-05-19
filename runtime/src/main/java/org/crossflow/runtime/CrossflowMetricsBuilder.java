package org.crossflow.runtime;

import java.time.LocalDateTime;

public class CrossflowMetricsBuilder {
    private boolean cacheMiss;
    private long bytesLoaded;
    private String workerId;
    private long downloadDuration;
    private long ioProcessingDuration;
    private String repositoryName;
    private LocalDateTime jobStartTime;
    private double currentLatency;
    private long repositorySizeBytes;

    public CrossflowMetricsBuilder setCacheMiss(boolean cacheMiss) {
        this.cacheMiss = cacheMiss;
        return this;
    }

    public CrossflowMetricsBuilder setBytesLoaded(long bytesLoaded) {
        this.bytesLoaded = bytesLoaded;
        return this;
    }

    public CrossflowMetricsBuilder setWorkerId(String workerId) {
        this.workerId = workerId;
        return this;
    }

    public CrossflowMetricsBuilder setDownloadDuration(long downloadDuration) {
        this.downloadDuration = downloadDuration;
        return this;
    }

    public CrossflowMetricsBuilder setIoProcessingDuration(long ioProcessingDuration) {
        this.ioProcessingDuration = ioProcessingDuration;
        return this;
    }

    public CrossflowMetricsBuilder setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
        return this;
    }

    public CrossflowMetricsBuilder setJobStartTime(LocalDateTime jobStartTime) {
        this.jobStartTime = jobStartTime;
        return this;
    }

    public CrossflowMetricsBuilder setCurrentLatency(double currentLatency) {
        this.currentLatency = currentLatency;
        return this;
    }

    public CrossflowMetricsBuilder setRepositorySizeBytes(long repositorySizeBytes) {
        this.repositorySizeBytes = repositorySizeBytes;
        return this;
    }
    public CrossflowMetrics createCrossflowMetrics() {
        return new CrossflowMetrics(repositorySizeBytes, cacheMiss, bytesLoaded, workerId, downloadDuration, ioProcessingDuration, repositoryName, jobStartTime, currentLatency);
    }

}