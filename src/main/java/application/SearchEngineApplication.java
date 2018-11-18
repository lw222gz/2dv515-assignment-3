package application;

import application.dataset.handler.DatasetHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SearchEngineApplication {

	public static void main(String[] args){
		SpringApplication.run(SearchEngineApplication.class, args);
	}

	@Autowired
	private DatasetHandler datasetHandler;

	@GetMapping("/")
	public String hello(){
		return "ok";
	}
}
