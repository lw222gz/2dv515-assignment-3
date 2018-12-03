package application.objects;

public class PageDto implements Comparable<PageDto> {
	private final String page;
	private final Double score;

	public PageDto(String page, Double score){
		this.page = page;
		this.score = score;
	}

	public String getPage() {
		return page;
	}

	public Double getScore() {
		return score;
	}

	@Override
	public int compareTo(PageDto other) {
		//switch compare as double comparison prioritizes lower values
		return other.getScore().compareTo(this.getScore());
	}
}
