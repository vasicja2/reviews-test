package logic.sentences;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import logic.ParagraphType;

/**
 * 
 * @author jakub
 *
 */
public class SentenceRecommender {
	private Map<ParagraphType, ArrayList<Sentence>> avaliableSentences;
	private static final int TO_RECOMMEND = 7;
	
	public SentenceRecommender() {	
		avaliableSentences = new HashMap<>();
	}
	
	/**
	 * Adds sentences to the list.
	 * @param sentences
	 */
	public void addAvaliableSentences(ParagraphType type, ArrayList<Sentence> sentences) {
		this.avaliableSentences.put(type, sentences);
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<Sentence> recommend(ParagraphType parType, float [] attributes) {
		ArrayList<Sentence> result = new ArrayList<>();
		ArrayList<Sentence> allAvailable = avaliableSentences.get(parType);
		int [] maxCounts = parType.getSectionLengths();
		
		if (attributes.length != parType.getEvaluation().size()) {
			System.out.println("Invalid number of attributes!");
			return result;
		}
		
		/*Split into sections (the list of all sentences should be sorted by section here) and process each section.*/
		int lower = 0;
		int upper = 1;
		while(allAvailable.size() > upper) {
			while ((allAvailable.size() > upper) && 
					(allAvailable.get(upper).getPosition() == allAvailable.get(upper-1).getPosition())) {
				upper++;
			}
			result.addAll(recommendInSection(result.size(), maxCounts[allAvailable.get(upper-1).getPosition()], 
											attributes, allAvailable.subList(lower, upper)));
			lower = upper;
			upper++;
		}
		
		for (Sentence s : result) {
			s.recommend();
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param recommendedSoFar
	 * @param maxInSection
	 * @param attributes
	 * @param sentences
	 * @return
	 */
	private List<Sentence> recommendInSection(int recommendedSoFar, int maxInSection,
													float [] attributes, List<Sentence> sentences) {
		/*How many sentences do I want*/
		int toReturn = Math.min(maxInSection, TO_RECOMMEND-recommendedSoFar);
		if (toReturn == 0) {
			return sentences.subList(0, 0);
		}
				
		/*Count distances*/
		for (Sentence s : sentences) {
			if (s.isRecommended()) 
				s.setDistance(Float.MAX_VALUE);
			else 
				s.setDistance(countDistance(s.getAttributes(), attributes));
		}
		
		/*Sort according to distance. Those with the same distance will be sorted randomly.*/
		sentences.sort(new Comparator<Sentence>() {
			public int compare(Sentence s1, Sentence s2) {
				if (s1.getDistance() < s2.getDistance()) return -1;
			    if (s1.getDistance() > s2.getDistance()) return 1;
			    if (s1.getDistance() == s2.getDistance()) {
			    	return ThreadLocalRandom.current().nextInt(-1, 1);
			    }
			    
				return 0;
			}
		});
		
		toReturn = Math.min(toReturn, sentences.size());
		return sentences.subList(0, toReturn);
	}

	private float countDistance(float[] sentenceAttributes, float[] currentAttributes) {
		float result = 0;
		for (int i=0; i<sentenceAttributes.length; i++) {
			if (sentenceAttributes[i] != -1)
				result += Math.abs(sentenceAttributes[i] - currentAttributes[i]);
		}
		result /= sentenceAttributes.length;
		
		return result;
	}

}
