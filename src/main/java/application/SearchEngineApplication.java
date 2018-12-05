package application;

import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toList;

import java.util.List;

import application.dataset.handler.DatasetHandler;
import application.objects.PageDto;
import application.search.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SearchEngineApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(SearchEngineApplication.class);

	public static void main(String[] args){
		SpringApplication.run(SearchEngineApplication.class, args);
	}

	@Autowired
	private DatasetHandler datasetHandler;

	@Autowired
	private SearchService searchService;

	@GetMapping("/search")
	public List<PageDto> search(@RequestParam(value = "query") List<String> query){
		long time = currentTimeMillis();
		try{
			//Convert to lower case since dataset words are all lowercase.
			query = query.stream().map(String::toLowerCase).collect(toList());

			return searchService.search(query);
		} finally{
			LOGGER.info("Time to execute search request: " + (currentTimeMillis() - time) + "ms");
		}
	}
}
