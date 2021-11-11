package org.crossflow.tests.opinionated;

public class OccurencesMonitor extends OpinionatedOccurencesMonitorBase {

	protected int occurences = 0;
	private String favouriteWord;

	@Override
	public Occs consumeWords(Word word) throws Exception {
		//System.out.println("consumeWords: " + workflow.getName() + " :: " + word.w);
		occurences++;
		Occs o = new Occs();
		o.setI(occurences);
		o.setCorrelationId(word.getJobId());
		return o;
	}

	@Override
	public boolean acceptInput(Word input) {
		//System.out.println("received: " + input.getW());
		//System.out.println("favoriteWord: "+favouriteWord+" (currentOccurences:"+occurences+")");
		//logic for when to accept tasks for this instance of OccurencesMonitor goes here.
		return input.getW().equals(favouriteWord);
	}

	public int getOccurences() {
		return occurences;
	}

	public void setFavouriteWord(String word) {
		favouriteWord = word;
	}

}
