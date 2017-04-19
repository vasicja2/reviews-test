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
	private static final int MAX_DISTANCE = 15;
	private static final SentenceRecommender instance = new SentenceRecommender();	
	private SentenceRecommender(){}
	
	/**
	 * Counts sentences in the text of a paragraph.
	 * @param text
	 * @return
	 */
	/*private int countSentences(String text) {
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
	}*/
	
	/**
	 * Counts the distance of the cluster from the current position by single-link method 
	 * Average distance is used to reduce influence of dimensionality.
	 * @param cluster
	 * @param current
	 * @return
	 */
	private double countDistance(SentenceCluster cluster, double[] current, double[] additionalCurrent) {
		double result = Double.MAX_VALUE;
		
		for (Sentence s : cluster.getSentences()) {
			String [] additionalAttr = s.getAdditionalAttributes();
			double tmp = 0, additional;
			double [] sentenceAttr = s.getAttributes();
			int usedAttr = additionalCurrent.length;
			
			for (int i=0; i<additionalCurrent.length; i++) {
				additional = Double.parseDouble(additionalAttr[i].split("\\.")[2]);
				tmp += Math.pow(additionalCurrent[i] - additional, 2);
			}
			
			for (int i=0; i<current.length; i++) {
				if(sentenceAttr[i] != -1) {
					tmp += Math.pow(current[i] - sentenceAttr[i], 2);
					usedAttr++;
				}
			}
			if (usedAttr != 0) {
				tmp /= usedAttr;
				tmp = Math.sqrt(tmp);
			}
			
			s.setDistance(tmp);
			
			result = Math.min(result, tmp);
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
	/*public List<SentenceCluster> recommend(Paragraph par, double [] attributes) {
		ParagraphType parType = par.getType();
		List<SentenceCluster> result = new ArrayList<>();
		
		if (attributes.length != parType.getEvaluation().size()) {
			System.err.println("Invalid number of attributes!");
			return null;
		}
		
		ArrayList<SentenceCluster> clusters = SentenceDBHandler.getInstance().loadSentences(parType);
		//int currentPos = countSentences(par.getText());
		
		for (SentenceCluster cl : clusters) {
			double distance = countDistance(cl, attributes, currentPos);
			cl.setCurrentDistance(distance);
			par.addRecommended(cl);
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
	}*/
	
	/**
	 * 
	 * @param par
	 * @param attributes
	 * @param clusters
	 * @return
	 */
	/*public List<SentenceCluster> recommend(Paragraph par, double [] attributes, List<SentenceCluster> clusters) {
		ParagraphType parType = par.getType();
		List<SentenceCluster> result = new ArrayList<>();
		
		if (attributes.length != parType.getEvaluation().size()) {
			System.err.println("Invalid number of attributes!");
			return null;
		}
		
		//int currentPos = countSentences(par.getText());
		
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
		
	}*/

	public static SentenceRecommender getInstance() {
		return instance;
	}

	public List<SentenceCluster> recommend(Paragraph par, ArrayList<Paragraph> paragraphs, double[] attributes) {
		List<SentenceCluster> result = new ArrayList<>();
		
		if (attributes.length != par.getType().getEvaluation().size()) {
			System.err.println("Invalid number of attributes!");
			return null;
		}
		
		ArrayList<SentenceCluster> clusters = SentenceDBHandler.getInstance().loadSentences(par.getType());
			
		for (SentenceCluster cl : clusters) {
			Sentence s;
			if (!cl.getOpeningSentences().isEmpty()) {
				s = cl.getOpeningSentences().get(0);
			} else if (!cl.getOtherSentences().isEmpty()) {
				s = cl.getOtherSentences().get(0);
			} else {
				continue;
			}
			
			String [] additional = s.getAdditionalAttributes();
			double [] additionalCurrent = new double[additional.length];
			double distance = 0;
			
			for(int i=0; i<additional.length; i++) {
				String parID = additional[i].split("\\.")[0];
				int evalOrder = Integer.parseInt(additional[i].split("\\.")[1])-1;
				additionalCurrent[i] = -1;
				
				for(Paragraph p : paragraphs) {
					if (p.getId().equals(parID)) {
						additionalCurrent[i] = p.getEvaluation()[evalOrder];
						break;
					}
				}
				if (additionalCurrent[i] == -1) {
					distance = -1;
					break;
				}
			}
			
			if (distance != -1) {
				if(par.getType().equals(ParagraphType.OVERALL)) 
					distance = countDistanceOverall(cl, attributes, additionalCurrent);
				else
					distance = countDistance(cl, attributes, additionalCurrent);
			} else {
				distance = Double.MAX_VALUE;
			}
			
			cl.setCurrentDistance(distance);
			par.addSentence(cl);
			
			if (distance <= MAX_DISTANCE) 
				result.add(cl);
		}
		
		result.sort(new Comparator<SentenceCluster>() {
			public int compare(SentenceCluster c1, SentenceCluster c2) {
				if (c1.getCurrentDistance() < c2.getCurrentDistance()) return -1;
			    if (c1.getCurrentDistance() > c2.getCurrentDistance()) return 1;
			    if (c1.getCurrentDistance() == c2.getCurrentDistance()) {
			    	return ThreadLocalRandom.current().nextInt(-1, 2);
			    }
			    
				return 0;
			}
		});
		
		int toReturn = Math.min(result.size(), TO_RECOMMEND);
		return result.subList(0, toReturn);
	}

	private double countDistanceOverall(SentenceCluster cluster, double[] current, double[] additionalCurrent) {
		double result = Double.MAX_VALUE;
		
		for (Sentence s : cluster.getSentences()) {
			String [] additionalAttr = s.getAdditionalAttributes();
			double tmp = 0, additional;
			double sentenceAttr = s.getAttributes()[0];
			
			if (sentenceAttr != -1) {
				if (sentenceAttr < 0 && current[0] > Math.abs(sentenceAttr)) {
					continue;
				} else if (sentenceAttr > 0 && current[0] < sentenceAttr) {
					continue;
				}
			}
			
			for (int i=0; i<additionalCurrent.length; i++) {
				additional = Double.parseDouble(additionalAttr[i].split("\\.")[2]);
				tmp += Math.pow(additionalCurrent[i] - additional, 2);
			}
			
			//tmp += Math.pow(current[0] - s.getAttributes()[0], 2);
			if (additionalAttr.length != 0) {
				tmp /= additionalAttr.length;
				tmp = Math.sqrt(tmp);
			}
			
			s.setDistance(tmp);
			
			result = Math.min(result, tmp);
		}
		cluster.sortSentences();
		
		return result;
	}

	public List<SentenceCluster> recommend(Paragraph par, ArrayList<Paragraph> paragraphs, double[] attributes,	ArrayList<SentenceCluster> clusters) {
		List<SentenceCluster> result = new ArrayList<>();
		
		if (attributes.length != par.getType().getEvaluation().size()) {
			System.err.println("Invalid number of attributes!");
			return null;
		}
		
		//ArrayList<SentenceCluster> clusters = SentenceDBHandler.getInstance().loadSentences(ParagraphType.OVERALL);
		
		for (SentenceCluster cl : clusters) {
			Sentence s;
			if (!cl.getOpeningSentences().isEmpty()) {
				s = cl.getOpeningSentences().get(0);
			} else if (!cl.getOtherSentences().isEmpty()) {
				s = cl.getOtherSentences().get(0);
			} else {
				continue;
			}
			
			String [] additional = s.getAdditionalAttributes();
			double [] additionalCurrent = new double[additional.length];
			double distance = 0;
			
			for(int i=0; i<additional.length; i++) {
				String parID = additional[i].split("\\.")[0];
				int evalOrder = Integer.parseInt(additional[i].split("\\.")[1])-1;
				additionalCurrent[i] = -1;
				
				for(Paragraph p : paragraphs) {
					if (p.getId().equals(parID)) {
						additionalCurrent[i] = p.getEvaluation()[evalOrder];
						break;
					}
				}
				if (additionalCurrent[i] == -1) {
					distance = -1;
					break;
				}
			}
			
			if (distance != -1) {
				if(par.getType().equals(ParagraphType.OVERALL)) 
					distance = countDistanceOverall(cl, attributes, additionalCurrent);
				else
					distance = countDistance(cl, attributes, additionalCurrent);
			} else {
				distance = Double.MAX_VALUE;
			}
			
			cl.setCurrentDistance(distance);
			//par.addRecommended(cl);
			
			if (distance <= MAX_DISTANCE) 
				result.add(cl);
		}
		
		result.sort(new Comparator<SentenceCluster>() {
			public int compare(SentenceCluster c1, SentenceCluster c2) {
				if (c1.getCurrentDistance() < c2.getCurrentDistance()) return -1;
			    if (c1.getCurrentDistance() > c2.getCurrentDistance()) return 1;
			    if (c1.getCurrentDistance() == c2.getCurrentDistance()) {
			    	return ThreadLocalRandom.current().nextInt(-1, 2);
			    }
			    
				return 0;
			}
		});
		
		int toReturn = Math.min(result.size(), TO_RECOMMEND);
		return result.subList(0, toReturn);
	}
}
