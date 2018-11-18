package application.objects;

public class PageScore implements Comparable<PageScore> {
	private Page page;
	private int wordFrequencyScore;
	private int documentLocationScore;

	public PageScore(Page page){
		this.page = page;
	}

	public int getDocumentLocationScore() {
		return documentLocationScore;
	}

	public int getWordFrequencyScore() {
		return wordFrequencyScore;
	}

	public void setWordFrequencyScore(int wordFrequencyScore) {
		this.wordFrequencyScore = wordFrequencyScore;
	}

	public void setDocumentLocationScore(int documentLocationScore) {
		this.documentLocationScore = documentLocationScore;
	}

	@Override
	public int compareTo(PageScore o) {
		//TODO
		return 0;
	}
}
