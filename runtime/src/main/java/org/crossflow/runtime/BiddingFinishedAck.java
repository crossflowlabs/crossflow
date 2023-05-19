package org.crossflow.runtime;

import java.io.Serializable;

public class BiddingFinishedAck implements Serializable {
    private String workerId;
    private String jobId;

    public BiddingFinishedAck(String workerId, String jobId) {
        this.workerId = workerId;
        this.jobId = jobId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
