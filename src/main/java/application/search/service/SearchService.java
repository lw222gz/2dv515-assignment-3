package application.search.service;

import static java.lang.Double.max;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.dataset.handler.DatasetHandler;
import application.objects.Page;
import application.objects.PageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

	private static final double DOCUMENT_LOCATION_WEIGHT = 0.8;
	private static final double MIN_DIVISION = 0.000001;
	private static final double WORD_NOT_FOUND = 1000000;

	@Autowired
	private DatasetHandler datasetHandler;

	public List<PageDto> search(List<String> queryWords){

		Collection<Page> allPages = datasetHandler.getPages();
		List<Integer> query = queryWords.stream().map(String::hashCode).collect(toList());

		Map<Page, Double> pageToWordFrequencyScore = new HashMap<>();
		Map<Page, Double> pageToDocumentLocationScore = new HashMap<>();
		Map<Page, Double> pageToWordDistanceScore = new HashMap<>();

		for(Page p : allPages){
			pageToWordFrequencyScore.put(p, wordFrequencyScore(query, p));
			pageToDocumentLocationScore.put(p, documentLocationScore(query, p));
			pageToWordDistanceScore.put(p, wordDistanceScore(query, p));
		}

		normalizeScores(pageToWordFrequencyScore, false);
		normalizeScores(pageToDocumentLocationScore, true);
		normalizeScores(pageToWordDistanceScore, true);

		return allPages.stream()
				.map(page -> new PageDto(page,
						pageToWordFrequencyScore.get(page),
						DOCUMENT_LOCATION_WEIGHT * pageToDocumentLocationScore.get(page),
						pageToWordDistanceScore.get(page)))
				.sorted()
				.collect(toList());
	}

	private double wordFrequencyScore(List<Integer> query, Page p){
		return p.getWords().stream().filter(query::contains).count();
	}

	private double documentLocationScore(List<Integer> query, Page p){
		return query.stream().mapToDouble(word -> calculateDocumentLocationScore(word, p.getWords())).sum();
	}

	private double wordDistanceScore(List<Integer> query, Page p){
		double sum = 0;
		for(int i = 0; i < query.size(); i++){
			for(int k = i + 1; k < query.size(); k++){
				Double scoreA = documentLocationScore(singletonList(query.get(i)), p);
				Double scoreB = documentLocationScore(singletonList(query.get(k)), p);
				if(scoreA.equals(WORD_NOT_FOUND) || scoreB.equals(WORD_NOT_FOUND)){
					sum += WORD_NOT_FOUND;
				} else {
					double val = scoreA - scoreB;
					if(val < 0){
						val = val * -1.0;
					}
					sum += val;
				}

			}
		}

		return sum;
	}

	private double calculateDocumentLocationScore(Integer word, List<Integer> words){
		for(int i = 0; i < words.size(); i++){
			if(words.get(i).equals(word)){
				return i + 1;
			}
		}
		return WORD_NOT_FOUND;
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
