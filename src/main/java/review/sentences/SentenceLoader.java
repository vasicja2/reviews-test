package review.sentences;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import review.ParagraphType;
import review.ReviewType;

/**
 * 
 * @author jakub
 *
 */
public class SentenceLoader {
	private char revType;
	
	public SentenceLoader(ReviewType revType) {
		this.revType = revType.getName().charAt(0);
	}
	
	/**
	 * Reads all the sentences in the database fit for the specified paragraph and review types.
	 * @param parType - type of the paragraph
	 * @return An ArrayList of the Sentence objects
	 */
	public ArrayList<Sentence> loadSentences(ParagraphType parType) {
		ArrayList<Sentence> result = new ArrayList<>();
		String filename = "src/main/resources/sentenceDB/" + parType.getId() + ".log";
		
		try (BufferedReader breader = new BufferedReader(new FileReader(filename))) {
			int attrCount = parType.getEvaluation().size();
			String line = breader.readLine();			/*Skip the head of the log file*/
			
			while (true) {
				line = breader.readLine();
				
				if (line == null || line.isEmpty()) break;
				if (!fitsType(line)) continue;
				
				result.add(processLine(line, attrCount));
			} 
		} catch (IOException e) {
			System.err.println("Problem reading the file " + filename + "!");
			e.printStackTrace();
		} 
		
		result.sort(new Comparator<Sentence>() {
			public int compare(Sentence s1, Sentence s2) {
				if (s1.getPosition() < s2.getPosition()) return -1;
			    if (s1.getPosition() > s2.getPosition()) return 1;
			    
				return 0;
			}
		});
		return result;
	}
	
	/**
	 * Turns the sentence line in the database into the Sentence object.
	 * @param line
	 * @param attrCount
	 * @return
	 */
	private Sentence processLine(String line, int attrCount) {
		int i = 0;
		float [] attributes = new float[attrCount];
		int position = 1;
		String value = "";
		
		for (String token : line.split("~")) {
			if (i == 0) {
				i++;
				continue;
			} else if (i == 1) {
				position = Integer.valueOf(token);
				i++;
				continue;
			} else if (i == attrCount+2) {
				value = token;
				i++;
			} else {
				attributes[i-2] = Float.parseFloat(token);
				i++;
			}
		}
		
		return new Sentence(position, attributes, value);	
	}
	
	/**
	 * Checks if the sentence fits the type of the review.
	 * @param line
	 * @return
	 */
	private boolean fitsType(String line) {
		if (line.charAt(0) == 'X') return true;
		
		for(int i=0; line.charAt(i) != '~'; i++) {
			if (line.charAt(i) == revType) return true;
		}
		
		return false;
	}

}
