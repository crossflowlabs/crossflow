package org.crossflow.runtime;

import java.io.Serializable;
import java.util.List;

public class BidOffer implements Serializable {
    private Job job;

    public BidOffer(Job job) {
        this.job = job;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    @Override
    public String toString() {
        return "BidOffer{" +
                "job=" + job +
                '}';
    }
}
