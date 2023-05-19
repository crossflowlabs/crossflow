//package org.crossflow.tests.techrank;
//
//import java.util.HashSet;
//
//public class RepositorySearchDispatcher extends RepositorySearchDispatcherBase {
//
//	protected HashSet<String> repositories = new HashSet<>();
//
//	@Override
//	public RepositorySearch consumeRepositories(Repository repository) throws Exception {
//		long start = System.currentTimeMillis();
//
//
//		if (!repositories.contains(repository.getPath())) {
//			repositories.add(repository.getPath());
//			getWorkflow().addWorkTime(System.currentTimeMillis() - start);
//			RepositorySearch repositorySearch = new RepositorySearch(repository.getPath(),
//					workflow.getTechnologySource().getTechnologyList(), repository);
//			repositorySearch.size = repository.size;
//			return repositorySearch;
//		}
//		getWorkflow().addWorkTime(System.currentTimeMillis() - start);
//		return null;
//	}
//
//}