package org.crossflow.tests.techrank;

import org.crossflow.runtime.Job;
import org.crossflow.runtime.Mode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TechrankWorkflowExt extends TechrankWorkflow {

    private static final int NUM_PAGES = 10;
    private static final int SLEEP_PER_PAGE_SECONDS = 60;
    public static final double GITHUB_API_SEARCH_TIME_SECONDS = NUM_PAGES * SLEEP_PER_PAGE_SECONDS;
    private final String writerFileName;
    public long net_bytesPerSecond = 1 << 21; // 2MB / s
    public long io_bytesPerSecond = 1 << 27; // 128 MB / s
    public Set<String> downloaded = new HashSet<>();
    private double latencySec = 0;
    private double correctionFactor = 1.0;

    private FileWriter writer;
    public String repoInputFile;
    private int cacheMisses = 0;

    public long workTimeMillis;
    private BigInteger bytesLoaded = BigInteger.ZERO;

    private List<Double> correctionFactors = new ArrayList<>();
    private boolean weightedFactors;

    public void setWeightedFactors() {
        this.weightedFactors = true;
    }

    public TechrankWorkflowExt(Mode m) {
        super(m);
        writerFileName = String.format("/Users/ana/Desktop/repositories_%s.txt", getInstanceId());
    }

    public void updateLatencySec(double newLatency) {
        this.latencySec = newLatency;
    }

    @Override
    public synchronized void terminate() {
        if (getResultsSink() != null) {
            this.getResultsSink().printMatrix();
        }
        saveDownloadedRepositories();
        if (!isMaster()) printWorkerStatistics();
        if (isMaster()) {
            saveMetricsCsv();
        }
        super.terminate();
    }


    @Override
    protected double calculateJobCost(Job job) {
        if (job instanceof Repository) {
            Repository repository = (Repository) job;
            return calculateRepositoryBidCost(repository);
        }

        if (job instanceof RepositorySearchResult) {
            return 1.0;
        }
        if (job instanceof Technology) {
            Technology technology = (Technology) job;
            return calculateTechnologyCost(technology);
        }
        return Double.MAX_VALUE;
    }

    @Override
    protected double calculateProcessingTime(Job job) {
        if (job instanceof Repository) {
            Repository repository = (Repository) job;
            return calculateRepositoryProcessingCost(repository);
        }

        if (job instanceof RepositorySearchResult) {
            return 1.0;
        }
        if (job instanceof Technology) {
            Technology technology = (Technology) job;
            return calculateTechnologyCost(technology);
        }
        return Double.MAX_VALUE;
    }

    private double calculateRepositoryProcessingCost(Repository repository) {
        double cost = 1.0 * (repository.size / getIOSpeed());

        if (isLocalResource(repository)) {
            // TODO check if needed
            repository.setCost(cost);
//            double bidOffer = workloadCost + cost;// * correctionFactor;
            return cost * meanCorrectionFactor();
        }
        cost += 1.0 * (repository.size / getNetSpeed());
        // TODO check if needed
        repository.setCost(cost);
//        double bidOffer = cost + workloadCost;// * correctionFactor;
        return cost + meanCorrectionFactor();
    }

    @Override
    protected double queuedJobsTime() {
        return currentWorkloadCost();
    }

    private double calculateTechnologyCost(Technology technology) {
        return currentWorkloadCost() + GITHUB_API_SEARCH_TIME_SECONDS;
    }

    private double currentWorkloadCost() {
        return jobsWaiting
                .values()
                .stream()
                .mapToDouble(Job::getCost)
                .sum();
    }

    private double calculateRepositoryBidCost(Repository repository) {
        double cost = 1.0 * (repository.size / getIOSpeed());

        double workloadCost = currentWorkloadCost();

        if (isLocalResource(repository)) {
            // TODO check if needed
            repository.setCost(cost);
//            double bidOffer = workloadCost + cost;// * correctionFactor;
            double bidOffer = workloadCost + cost * meanCorrectionFactor();
            System.out.println("bidOffer = " + bidOffer);
            return bidOffer;
        }

        cost += 1.0 * (repository.size / getNetSpeed());
        // TODO check if needed
        repository.setCost(cost);
//        double bidOffer = cost + workloadCost;// * correctionFactor;
        double bidOffer = cost + workloadCost * meanCorrectionFactor();
        System.out.println("bidOffer = " + bidOffer);
        return bidOffer;
    }

    private boolean isLocalResource(Repository repository) {
        return downloaded.contains(repository.path);
    }

    public void loadDownloadedRepositories() {
        if (isMaster()) {
            return;
        }

        try {
            File file = new File(String.format("experiment/techrank/in/%s_downloaded_repositories.txt", getName()));
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    downloaded.add(line);
                }
                scanner.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveDownloadedRepositories() {
        try {
            File file = new File(String.format("experiment/techrank/in/%s_downloaded_repositories.txt", getName()));
            FileWriter writer = new FileWriter(file);
            for (String path : downloaded) {
                writer.write(path + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private boolean isLocalResource(Repository repository) {
//        try {
//            TechrankWorkflowContext context = new TechrankWorkflowContext(this);
//            File file = new File(context.getProperties().getProperty("clones") + "/" + getName() + "/" + UUID.nameUUIDFromBytes(repository.getPath().getBytes()));
//            return file.exists();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    public void setNet_bytesPerSecond(long bytesPerSecond) {
        this.net_bytesPerSecond = bytesPerSecond;
    }

    public void setIo_bytesPerSecond(long io_bytesPerSecond) {
        this.io_bytesPerSecond = io_bytesPerSecond;
    }

//    public void reportJobFinish(Job job, double actualExecTime) {
//        // TODO: Razmisli o tome da li treba += ili =
//        latencySec += job.getCost() - actualExecTime;
//        correctionFactor *= getAdjustedCorrectionFactor(job.getCost(), actualExecTime);
//        roundCorrectionFactor();
//    }

    public synchronized void reportJobFinish(Job job, double actualExecTime) {
        double estimatedCost = estimatedBids.getOrDefault(job.getJobId(), 0.0);
        estimatedBids.remove(job.getJobId());
        jobsWaiting.remove(job.getJobId());
        addCorrectionFactor(getAdjustedCorrectionFactor(estimatedCost, actualExecTime));
    }


    private void roundCorrectionFactor() {
        if (correctionFactor < 0.025) {
            correctionFactor = 0.025;
        }
        if (correctionFactor > 4) {
            correctionFactor = 4;
        }
    }

    private double getAdjustedCorrectionFactor(double jobCost, double actualExecTime) {

        // Convert to seconds
        actualExecTime /= 1000;

        // Prevent division by zero
        if (jobCost == 0) {
            jobCost = 0.00001;
        }
        double cf = actualExecTime / jobCost;
        if (cf < 0.025) {
            cf = 0.025;
        }
        if (cf > 4) {
            cf = 4;
        }
        return cf;
    }

    public void appendStringToFile(String text) {

        try {
            writer = new FileWriter(writerFileName, true);
            writer.write(text + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public double getLatencySec() {
        return latencySec;
    }

    public String repoInputFile() {
        return repoInputFile;
    }

    public void printLocalWorkloadStatistics() {
        System.out.print(getName() + ": cachemisses: " + cacheMisses + ", dataloaded: " + bytesLoaded + ", worktime: " + workTimeMillis + "\n");
    }

    public void addLocalWorkTime(long milis) {
        workTimeMillis += milis;
    }

    public void addCacheData(int numMisses, long dataLoaded) {
        cacheMisses += numMisses;
        bytesLoaded = bytesLoaded.add(BigInteger.valueOf(dataLoaded));
    }

    public void printWorkerStatistics() {
        String fileName = String.format("statistics-i%s-%s-%s-%s-%s.txt", iteration, algorithm, jobConf, workerConf, getName());
        try {
            Files.write(Paths.get(fileName), getWorkerStatistics().getBytes());
            System.out.println("Saved " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getWorkerStatistics() {
        return String.format("workername: %s, cachemisses: %s, dataloaded: %s, worktime: %s", getName(), cacheMisses, bytesLoaded, workTimeMillis);
    }

    public void printMasterStatistics(String masterStatistics) {
        String fileName = String.format("statistics-i%s-%s-%s-%s-master.txt", iteration, algorithm, jobConf, workerConf);
        try {
            Files.write(Paths.get(fileName), masterStatistics.getBytes());
            System.out.println("Saved " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addCorrectionFactor(double cf) {
        correctionFactors.add(cf);
    }

    public long getNetSpeed() {
        return SpeedUtils.getSpeed(net_bytesPerSecond);
    }

    public long getIOSpeed() {
        return SpeedUtils.getSpeed(io_bytesPerSecond);
    }

    public synchronized double meanCorrectionFactor() {
        if (weightedFactors) {
            return weightedCorrectionFactor();
        }
        return correctionFactors
                .stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(1.0);
    }

    private double weightedCorrectionFactor() {
        if (correctionFactors.isEmpty()) {
            return 1.0;
        }

        double sum = 0.0;
        double weight = 0.0;
        for (int i = 0; i < correctionFactors.size(); i++) {
            double currentValue = correctionFactors.get(i);
            double currentWeight = i + 1;
            sum += currentValue * currentWeight;
            weight += currentWeight;
        }
        return sum / weight;

    }
}
