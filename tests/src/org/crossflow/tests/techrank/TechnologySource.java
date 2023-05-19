package org.crossflow.tests.techrank;

import java.util.Arrays;
import java.util.List;

public class TechnologySource extends TechnologySourceBase {

	protected List<Technology> technologies;

	@Override
	public void produce() throws Exception {

		technologies = Arrays.asList(new Technology("eugenia", "gmf.node", "ecore"), new Technology("eol", "var", "eol"));

		while (!workflow.workerConnected()) {
			System.out.println("No workers connected, sleeping.");
			Thread.sleep(1_000);
		}

		for (Technology technology : technologies) {
			sendToTechnologies(technology);
		}

	}

	public List<Technology> getTechnologyList() {
		return technologies;
	}

}