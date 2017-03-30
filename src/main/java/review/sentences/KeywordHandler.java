package review.sentences;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class KeywordHandler {
	private static final KeywordHandler instance = new KeywordHandler();
	private Map<KeywordType, ArrayList<Keyword>> keywordDB;
	
	private KeywordHandler() {
		this.keywordDB = new TreeMap<>();
	}
	
	private void loadKeywords() {
		String filename = "src/main/resources/sentenceDB/keywords.log";
		keywordDB.clear();
		for (KeywordType t : KeywordType.values()) {
			keywordDB.put(t, new ArrayList<>());
		}
		
		try (BufferedReader breader = new BufferedReader(new FileReader(filename))) {
			String category = "", variation = "", value = "";
			boolean reachedEnd = true;
			Keyword newKw = new Keyword(value);			//this is to avoid the error "may have not been initialized"
			
			String line = breader.readLine();			//skip the head of the file
			line = breader.readLine();
			if (line != null && !line.isEmpty()) {
				category = line.split("~")[0];
				variation = line.split("~")[1].substring(3);
				value = line.split("~")[2];	
				
				newKw = new Keyword(value);
				
				reachedEnd = false;
			}			
			
			while (!reachedEnd) {
				String currentCategory = category;
				while (!variation.startsWith("Def")) {
					newKw.addVariation(variation, value);
					
					line = breader.readLine();
					if (line == null || line.isEmpty()) {
						reachedEnd = true;
						break;
					}

					category = line.split("~")[0];
					variation = line.split("~")[1];
					value = line.split("~")[2];	
				}				
				
				if (!reachedEnd) {
					KeywordType type = null;
					for (KeywordType t : KeywordType.values()) {
						if (t.getId().equals(currentCategory))
							type = t;
					}
					keywordDB.get(type).add(newKw);
					
					variation = variation.substring(3);
					newKw = new Keyword(value);
				}
			}
			
		} catch (IOException e) {
			System.err.println("Problem reading the file " + filename + "!");
			e.printStackTrace();
		}
	}
	
	public static KeywordHandler getInstance() {
		return instance;
	}
	
	public Map<KeywordType, ArrayList<Keyword>> getKeywordDB() {
		loadKeywords();
		return keywordDB;
	}
}
