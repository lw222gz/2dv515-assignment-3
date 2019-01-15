package application.dataset.handler;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;

import application.objects.Page;
import org.springframework.stereotype.Component;

@Component
public class DatasetHandler {

	private List<Page> pages;
	private Map<String, Page> titleToPage;

	private static final int PAGE_RANK_ITERATIONS = 20;

	public Collection<Page> getPages() {
		return titleToPage.values();
	}

	@PostConstruct
	private void setup() throws RuntimeException{

		titleToPage = new HashMap<>();
		String line;

		try{
			System.out.println(getClass().getClassLoader().getResource("Dataset/Words").getPath());

			File wordsCategoryDir = new File(getClass().getClassLoader().getResource("Dataset/Words").getPath());
			for (String wordCategory : wordsCategoryDir.list()){
				for(File f : new File(wordsCategoryDir.getAbsolutePath() + "/" + wordCategory).listFiles()){

					String pageTitle = getPageTitle(f.getName());
					BufferedReader br = new BufferedReader(new FileReader(f));

					Page p = new Page(pageTitle);
					while (nonNull(line = br.readLine())) {
						p.addWords(asList(line.split("\\s+")).stream().map(String::hashCode).collect(toList()));
					}

					titleToPage.put(pageTitle, p);
					br.close();
				}
			}

			File linksCategoryDir = new File(getClass().getClassLoader().getResource("Dataset/Links").getPath());
			for (String linkCategory : linksCategoryDir.list()){
				for(File f : new File(linksCategoryDir.getAbsolutePath() + "/" + linkCategory).listFiles()){

					String pageTitle = getPageTitle(f.getName());
					BufferedReader br = new BufferedReader(new FileReader(f));

					Page p = titleToPage.get(pageTitle);
					if(nonNull(p)){
						while (nonNull(line = br.readLine())){
							p.addOutgoingLink(line);
						}
					}
					br.close();
				}
			}

			calculatePageRanks();
			normalizePageRank();

		} catch (IOException ex){
			throw new RuntimeException("Something went wrong when trying to parse dataset.");
		}
	}

	private void normalizePageRank() {
		double max = getPages().stream().map(Page::getPageRank).max(Double::compareTo).get();

		getPages().stream().forEach(p -> p.setPageRank(p.getPageRank() / max));
	}

	private String getPageTitle(String fileName){
		return fileName.replace("057", "/");
	}

	private void calculatePageRanks() {
		Map<Page, Double> pageToRank = new HashMap<>();
		for(int i = 0; i < PAGE_RANK_ITERATIONS; i++){
			System.out.println("Page rank iteration: " + (i + 1));
			getPages().forEach(p -> pageToRank.put(p, calculatePageRank(p)));
			pageToRank.forEach(Page::setPageRank);
		}
	}

	private double calculatePageRank(Page page){
		double pr = getPages()
				.stream()
				.filter(p -> p.hasLinkTo(page))
				.mapToDouble(p -> p.getPageRank() / p.amountOfOutgoingLinks())
				.sum();

		return pr * 0.85 + 0.15;
	}

}
