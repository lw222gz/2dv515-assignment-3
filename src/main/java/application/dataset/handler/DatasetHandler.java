package application.dataset.handler;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import application.objects.Page;
import org.springframework.stereotype.Component;

@Component
public class DatasetHandler {

	private List<Page> pages;

	public List<Page> getPages() {
		return pages;
	}

	@PostConstruct
	private void setup() throws RuntimeException{

		pages = new ArrayList<>();

		try{
			for(File f : getFilesFromFolder(getClass().getClassLoader().getResource("Words").getPath())) {

				BufferedReader br = new BufferedReader(new FileReader(f));
				Page p = new Page(f.getName());
				String line;

				while (nonNull(line = br.readLine())) {
					asList(line.split("\\s+")).forEach(p::addWord);
				}
				pages.add(p);
			}

			System.out.println(pages.size());

		} catch (IOException ex){
			throw new RuntimeException("Something went wrong when trying to parse dataset.");
		}
	}

	private List<File> getFilesFromFolder(String resourceFolderPath){
		List<File> files = new ArrayList<>();

		File folder = new File(resourceFolderPath);
		for(File f : folder.listFiles()){
			if(f.isFile()){
				files.add(f);
			} else {
				files.addAll(getFilesFromFolder(f.getPath()));
			}
		}

		return files;
	}
}
