package org.crossflow.tests.terasort;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;

import java.io.IOException;

public class HadoopConfiguration {
    public static FileSystem getFileSystem() {
        Configuration configuration = getConfiguration();
        try {
            return FileSystem.get(configuration);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Configuration getConfiguration() {
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", "hdfs://localhost:9000/");
        configuration.set("dfs.datanode.use.datanode.hostname", "true");
        configuration.set("dfs.client.use.datanode.hostname", "true");
        return configuration;
    }
}
