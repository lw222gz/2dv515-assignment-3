package application.dataset.handler;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;

import application.objects.Page;
import org.springframework.stereotype.Component;

@Component
public class DatasetHandler {

	private static final String DATASET_WORDS_DIR = "Dataset/Words";
	private static final String DATASET_LINKS_DIR = "Dataset/Links";
	private Map<String, Page> titleToPage;

	private static final int PAGE_RANK_ITERATIONS = 20;
	private static final double PAGE_RANK_BASE_VALUE = 0.15;
	private static final double PAGE_RANK_WEIGHT = 0.85;

	public Collection<Page> getPages() {
		return titleToPage.values();
	}

	@PostConstruct
	private void setup() throws RuntimeException{

		titleToPage = new HashMap<>();
		String line;

		try{
			File wordsCategoryDir = new File(getClass().getClassLoader().getResource(DATASET_WORDS_DIR).getPath());
			for (String wordCategory : requireNonNull(wordsCategoryDir.list())){
				for(File f : requireNonNull(new File(wordsCategoryDir.getAbsolutePath() + "/" + wordCategory).listFiles())){

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

			File linksCategoryDir = new File(getClass().getClassLoader().getResource(DATASET_LINKS_DIR).getPath());
			for (String linkCategory : requireNonNull(linksCategoryDir.list())){
				for(File f : requireNonNull(new File(linksCategoryDir.getAbsolutePath() + "/" + linkCategory).listFiles())){

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
		double max = getPages().stream().map(Page::getPageRank).max(Double::compareTo).orElseThrow(() -> new RuntimeException("No pages read from the dataset."));

		getPages().forEach(p -> p.setPageRank(p.getPageRank() / max));
	}

	//Replace temp 8-bit ASCII code 057 with '/'
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

		return pr * PAGE_RANK_WEIGHT + PAGE_RANK_BASE_VALUE;
	}

}
