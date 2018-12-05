package application.search.service;

import static java.lang.Double.max;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import application.dataset.handler.DatasetHandler;
import application.objects.Page;
import application.objects.PageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

	private static final double DOCUMENT_LOCATION_WEIGHT = 0.8;
	private static final double MIN_DIVISION = 0.000001;

	@Autowired
	private DatasetHandler datasetHandler;

	public List<PageDto> search(List<String> queryWords){

		List<Page> allPages = datasetHandler.getPages();
		Set<Integer> query = queryWords.stream().map(String::hashCode).collect(toSet());

		Map<Page, Double> pageToWordFrequencyScore = new HashMap<>();
		Map<Page, Double> pageToDocumentLocationScore = new HashMap<>();

		for(Page p : allPages){
			pageToWordFrequencyScore.put(p, wordFrequencyScore(query, p));
			pageToDocumentLocationScore.put(p, documentLocationScore(query, p));
		}

		normalizeScores(pageToWordFrequencyScore, false);
		normalizeScores(pageToDocumentLocationScore, true);

		return allPages.stream()
				.map(page -> new PageDto(page.getPage(),
							pageToWordFrequencyScore.get(page), DOCUMENT_LOCATION_WEIGHT * pageToDocumentLocationScore.get(page)))
				.sorted()
				.collect(toList());
	}

	private double wordFrequencyScore(Set<Integer> query, Page p){
		return p.getWords().stream().filter(query::contains).count();
	}

	private double documentLocationScore(Set<Integer> query, Page p){
		return query.stream().mapToInt(word -> calculateDocumentLocationScore(word, p.getWords())).sum();
	}

	private int calculateDocumentLocationScore(Integer word, List<Integer> words){
		for(int i = 0; i < words.size(); i++){
			if(words.get(i).equals(word)){
				return i + 1;
			}
		}
		return 1000000;
	}

	private void normalizeScores(Map<Page, Double> scoreMap, boolean smallerIsBetter){
		if(smallerIsBetter){
			double min = scoreMap.values().stream().min(Double::compareTo).get();

			scoreMap.entrySet().forEach(entry -> entry.setValue(min / max(entry.getValue(), MIN_DIVISION)));
		} else {
			double max = scoreMap.values().stream().max(Double::compareTo).get();

			scoreMap.entrySet().forEach(entry -> entry.setValue(entry.getValue() / max));
		}
	}
}
