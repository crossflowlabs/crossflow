package org.crossflow.runtime.utils;

import org.crossflow.runtime.Job;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;
import java.util.stream.Collectors;

public class KVSRepository {

    private final String JOBS_KEY_PREFIX = "jobs:";
    private final String REJECTED_JOBS_KEY_PREFIX = "rejected:";
    private final String WORKER_KEY_PREFIX = "work:";

    private JedisPool pool;

    public KVSRepository(String hostname, int port) {
        pool = new JedisPool(hostname, port);
    }

    public KVSRepository() {
        pool = new JedisPool();
    }

    public List<String> getWorkerIds(String jobId) {
        try (Jedis jedis = pool.getResource()) {
            return jedis.zrange(JOBS_KEY_PREFIX + jobId, 0, -1);
        }
    }

    public void setWorkerId(String jobId, String workerId) {
        try (Jedis jedis = pool.getResource()) {
            jedis.zadd(JOBS_KEY_PREFIX + jobId, 1, workerId);
        }
    }

    public void setWorkerIdsForJob(String jobId, Map<String, Integer> rankedMap) {
        try (Jedis jedis = pool.getResource()) {
            jedis.zremrangeByScore(JOBS_KEY_PREFIX + jobId, 0, Double.MAX_VALUE);
            rankedMap.forEach((workerId, score) -> jedis.zadd(JOBS_KEY_PREFIX + jobId, score, workerId));
        }
    }

    public List<JobInfo> getJobsByWorker(String workerId) {
        if (workerId == null) {
            return Collections.EMPTY_LIST;
        }
        try (Jedis jedis = pool.getResource()) {
            return jedis.lrange(WORKER_KEY_PREFIX + workerId, 0, -1)
                        .stream()
                        .map(JobInfo::fromString)
                        .collect(Collectors.toList());
        }
    }

    public void addJobInfo(JobInfo jobInfo, String workerId) {
        try (Jedis jedis = pool.getResource()) {
            List<JobInfo> jobsByWorker = getJobsByWorker(workerId);
            if (jobsByWorker.stream().noneMatch(job -> job.getJobHash().equals(jobInfo.getJobHash()))) {
                jedis.rpush(WORKER_KEY_PREFIX + workerId, jobInfo.toJson());
            }
        }
    }

    public Optional<JobInfo> getJobInfoByHash(String workerId, String jobHash) {
        List<JobInfo> jobsByWorker = getJobsByWorker(workerId);
        return jobsByWorker.stream()
                .filter(jobInfo -> jobInfo.getJobHash().equals(jobHash))
                .findAny();
    }

    public boolean setJobStatus(String workerId, String jobHash, String status) {
        try (Jedis jedis = pool.getResource()) {
            List<JobInfo> jobsByWorker = getJobsByWorker(workerId);
            for (int i = 0; i < jobsByWorker.size(); i++) {
                JobInfo jobInfo = jobsByWorker.get(i);
                if (jobInfo.getJobHash().equals(jobHash)) {
                    jobInfo.setStatus(status);
                    jedis.lset(WORKER_KEY_PREFIX + workerId, i, jobInfo.toJson());
                    return true;
                }
            }
            return false;
        }
    }

    public void issueJob(Job job, String senderId, String destinationId) {
        JobInfo jobInfo = new JobInfo(job.getJobHash(), senderId, destinationId, job.getCost(), "ISSUED");
        addJobInfo(jobInfo, destinationId);
    }

    public void finishJob(Job job) {
        setJobStatus(job.getDesignatedWorkerId(), job.getJobHash(), "FINISHED");
    }
    //TODO 25.05. move from set to sorted set (based on updates made for cost function)
    public void rejectJobByWorkerId(String jobId, String workerId){
        try (Jedis jedis = pool.getResource()) {
            jedis.sadd(REJECTED_JOBS_KEY_PREFIX + jobId, workerId);
        }
    }

    public Set<String> getRejectedWorkerIds(String jobId){
        try (Jedis jedis = pool.getResource()) {
            return jedis.smembers(REJECTED_JOBS_KEY_PREFIX + jobId);
        }
    }

    public void flushAll() {
        try (Jedis jedis = pool.getResource()) {
            jedis.flushAll();
        }
    }
}
