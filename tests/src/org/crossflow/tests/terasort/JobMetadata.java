package org.crossflow.tests.terasort;

public class JobMetadata {
    private String jobHash;
    private String hdfsFileLocation;

    public JobMetadata(String jobHash, String hdfsFileLocation) {
        this.jobHash = jobHash;
        this.hdfsFileLocation = hdfsFileLocation;
    }

    public String getJobHash() {
        return jobHash;
    }

    public void setJobHash(String jobHash) {
        this.jobHash = jobHash;
    }

    public String getHdfsFileLocation() {
        return hdfsFileLocation;
    }

    public void setHdfsFileLocation(String hdfsFileLocation) {
        this.hdfsFileLocation = hdfsFileLocation;
    }
}
