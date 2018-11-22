package application.objects;

public class PageDto implements Comparable<PageDto> {
	private String page;
	private Double score;

	public PageDto(String page){
		this.page = page;
	}

	public String getPage() {
		return page;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	@Override
	public int compareTo(PageDto other) {
		//switch compare as double comparison prioritizes lower values
		return other.getScore().compareTo(this.getScore());
	}
}
