package org.crossflow.tests.techrank;

import org.kohsuke.github.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.*;

public class GitHubCodeSearcher extends GitHubCodeSearcherBase {

    public static final long BURST_SLEEP_MILLIS = 2000L;
    public static final int BURST_SIZE = 3;
    protected GitHub github = null;

    List<Technology> technologies = Arrays.asList(new Technology("eugenia", "gmf.node", "ecore"), new Technology("eol", "var", "eol"));

    @Override
    public Repository consumeTechnologies(Technology technology) throws Exception {

//        loadRepositoriesForTechnology(technology)
//                .forEach(this::sendToRepositories);
//        return null;
//    }

        long start = System.currentTimeMillis();

        System.out.println(workflow.getName() + " working on technology " + technology.getName());

        if (github == null) connectToGitHub();


        PagedSearchIterable<GHContent> it = github.searchContent()
                .extension(technology.getExtension())
                .q(technology.getKeyword())
//                .size("10000..11000")
                .list();
        PagedIterator<GHContent> pit = it._iterator(100);

        List<GHContent> contents;

        do {
            GHRateLimit rateLimit = github.getRateLimit();
            long epochSecond = Instant.now().getEpochSecond();
            long sleepTime = rateLimit.getSearch().getResetEpochSeconds() - epochSecond;
            long sleepTimeMillis = (sleepTime + 2) * 1000;

            if (!pit.hasNext()) {
                System.out.println("Ain't got next");
                break;
            }
            contents = pit.nextPage();
            for (GHContent content : contents) {
                Repository repo = new Repository();
                repo.path = content.getOwner().getFullName();
                GHRepository r = github.getRepository(repo.path);

                repo.size = (long) r.getSize() << 10;
                //System.out.println("content.getSize() = " + (r.getSize() << 10));
                repo.setCorrelationId(repo.getPath());
                repo.setTechnologies(technologies);
                sendToRepositories(repo);
            }

            Thread.sleep(sleepTimeMillis);
        } while (contents.size() != 0);


        long execTimeMs = System.currentTimeMillis() - start;
        getWorkflow().addWorkTime(execTimeMs);
        double execTimeSeconds = execTimeMs / 1000.0;
        ((TechrankWorkflowExt) workflow).reportJobFinish(technology, execTimeSeconds);
        return null;
    }

    protected void connectToGitHub() throws Exception {
        Properties properties = new TechrankWorkflowContext(workflow).getProperties();

        github = GitHubBuilder.fromProperties(properties)
                .build();
    }

    private List<Repository> loadRepositoriesForTechnology(Technology technology) throws Exception {
        List<Repository> jobs = new ArrayList<>();
        String fileName = ((TechrankWorkflowExt) getWorkflow()).repoInputFile;
        try (Scanner reader = new Scanner(new File(fileName))) {
            reader.nextLine(); // skip header
            while (reader.hasNextLine()) {
                String input = reader.nextLine();
                String[] split = input.split(",");
                if (split[2].equals(technology.extension)) {
                    jobs.add(new Repository(split[0], Long.parseLong(split[1]), technologies));
                }
            }
            return jobs;
        }
    }

}