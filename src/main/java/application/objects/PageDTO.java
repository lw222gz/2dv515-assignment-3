package application.objects;

public class PageDTO implements Comparable<PageDTO> {
	private final double wordFrequencyScore;
	private final double documentLocationScore;
	private final double wordDistanceScore;
	private final double pageRankScore;
	private final String page;
	private final double totalScore;

	public PageDTO(Page page, Double wordFrequencyScore, Double documentLocationScore, Double wordDistanceScore){
		this.page = page.getPageTitle();
		this.wordFrequencyScore = wordFrequencyScore;
		this.documentLocationScore = documentLocationScore;
		this.wordDistanceScore = wordDistanceScore;
		pageRankScore = page.getPageRank();

		totalScore = this.wordFrequencyScore + this.documentLocationScore + this.wordDistanceScore + pageRankScore;
	}

	public String getPage() {
		return page;
	}

	public Double getTotalScore() {
		return totalScore;
	}

	public Double getDocumentLocationScore() {
		return documentLocationScore;
	}

	public Double getWordDistanceScore() {
		return wordDistanceScore;
	}

	public Double getWordFrequencyScore() {
		return wordFrequencyScore;
	}

	public Double getPageRankScore() {
		return pageRankScore;
	}

	@Override
	public int compareTo(PageDTO other) {
		//switch compare as double comparison prioritizes lower values
		return other.getTotalScore().compareTo(this.getTotalScore());
	}
}
