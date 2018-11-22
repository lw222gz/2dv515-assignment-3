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
			pageToWordFrequencyScore.put(p, wordFrequency(query, p));
			pageToDocumentLocationScore.put(p, documentLocation(query, p));
		}

		normalizeScores(pageToWordFrequencyScore, false);
		normalizeScores(pageToDocumentLocationScore, true);

		return allPages.stream()
				.map(page -> {
					PageDto dto = new PageDto(page.getPage());
					dto.setScore(pageToWordFrequencyScore.get(page) + DOCUMENT_LOCATION_WEIGHT * pageToDocumentLocationScore.get(page));

					return dto;
				})
				.sorted()
				.collect(toList());
	}

	private double wordFrequency(Set<Integer> query, Page p){
		return p.getWords().stream().filter(query::contains).count();
	}

	private double documentLocation(Set<Integer> query, Page p){
		List<Integer> words = p.getWords();
		int score = 0;

		for(int i = 0; i < words.size(); i++){
			if(query.contains(words.get(i))){
				score += i;
			}
		}

		return score != 0 ? score : Double.MAX_VALUE;
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
