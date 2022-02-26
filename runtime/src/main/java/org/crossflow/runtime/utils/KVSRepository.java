package org.crossflow.runtime.utils;

import redis.clients.jedis.Jedis;

import java.util.Optional;

public class KVSRepository {

    private final String KEY_PREFIX = "workflow:";

    private final Jedis jedis;

    public KVSRepository() {
        jedis = new Jedis();
    }

    public Optional<String> getWorkerId(String jobId) {
        Optional<String> result = Optional.ofNullable(jedis.get(KEY_PREFIX + jobId));
        System.out.println("[Debug] Get " + jobId + " -> " + result.orElse("NULL"));
        return result;
    }

    public void setWorkerId(String jobId, String workerId) {
        System.out.println("[Debug] Set: " + jobId + " -> " + workerId);
        jedis.set(KEY_PREFIX + jobId, workerId);
    }
}
