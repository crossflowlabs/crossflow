package org.crossflow.tests.techrank;

import org.crossflow.runtime.CrossflowMetricsBuilder;
import org.eclipse.jgit.api.Git;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class RepositorySearcher extends CommitmentRepositorySearcherBase {


    private TechrankWorkflowContext context;

    private CrossflowMetricsBuilder currentMetricBuilder;

    public RepositorySearchResult fakeSearch(Repository repository) throws Exception {
        LocalDateTime startTime = LocalDateTime.now();

        currentMetricBuilder = new CrossflowMetricsBuilder();
        currentMetricBuilder
                .setWorkerId(getWorkflow().getName())
                .setRepositoryName(repository.path)
                .setRepositorySizeBytes(repository.size)
                .setJobStartTime(startTime)
                .setCurrentLatency(((TechrankWorkflowExt) workflow).getLatencySec());

        RepositorySearchResult result = new RepositorySearchResult();

        long start = System.currentTimeMillis();
        if (!alreadyDownloaded(repository)) {
            fakeDownload(repository);
            ((TechrankWorkflowExt) getWorkflow()).addCacheData(1, repository.size);
        }

        fakeIOProcess(repository);
        getWorkflow().sendMetric(currentMetricBuilder.createCrossflowMetrics());
        ((TechrankWorkflowExt) getWorkflow()).addLocalWorkTime(System.currentTimeMillis() - start);
        ((TechrankWorkflowExt) workflow).reportJobFinish(repository, System.currentTimeMillis() - start);
        return result;
    }

    public RepositorySearchResult actuallySearch(Repository repository) throws Exception {
        context = new TechrankWorkflowContext(workflow);
        String parentFolder = context.getProperties().getProperty("clones") + "/" + workflow.getName();

        if (!new File(parentFolder).exists()) {
            new File(parentFolder).mkdirs();
        }

        long start = System.currentTimeMillis();
        LocalDateTime startTime = LocalDateTime.now();

        long downloadTime = -1;
        int cacheMisses = 0;

        File clone = new File(parentFolder + "/" + UUID.nameUUIDFromBytes(repository.getPath().getBytes()));

        if (!clone.exists()) {
            cacheMisses++;
            ((TechrankWorkflowExt) getWorkflow()).addCacheData(1, repository.size);

            try {
                // Try the command-line option first as it supports --depth 1
                Process process = Runtime.getRuntime().exec("git clone --depth 1 " + "https://github.com/" +
                        repository.getPath() + ".git " + clone.getAbsolutePath());
                process.waitFor();

                downloadTime = System.currentTimeMillis() - start;
            } catch (Exception ex) {
                System.out.println("Falling back to JGit because " + ex.getMessage());
                Git.cloneRepository()
                        .setURI("https://github.com/" + repository.getPath() + ".git")
                        .setDirectory(clone)
                        .call();
            }
        }

        currentMetricBuilder = new CrossflowMetricsBuilder();
        currentMetricBuilder
                .setWorkerId(getWorkflow().getName())
                .setRepositoryName(repository.path)
                .setRepositorySizeBytes(repository.size)
                .setCurrentLatency(((TechrankWorkflowExt) workflow).getLatencySec())
                .setJobStartTime(startTime);


        long ioProcessTime = 0;
        for (Technology technology : repository.getTechnologies()) {


            if (countFiles(clone, technology) > 0) {
                ioProcessTime += System.currentTimeMillis() - ioProcessTime;



                RepositorySearchResult result = new RepositorySearchResult();
                result.setRepository(repository.getPath());
                RepositorySearchResult repositorySearchResult = new RepositorySearchResult(technology.getName(), 1, repository.path);
                sendToRepositorySearchResults(repositorySearchResult);
            }

            /* TODO: See why grep has stopped working (it returns 0 results even when the terminal says otherwise
			try {
				String grep = "grep -r -l --include=\"*." + technology.getExtension() + "\" \"" +
						technology.getKeyword() + "\" " + clone.getAbsolutePath();

				System.out.println("Grepping: " + grep);

				Process process = Runtime.getRuntime().exec(grep);


				BufferedReader processInputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
				BufferedReader processErrorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));


				int files = 0;
				String s;
				while ((s = processInputStream.readLine()) != null) {
					System.out.println("Found: " + s);
					files++;
				}

				String e;
				while ((e = processErrorStream.readLine()) != null) {
					System.out.println("Error: " + e);
				}



				RepositorySearchResult result = new RepositorySearchResult(technology.getName(), files, repositorySearch);
				sendToRepositorySearchResults(result);

			}
			catch (Exception ex) {
				System.out.println("Falling back to file-by-file searching because " + ex.getMessage());*/
//			RepositorySearchResult repositorySearchResult = new RepositorySearchResult(technology.getName(), 1, repositorySearch.repository);
//			sendToRepositorySearchResults(repositorySearchResult);
            //}
        }

        currentMetricBuilder
                .setDownloadDuration(downloadTime)
                .setCacheMiss(cacheMisses > 0)
                .setIoProcessingDuration(ioProcessTime);

        getWorkflow().sendMetric(currentMetricBuilder.createCrossflowMetrics());
        long execTimeMs = System.currentTimeMillis() - start;


        ((TechrankWorkflowExt) getWorkflow()).addLocalWorkTime(execTimeMs);
        ((TechrankWorkflowExt) workflow).reportJobFinish(repository, execTimeMs);

        getWorkflow().addWorkTime(execTimeMs);
        return null;
    }

    @Override
    public RepositorySearchResult consumeRepositories(Repository repository) throws Exception {
//        RepositorySearchResult repositorySearchResult = fakeSearch(repository);
        RepositorySearchResult repositorySearchResult = actuallySearch(repository);
        workflow.getJobsWaiting().remove(repository.getJobId());
        return repositorySearchResult;
    }

    private void fakeDownload(Repository repository) throws Exception {
        long speed = ((TechrankWorkflowExt) workflow).getNetSpeed();
        long sleepTime = repository.size / speed * 1000;

        System.out.println("Worker " + getWorkflow().getName() + " downloading " + repository.getPath() + " for " + sleepTime + " ms.");

        /*
		TODO:
		    - Cache misses
    		- Data load (sum of downloaded repositories)
		* */

        currentMetricBuilder
                .setDownloadDuration(sleepTime)
                .setBytesLoaded(repository.size)
                .setCacheMiss(true);

        Thread.sleep(sleepTime);
        markAsDownloaded(repository);
    }

    private void markAsDownloaded(Repository repository) {
        ((TechrankWorkflowExt) workflow).downloaded.add(repository.path);
    }

    private boolean alreadyDownloaded(Repository repository) {
        return ((TechrankWorkflowExt) workflow).downloaded.contains(repository.path);
    }

    private void fakeIOProcess(Repository repository) throws Exception {
        long speed = ((TechrankWorkflowExt) workflow).getIOSpeed();
        long sleepTime = repository.size / speed * 1000;

        System.out.println("Worker " + getWorkflow().getName() + " io processing " + repository.getPath() + " for " + sleepTime + " ms.");

        Thread.sleep(sleepTime);

        currentMetricBuilder.setIoProcessingDuration(sleepTime);
    }

    private int countAllFiles(File clone) {
        if (clone.isDirectory()) {
            return Arrays.asList(clone.listFiles()).stream().filter(f ->
                    !f.isDirectory()).collect(Collectors.toList()).size() +
                    Arrays.asList(clone.listFiles()).stream().filter(f -> f.isDirectory() && !f.getName().equals(".git")).
                            mapToInt(f -> countAllFiles(f)).sum();
        } else return 0;
    }

    @Override
    public boolean hasCommited(Repository input) {

//		try {
//			context = new TechrankWorkflowContext(workflow);
//			File clone = new File(context.getProperties().getProperty("clones") + "/" + workflow.getName() + "/" + UUID.nameUUIDFromBytes(input.getPath().getBytes()));
//			if (clone.exists()) return true;
//
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		return commitments.contains(input.getJobHash());

        return true;
    }

    protected int countFiles(File directory, Technology technology) {
        if (directory.isDirectory()) {
            return Arrays.asList(directory.listFiles()).stream().filter(f ->
                    !f.isDirectory() && conforms(f, technology)).collect(Collectors.toList()).size() +
                    Arrays.asList(directory.listFiles()).stream().filter(f -> f.isDirectory() && !f.getName().equals(".git")).
                            mapToInt(f -> countFiles(f, technology)).sum();
        } else return 0;
    }

    protected boolean conforms(File file, Technology technology) {
        try {
            return file.getName().endsWith(technology.getExtension()) && new String(Files.readAllBytes(Paths.get(file.toURI()))).indexOf(technology.getKeyword()) > -1;
        } catch (IOException e) {
            workflow.reportInternalException(e);
            return false;
        }
    }

}