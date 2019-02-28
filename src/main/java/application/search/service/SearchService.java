package application.search.service;

import static java.lang.Double.max;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import application.dataset.handler.DatasetHandler;
import application.objects.Page;
import application.objects.PageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

	static final double DOCUMENT_LOCATION_WEIGHT = 0.8;
	static final double MIN_DIVISION = 0.000001;
	static final double WORD_NOT_FOUND = 1000000;

	@Autowired
	private DatasetHandler datasetHandler;

	public List<PageDTO> search(List<String> queryWords){

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
				.map(page -> new PageDTO(page,
						pageToWordFrequencyScore.get(page),
						pageToDocumentLocationScore.get(page) * DOCUMENT_LOCATION_WEIGHT,
						pageToWordDistanceScore.get(page)))
				.sorted()
				.collect(toList());
	}

	/**
	 * Gives a score depending on how often a word occurs
	 * Higher scores are better
	 * @param query
	 * @param p
	 * @return
	 */
	double wordFrequencyScore(List<Integer> query, Page p){
		return p.getWords().stream().filter(query::contains).count();
	}

	/**
	 * Adds a score depending on the index of a words first occurance
	 * Lower scores are better
	 * @param query
	 * @param p
	 * @return
	 */
	double documentLocationScore(List<Integer> query, Page p){
		return query.stream().mapToDouble(word -> calculateDocumentLocationScore(word, p.getWords())).sum();
	}

	private double calculateDocumentLocationScore(Integer word, List<Integer> words){
		for(int i = 0; i < words.size(); i++){
			if(words.get(i).equals(word)){
				return i + 1;
			}
		}
		return WORD_NOT_FOUND;
	}

	/**
	 * Adds a score that is decided by the aboslute value between the documentLocationScore of each word in the query
	 * Each word is compares towards all others once. Will give same score to all if query only has 1 word.
	 * Lower scores are better
	 * @param query
	 * @param p
	 * @return
	 */
	double wordDistanceScore(List<Integer> query, Page p){
		double sum = 0;
		for(int i = 0; i < query.size(); i++){
			for(int k = i + 1; k < query.size(); k++){
				Double scoreA = calculateDocumentLocationScore(query.get(i), p.getWords());
				Double scoreB = calculateDocumentLocationScore(query.get(k), p.getWords());

				if(scoreA.equals(WORD_NOT_FOUND) || scoreB.equals(WORD_NOT_FOUND)){
					sum += WORD_NOT_FOUND;
				} else {
					sum += Math.abs(scoreA - scoreB);
				}
			}
		}

		return sum;
	}

	void normalizeScores(Map<Page, Double> scoreMap, boolean smallerIsBetter){
		if(smallerIsBetter){
			double min = scoreMap.values().stream().min(Double::compareTo).orElseThrow(() -> new RuntimeException("No min value found in score map."));

			scoreMap.entrySet().forEach(entry -> entry.setValue(min / max(entry.getValue(), MIN_DIVISION)));
		} else {
			double max = scoreMap.values().stream().max(Double::compareTo).orElseThrow(() -> new RuntimeException("No max value found in score map."));

			scoreMap.entrySet().forEach(entry -> entry.setValue(entry.getValue() / max));
		}
	}
}
