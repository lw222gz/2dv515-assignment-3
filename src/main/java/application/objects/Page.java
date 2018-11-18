package application.objects;

import java.util.ArrayList;
import java.util.List;

public class Page {
	private String page;
	private List<Integer> words;

	public Page(String page){
		this.page = page;
		words = new ArrayList<>();
	}

	/**
	 * List of words hash codes in order mentioned in the wiki page.
	 * @return
	 */
	public List<Integer> getWords() {
		return words;
	}

	public void addWord(String word){
		words.add(word.hashCode());
	}

	public String getPage() {
		return page;
	}
}
