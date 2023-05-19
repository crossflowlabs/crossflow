package org.crossflow.runtime;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Bid implements Serializable, Comparable<Bid> {
    private Job job;
    private double cost;
    private final String workerId;
    private final LocalDateTime bidTime;

    private final double jobProcessingTime;

    public Bid(Job job, double cost, String workerId, double jobProcessingTime) {
        this.job = job;
        this.cost = cost;
        this.workerId = workerId;
        this.jobProcessingTime = jobProcessingTime;
        this.bidTime = LocalDateTime.now();
    }

    public double getJobProcessingTime() {
        return jobProcessingTime;
    }

    public void decreaseCost(double cost) {
        this.cost = Math.max(0, this.cost - cost);
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }

    public Job getJob() {
        return job;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getWorkerId() {
        return workerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bid)) return false;

        Bid bid = (Bid) o;

        if (Double.compare(bid.getCost(), getCost()) != 0) return false;
        if (!getJob().equals(bid.getJob())) return false;
        return getWorkerId().equals(bid.getWorkerId());
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getJob().hashCode();
        temp = Double.doubleToLongBits(getCost());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + getWorkerId().hashCode();
        return result;
    }

    @Override
    public int compareTo(Bid o) {
        return Double.compare(this.cost, o.cost);
    }

    @Override
    public String toString() {
        return "Bid{" +
                "job=" + job +
                ", cost=" + cost +
                ", workerId='" + workerId + '\'' +
                '}';
    }
}
