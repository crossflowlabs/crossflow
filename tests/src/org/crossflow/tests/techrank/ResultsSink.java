package org.crossflow.tests.techrank;

import java.util.*;

public class ResultsSink extends ResultsSinkBase {
	
	protected HashMap<String, Integer> aggregate = new HashMap<>();
	protected Map<String, Set<String>> repoMatrix = new HashMap<>();

	@Override
	public void consumeRepositorySearchResults(RepositorySearchResult repositorySearchResult) throws Exception {
		long start = System.currentTimeMillis();
		String repository = repositorySearchResult.repository;
		putToRepoMatrix(repository, repositorySearchResult.getTechnology());
		getWorkflow().addWorkTime(System.currentTimeMillis() - start);
	}

	private void putToRepoMatrix(String repository, String technology) {
		Set<String> repoSet = repoMatrix.getOrDefault(repository, new HashSet<>());
		repoSet.add(technology);
		repoMatrix.put(repository, repoSet);
	}

	private void convertRepoMatrixToTechMatrix() {
		for (String repo : repoMatrix.keySet()) {
			Set<String> techSet = repoMatrix.get(repo);
			List<String> techs = new ArrayList<>(techSet);

			for (int i = 0; i < techs.size(); i++) {
				for (int j = i + 1; j < techs.size(); j++) {
					String tech1 = techs.get(i);
					String tech2 = techs.get(j);
					String key = tech1.compareTo(tech2) < 0 ? tech1 + "-" + tech2 : tech2 + "-" + tech1;
					aggregate.put(key, aggregate.getOrDefault(key, 0) + 1);
				}
			}
		}
	}

	public void printMatrix() {
		convertRepoMatrixToTechMatrix();
		aggregate.forEach((k, v) -> System.out.println(k + " -> " + v));
	}

}