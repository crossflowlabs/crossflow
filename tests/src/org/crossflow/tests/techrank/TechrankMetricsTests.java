package org.crossflow.tests.techrank;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.crossflow.runtime.Mode;
import org.crossflow.tests.WorkflowTests;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TechrankMetricsTests extends WorkflowTests {
    private static final int NUMBER_OF_WORKERS = 3;
    public static final String INPUT_FOLDER = "experiment/techrank/in";
    public static final String OUTPUT_FOLDER = "experiment/techrank/out";
    @Before
    public void clearRedis() throws Exception {
        TechrankWorkflowExt clear = new TechrankWorkflowExt(Mode.MASTER_BARE);
        //clear.getRepository().flushAll();
        clear.deleteCachedRepositories();
    }

    @Test
    public void testConfOneFast_allDiff10_10_100() throws Exception {
        String algorithm = "bidding";
        String jobConf = "all_diff_10_10_100";
        String workerConf = "conf_one_fast";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfAllEq_allDiff10_10_100() throws Exception {
        String algorithm = "bidding";
        String jobConf = "all_diff_10_10_100";
        String workerConf = "conf_all_equal";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfOneSlow_allDiff10_10_100() throws Exception {
        String algorithm = "bidding";
        String jobConf = "all_diff_10_10_100";
        String workerConf = "conf_one_slow";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfFastSlow_allDiff10_10_100() throws Exception {
        String algorithm = "bidding";
        String jobConf = "all_diff_10_10_100";
        String workerConf = "conf_slow_fast";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfOneFast_allDiff100_10_10() throws Exception {
        String algorithm = "bidding";
        String jobConf = "all_diff_100_10_10";
        String workerConf = "conf_one_fast";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfAllEq_allDiff100_10_10() throws Exception {
        String algorithm = "bidding";
        String jobConf = "all_diff_100_10_10";
        String workerConf = "conf_all_equal";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfOneSlow_allDiff100_10_10() throws Exception {
        String algorithm = "bidding";
        String jobConf = "all_diff_100_10_10";
        String workerConf = "conf_one_slow";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfFastSlow_allDiff100_10_10() throws Exception {
        String algorithm = "bidding";
        String jobConf = "all_diff_100_10_10";
        String workerConf = "conf_slow_fast";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfOneFast_allDiff40_40_40() throws Exception {
        String algorithm = "bidding";
        String jobConf = "all_diff_40_40_40";
        String workerConf = "conf_one_fast";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfAllEq_allDiff40_40_40() throws Exception {
        String algorithm = "bidding";
        String jobConf = "all_diff_40_40_40";
        String workerConf = "conf_all_equal";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfOneSlow_allDiff40_40_40() throws Exception {
        String algorithm = "bidding";
        String jobConf = "all_diff_40_40_40";
        String workerConf = "conf_one_slow";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfFastSlow_allDiff40_40_40() throws Exception {
        String algorithm = "bidding";
        String jobConf = "all_diff_40_40_40";
        String workerConf = "conf_slow_fast";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfOneFast_80PercentLarge() throws Exception {
        String algorithm = "bidding";
        String jobConf = "80_percent_large_10_10_100";
        String workerConf = "conf_one_fast";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfAllEq_80PercentLarge() throws Exception {
        String algorithm = "bidding";
        String jobConf = "80_percent_large_10_10_100";
        String workerConf = "conf_all_equal";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfOneSlow_80PercentLarge() throws Exception {
        String algorithm = "bidding";
        String jobConf = "80_percent_large_10_10_100";
        String workerConf = "conf_one_slow";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfFastSlow_80PercentLarge() throws Exception {
        String algorithm = "bidding";
        String jobConf = "80_percent_large_10_10_100";
        String workerConf = "conf_slow_fast";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfOneFast_80PercentSmall() throws Exception {
        String algorithm = "bidding";
        String jobConf = "80_percent_small_100_10_10";
        String workerConf = "conf_one_fast";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfAllEq_80PercentSmall() throws Exception {
        String algorithm = "bidding";
        String jobConf = "80_percent_small_100_10_10";
        String workerConf = "conf_all_equal";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfOneSlow_80PercentSmall() throws Exception {
        String algorithm = "bidding";
        String jobConf = "80_percent_small_100_10_10";
        String workerConf = "conf_one_slow";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    @Test
    public void testConfFastSlow_80PercentSmall() throws Exception {
        String algorithm = "bidding";
        String jobConf = "80_percent_small_100_10_10";
        String workerConf = "conf_slow_fast";

        runTechrankTests(algorithm, jobConf, workerConf);
    }

    private void runTechrankTests(String algorithm, String jobConf, String workerConf) throws Exception {
        for (int i = 0; i < 1; i++) {
            TechrankWorkflowExt master = new TechrankWorkflowExt(Mode.MASTER_BARE);
            master.createBroker(false);
            master.setMaster("localhost");
            master.setInputDirectory(new File(INPUT_FOLDER));
            master.setOutputDirectory(new File(OUTPUT_FOLDER));
            master.setInstanceId("techrank" + i);
            master.setName("techrank-master");
            master.setIteration(i + 1);
            master.setAlgorithm(algorithm);
            master.setJobConf(jobConf);
            master.setWorkerConf(workerConf);

            TechrankWorkerConfiguration[] configurations = getTechrankConfigurationsFromJson(workerConf + ".json");

            List<TechrankWorkflowExt> workers = new ArrayList<>();
            TechrankWorkflowExt worker;

            for (TechrankWorkerConfiguration configuration : configurations) {
                worker = new TechrankWorkflowExt(Mode.WORKER);
                worker.setName(configuration.getName());
                worker.setInstanceId("techrank" + i);
                worker.setInputDirectory(new File(INPUT_FOLDER));
                worker.setOutputDirectory(new File(OUTPUT_FOLDER));
                worker.setNet_bytesPerSecond(configuration.getNetSpeedBps());
                worker.setIo_bytesPerSecond(configuration.getIoSpeedBps());
                worker.loadDownloadedRepositories();
                worker.setIteration(i + 1);
                worker.setAlgorithm(algorithm);
                worker.setJobConf(jobConf);
                worker.setWorkerConf(workerConf);
                worker.repoInputFile = "experiment/techrank/in/" + jobConf + ".csv";
                workers.add(worker);
            }

            long init = System.currentTimeMillis();
            master.run();

            for (TechrankWorkflow w : workers) {
                w.run(0);
            }

            waitFor(master);

            long execTimeSecond = (System.currentTimeMillis() - init) / 1000;
            String stats = "normal execution time: " + execTimeSecond + "s";
            master.printMasterStatistics(stats);
            assertEquals(1000, 1000);

            master.stopQueues();
            for (TechrankWorkflow w : workers) {
                w.stopQueues();
            }
        }
    }

    private static TechrankWorkerConfiguration[] getTechrankConfigurationsFromJson(String confFileName) throws IOException {
        String filePath = String.format("experiment/techrank/in/%s", confFileName);
        Gson gson = new Gson();
        return gson.fromJson(FileUtils.readFileToString(new File(filePath)), TechrankWorkerConfiguration[].class);
    }
}
