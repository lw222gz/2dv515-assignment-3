package application.search.service;

import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import application.dataset.handler.DatasetHandler;
import application.objects.Page;
import application.objects.PageScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

	@Autowired
	private DatasetHandler datasetHandler;

	public List<Page> search(List<String> queryWords){

		List<PageScore> pageScores = new ArrayList<>();
		List<Page> allPages = datasetHandler.getPages();
		Set<Integer> query = queryWords.stream().map(String::hashCode).collect(toSet());

		for(Page p : allPages){
			PageScore ps = new PageScore(p);

			ps.setDocumentLocationScore(documentLocation(query, p));
			ps.setWordFrequencyScore(wordFrequency(query, p));
		}

		//TODO: normalize set scores

		return null;
	}

	private int wordFrequency(Set<Integer> query, Page p){
		return (int)p.getWords().stream().filter(query::contains).count();
	}

	private int documentLocation(Set<Integer> query, Page p){
		List<Integer> words = p.getWords();
		int score = 0;

		for(int i = 0; i < words.size(); i++){
			if(query.contains(words.get(i))){
				score += i;
			}
		}

		return score != 0 ? score : Integer.MAX_VALUE;
	}

	private long normalize(int score, boolean smallerIsBetter){
		if(smallerIsBetter){

		} else {

		}
		return 0;
	}
}
