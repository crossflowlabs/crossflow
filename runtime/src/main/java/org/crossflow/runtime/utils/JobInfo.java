package org.crossflow.runtime.utils;

import com.google.gson.Gson;

public class JobInfo {
    private String jobHash;
    private String assignedBy;
    private String assignedTo;
    private double cost;
    private String status;

    private static Gson gson = new Gson();

    public JobInfo(String jobHash, String assignedBy, String assignedTo, double cost, String status) {
        this.jobHash = jobHash;
        this.assignedBy = assignedBy;
        this.assignedTo = assignedTo;
        this.cost = cost;
        this.status = status;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static JobInfo fromString(String jsonString) {
        return gson.fromJson(jsonString, JobInfo.class);
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public String getJobHash() {
        return jobHash;
    }

    public void setJobHash(String jobHash) {
        this.jobHash = jobHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JobInfo)) return false;
        JobInfo jobInfo = (JobInfo) o;
        return getJobHash().equals(jobInfo.getJobHash());
    }
}
