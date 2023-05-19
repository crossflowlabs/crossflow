package org.crossflow.runtime;

import java.io.Serializable;

public class WinningBid implements Serializable {
    private final String jobId;
    private final String workerId;

    private final double cost;

    public WinningBid(String jobId, String workerId, double cost) {
        this.jobId = jobId;
        this.workerId = workerId;
        this.cost = cost;
    }

    public String getJobId() {
        return jobId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public double getCost() {
        return cost;
    }
}
