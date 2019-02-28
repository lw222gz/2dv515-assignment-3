package application.search.service;

import static application.search.service.SearchService.WORD_NOT_FOUND;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.dataset.handler.DatasetHandler;
import application.objects.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class SearchServiceTest {

	@InjectMocks
	SearchService service;

	@Mock
	DatasetHandler datasetHandler;

	Page page;

	static final List<String> DEFAULT_PAGE_CONTENT = asList("word", "bord", "hord", "lorde","ford",
			"cord","mord","dord","gord","tord",
			"pord","qord","eord","aord","sord",
			"nord","vord","xord","jord","yord");

	@BeforeEach
	void setup(){
		service = new SearchService();
		initMocks(this);

		page = new Page("titleA");
		setPageContent(DEFAULT_PAGE_CONTENT);
	}

	@Test
	void wordFrequencyScore_noMatches(){
		List<String> stringQuery = asList("super", "mario");

		double result = service.wordFrequencyScore(toQuery(stringQuery), page);
		assertEquals(0.0, result);
	}

	@Test
	void wordFrequencyScore_hasMatches(){
		List<String> stringQuery = asList("super", "mario");
		List<String> pageContent = new ArrayList<>(DEFAULT_PAGE_CONTENT);
		pageContent.set(4, stringQuery.get(0));
		pageContent.set(7, stringQuery.get(0));
		pageContent.set(9, stringQuery.get(1));

		setPageContent(pageContent);

		double result = service.wordFrequencyScore(toQuery(stringQuery), page);
		assertEquals(3, result);
	}

	@Test
	void documentLocationScore_noMatches(){
		List<String> stringQuery = asList("super", "mario");

		double result = service.documentLocationScore(toQuery(stringQuery), page);
		assertEquals(WORD_NOT_FOUND * stringQuery.size(), result);
	}

	@Test
	void documentLocationScore_hasMatches(){
		List<String> stringQuery = asList("super", "mario");
		List<String> pageContent = new ArrayList<>(DEFAULT_PAGE_CONTENT);
		int firstWordIndex = 4;
		int secondWordIndex = 9;
		pageContent.set(firstWordIndex, stringQuery.get(0));
		pageContent.set(7, stringQuery.get(0));
		pageContent.set(secondWordIndex, stringQuery.get(1));
		pageContent.set(12, stringQuery.get(1));

		setPageContent(pageContent);

		double result = service.documentLocationScore(toQuery(stringQuery), page);

		assertEquals(firstWordIndex + 1 + secondWordIndex + 1, result);
	}

	@Test
	void wordDistanceScore_noMatches(){
		List<String> stringQuery = asList("super", "mario", "bros", "2");

		double result = service.wordDistanceScore(toQuery(stringQuery), page);
		//Should be once for each combination of word comparisons
		assertEquals(WORD_NOT_FOUND * 6, result);
	}

	@Test
	void wordDistanceScore_hasMatches(){
		List<String> stringQuery = asList("super", "mario", "smash");
		List<String> pageContent = new ArrayList<>(DEFAULT_PAGE_CONTENT);
		int firstWordIndex = 4;
		int secondWordIndex = 9;
		int thirdWordIndex = 12;
		pageContent.set(firstWordIndex, stringQuery.get(0));
		pageContent.set(secondWordIndex, stringQuery.get(1));
		pageContent.set(thirdWordIndex, stringQuery.get(2));

		setPageContent(pageContent);

		double result = service.wordDistanceScore(toQuery(stringQuery), page);
		double expected = Math.abs(firstWordIndex - secondWordIndex) + Math.abs(firstWordIndex - thirdWordIndex) + Math.abs(secondWordIndex - thirdWordIndex);
		assertEquals(expected, result);
	}

	@Test
	void normalizeScores_lowerIsBetter(){
		Map<Page, Double> pageToScoreMap = new HashMap<>();
		Page pageA = new Page("A");
		double pageAScore = 5.0;
		pageToScoreMap.put(pageA, pageAScore);

		Page pageB = new Page("B");
		double pageBScore = 11.0;
		pageToScoreMap.put(pageB, pageBScore);

		Page pageC = new Page("C");
		double pageCScore = 70.0;
		pageToScoreMap.put(pageC, pageCScore);

		service.normalizeScores(pageToScoreMap, true);
		assertEquals(1.0, (double)pageToScoreMap.get(pageA));
	}

	@Test
	void normalizeScores_higherIsBetter(){
		Map<Page, Double> pageToScoreMap = new HashMap<>();
		Page pageA = new Page("A");
		double pageAScore = 5.0;
		pageToScoreMap.put(pageA, pageAScore);

		Page pageB = new Page("B");
		double pageBScore = 11.0;
		pageToScoreMap.put(pageB, pageBScore);

		Page pageC = new Page("C");
		double pageCScore = 70.0;
		pageToScoreMap.put(pageC, pageCScore);

		service.normalizeScores(pageToScoreMap, false);
		assertEquals(1.0, (double)pageToScoreMap.get(pageC));
	}

	private List<Integer> toQuery(List<String> stringQuery) {
		return stringQuery.stream().map(String::hashCode).collect(toList());
	}

	private void setPageContent(List<String> pageContent){
		page = new Page(page.getPageTitle());
		page.addWords(pageContent.stream().map(String::hashCode).collect(toList()));
	}
}