package review.sentences;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SentenceCluster {
	private int clusterID;
	private double currentDistance;
	private ArrayList<Sentence> sentences;
	
	public SentenceCluster(Sentence sentence) {
		this.sentences = new ArrayList<>();		
		this.currentDistance = 0;
		this.sentences.add(sentence);
	}
		
	public void sortSentences() {
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
	}
		
	public ArrayList<Sentence> getSentences() {		
		return sentences;
	}
		
	public void addSentence(Sentence sentence) {
		this.sentences.add(sentence);
	}
	
	public void addSentences(List<Sentence> sentences) {
		this.sentences.addAll(sentences);	
	}
	
	public void merge(SentenceCluster cluster) {
		this.sentences.addAll(cluster.getSentences());
	}
	
	public double getCurrentDistance() {
		return currentDistance;
	}
	
	public void setCurrentDistance(double currentDistance) {
		this.currentDistance = currentDistance;
	}
	
	public void setClusterId(int clusterID) {
		this.clusterID = clusterID;
	}
	
	public int getClusterId() {
		return clusterID;
	}
}
