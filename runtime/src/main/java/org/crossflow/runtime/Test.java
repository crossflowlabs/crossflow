package org.crossflow.runtime;

import java.time.LocalDateTime;

public class Test {
    public static void main(String[] args) {
        CrossflowMetricsBuilder builder = new CrossflowMetricsBuilder();
        builder.setBytesLoaded(1)
                .setIoProcessingDuration(2)
                .setRepositoryName("github.com/github/codeql")
                .setCurrentLatency(11.5)
                .setJobStartTime(LocalDateTime.now());

        System.out.println(builder.createCrossflowMetrics().toCsv());
    }
}
