package review.sentences;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import review.Paragraph;
import review.ParagraphType;

/**
 * 
 * @author jakub
 *
 */
public class SentenceRecommender {
	private static final int TO_RECOMMEND = 15;
	private static final int MAX_DISTANCE = 35;
	private static final SentenceRecommender instance = new SentenceRecommender();
	
	/**
	 * Counts sentences in the text of a paragraph.
	 * @param text
	 * @return
	 */
	private int countSentences(String text) {
		int result = 0;
		
		if (text.isEmpty()) return 0;
		
		for(int i=0; i<text.length(); i++) {
			if(text.charAt(i) == '.' && i < text.length()-2) {
				if (text.charAt(i+1) == ' ' && (text.charAt(i+2) >= 'A' && text.charAt(i+2) <= 'Z')) {
					result++;
				}
			}
		}
		result++;
		
		return result;
	}
	
	private double countDistance(SentenceCluster cluster, double [] current, int position) {
		double result = Double.MAX_VALUE;
		
		for (Sentence s : cluster.getSentences()) {
			double [] sentenceAttr = s.getAttributes();
			double tmp = 0;
			
			for (int i=0; i<current.length; i++) {
				if (sentenceAttr[i] == -1) continue;
				tmp += Math.pow(sentenceAttr[i] - current[i], 2);
			}
			tmp = Math.sqrt(tmp);
			s.setDistance(tmp);
			
			result = Math.min(tmp, result);
			if (result == 0) break;
		}
		cluster.sortSentences();
		
		return result;
	}
	
	/**
	 * 
	 * @param par
	 * @param attributes
	 * @return
	 */
	public List<SentenceCluster> recommend(Paragraph par, double [] attributes) {
		ParagraphType parType = par.getType();
		List<SentenceCluster> result = new ArrayList<>();
		
		if (attributes.length != parType.getEvaluation().size()) {
			System.err.println("Invalid number of attributes!");
			return null;
		}
		
		ArrayList<SentenceCluster> clusters = SentenceDBHandler.getInstance().loadSentences(parType);
		int currentPos = countSentences(par.getText());
		
		for (SentenceCluster cl : clusters) {
			double distance = countDistance(cl, attributes, currentPos);
			cl.setCurrentDistance(distance);
			if (distance <= MAX_DISTANCE) 
				result.add(cl);
		}
		
		result.sort(new Comparator<SentenceCluster>() {
			public int compare(SentenceCluster c1, SentenceCluster c2) {
				if (c1.getCurrentDistance() < c2.getCurrentDistance()) return -1;
			    if (c1.getCurrentDistance() > c2.getCurrentDistance()) return 1;
			    if (c1.getCurrentDistance() == c2.getCurrentDistance()) {
			    	return ThreadLocalRandom.current().nextInt(-1, 1);
			    }
			    
				return 0;
			}
		});
		
		int toReturn = Math.min(result.size(), TO_RECOMMEND);
		return result.subList(0, toReturn);
	}

	public static SentenceRecommender getInstance() {
		return instance;
	}
}
