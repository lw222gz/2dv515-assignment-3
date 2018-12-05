package application.objects;

public class PageDto implements Comparable<PageDto> {
	private final Double wordFrequencyScore;
	private final Double documentLocationScore;
	private final String page;

	public PageDto(String page, Double wordFrequencyScore, Double documentLocationScore){
		this.page = page;
		this.wordFrequencyScore = wordFrequencyScore;
		this.documentLocationScore = documentLocationScore;
	}

	public String getPage() {
		return page;
	}

	public Double getTotalScore() {
		return wordFrequencyScore + documentLocationScore;
	}

	public Double getDocumentLocationScore() {
		return documentLocationScore;
	}

	public Double getWordFrequencyScore() {
		return wordFrequencyScore;
	}

	@Override
	public int compareTo(PageDto other) {
		//switch compare as double comparison prioritizes lower values
		return other.getTotalScore().compareTo(this.getTotalScore());
	}
}
