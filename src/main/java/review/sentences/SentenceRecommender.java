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
	//private static final int TO_RECOMMEND = 15;
	private static final int MAX_DISTANCE = 25;
	private static final SentenceRecommender instance = new SentenceRecommender();	
	private SentenceRecommender(){}
	
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

	public static SentenceRecommender getInstance() {
		return instance;
	}

	/**
	 * Returns a list of clusters of recommended sentences to the given paragraph based on evaluation given as a paremeter. 
	 * Sentences are loaded from the database.
	 * @param par
	 * @param paragraphs
	 * @param attributesCurrent
	 * @return
	 */
	public List<SentenceCluster> recommend(Paragraph par, ArrayList<Paragraph> paragraphs, double[] attributesCurrent) {
		/*Check if the number of attributes is correct*/
		if (attributesCurrent.length != par.getType().getEvaluation().size()) {
			System.err.println("Invalid number of attributes!");
			return null;
		}
		
		/*Create an empty result*/
		List<SentenceCluster> result = new ArrayList<>();		
		
		/*Load sentences from the database*/
		ArrayList<SentenceCluster> clusters = SentenceDBHandler.getInstance().loadSentences(par.getType());
			
		for (SentenceCluster cl : clusters) {
			/*Select a representing sentence or skip an empty cluster (only for initialization)*/
			Sentence s;
			if (!cl.getSentences().isEmpty()) {
				s = cl.getSentences().get(0);
			} else {
				continue;
			}
			
			/*Initialize additional attributes, distance and validity*/
			String [] additional = s.getAdditionalAttributes();
			double [] additionalCurrent = new double[additional.length];
			double distance = 0;
			boolean valid = true;
			
			/*Check if the additional attributes are valid*/
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
					valid = false;
					break;
				}
			}
			
			/*Count the distance or set maximum if the attributes are not valid*/
			if (valid) {
				if (par.getType().equals(ParagraphType.OVERALL)) 
					distance = countDistanceOverall(cl, attributesCurrent, additionalCurrent);
				else
					distance = countDistance(cl, attributesCurrent, additionalCurrent);
			} else {
				distance = Double.MAX_VALUE;
			}
			
			/*Save the distance and the cluster for further recommendation*/
			cl.setCurrentDistance(distance);
			par.addSentenceCluster(cl);
			
			/*If the distance is small enough, add the cluster to the result list*/
			if (distance <= MAX_DISTANCE) 
				result.add(cl);
		}
		
		/*Sort the result*/
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
		
		return result;
	}

	/**
	 * Counts the distance of the cluster from the current position by single-link method 
	 * Average distance is used to reduce influence of dimensionality. The metric is adjusted to fit the last paragraph.
	 * @param cluster
	 * @param attributesCurrent
	 * @param additionalCurrent
	 * @return
	 */
	private double countDistanceOverall(SentenceCluster cluster, double[] attributesCurrent, double[] additionalCurrent) {
		double result = Double.MAX_VALUE;
		
		for (Sentence s : cluster.getSentences()) {
			String [] additionalAttr = s.getAdditionalAttributes();
			double tmp = 0, additional;
			double sentenceAttr = s.getAttributes()[0];
			
			if (sentenceAttr != -1) {
				if (sentenceAttr < 0 && attributesCurrent[0] > Math.abs(sentenceAttr)) {
					continue;
				} else if (sentenceAttr > 0 && attributesCurrent[0] < sentenceAttr) {
					continue;
				}
			}
			
			for (int i=0; i<additionalCurrent.length; i++) {
				additional = Double.parseDouble(additionalAttr[i].split("\\.")[2]);
				tmp += Math.pow(additionalCurrent[i] - additional, 2);
			}
			
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

	/**
	 * Returns a list of clusters of recommended sentences to the given paragraph based on evaluation given as a paremeter. 
	 * Sentences are not loaded from the database, they are also requested as a parameter.
	 * @param par
	 * @param paragraphs
	 * @param attributes
	 * @param clusters
	 * @return
	 */
	public List<SentenceCluster> recommend(Paragraph par, ArrayList<Paragraph> paragraphs, double[] attributes,	ArrayList<SentenceCluster> clusters) {
		/*Create an empty result*/
		List<SentenceCluster> result = new ArrayList<>();
		
		/*Check if the number of attributes is correct*/
		if (attributes.length != par.getType().getEvaluation().size()) {
			System.err.println("Invalid number of attributes!");
			return null;
		}
			
		for (SentenceCluster cl : clusters) {
			/*Select a representing sentence or skip an empty cluster (only for initialization)*/
			Sentence s;
			if (!cl.getSentences().isEmpty()) {
				s = cl.getSentences().get(0);
			} else {
				continue;
			}
			
			/*Initialize additional attributes, distance and validity*/
			String [] additional = s.getAdditionalAttributes();
			double [] additionalCurrent = new double[additional.length];
			double distance = 0;
			boolean valid = true;
			
			/*Check if the additional attributes are valid*/
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
					valid = false;
					break;
				}
			}
			
			/*Count the distance or set maximum if the attributes are not valid*/
			if (valid) {
				if (par.getType().equals(ParagraphType.OVERALL)) 
					distance = countDistanceOverall(cl, attributes, additionalCurrent);
				else
					distance = countDistance(cl, attributes, additionalCurrent);
			} else {
				distance = Double.MAX_VALUE;
			}
			
			/*Save the distance*/
			cl.setCurrentDistance(distance);
			
			/*If the distance is small enough, add the cluster to the result list*/
			if (distance <= MAX_DISTANCE) 
				result.add(cl);
		}
		
		/*Sort the result*/
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
				
		return result;
	}
}
