package application.objects;

public class PageDto implements Comparable<PageDto> {
	private final Double wordFrequencyScore;
	private final Double documentLocationScore;
	private final Double wordDistanceScore;
	private final Double pageRankScore;
	private final String page;
	private final Double totalScore;

	public PageDto(Page page, Double wordFrequencyScore, Double documentLocationScore, Double wordDistanceScore){
		this.page = page.getPageTitle();
		this.wordFrequencyScore = wordFrequencyScore;
		this.documentLocationScore = documentLocationScore;
		this.wordDistanceScore = wordDistanceScore;
		pageRankScore = page.getPageRank() * 0.5;

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
	public int compareTo(PageDto other) {
		//switch compare as double comparison prioritizes lower values
		return other.getTotalScore().compareTo(this.getTotalScore());
	}
}
