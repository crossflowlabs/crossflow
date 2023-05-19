package org.crossflow.tests.techrankBackup;


public class RepositorySearchDispatcher extends RepositorySearchDispatcherBase {
	
	@Override
	public RepositorySearch consumeRepositories(Repository repository) throws Exception {
		
		RepositorySearch repositorySearchInst = new RepositorySearch();
		//	repositorySearchInst.setRepository( String );
		//	repositorySearchInst.setTechnologies( Technology );
		return repositorySearchInst;
	
	}


}
