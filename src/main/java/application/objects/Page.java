package application.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Page {
	private String pageTitle;
	private String pageLink;
	private List<Integer> words;
	private Set<String> outgoingLinks;
	private double pageRank = 1.0;

	public Page(String pageTitle){
		this.pageTitle = pageTitle;
		pageLink = "/wiki/" + pageTitle;
		words = new ArrayList<>();
		outgoingLinks = new HashSet<>();
	}

	/**
	 * List of words hash codes in order mentioned in the wiki pageTitle.
	 * @return
	 */
	public List<Integer> getWords() {
		return words;
	}

	public String getPageLink() {
		return pageLink;
	}

	public double getPageRank() {
		return pageRank;
	}

	public void setPageRank(double pageRank) {
		this.pageRank = pageRank;
	}

	public String getPageTitle() {
		return pageTitle;
	}

	public void addWords(List<Integer> words) {
		this.words.addAll(words);
	}

	public boolean hasLinkTo(Page p){
		return outgoingLinks.contains(p.getPageLink());
	}

	public void addOutgoingLink(String link) {
		outgoingLinks.add(link);
	}

	public int amountOfOutgoingLinks(){
		return outgoingLinks.size();
	}
}
