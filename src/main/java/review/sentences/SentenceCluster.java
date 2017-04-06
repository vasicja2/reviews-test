package review.sentences;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SentenceCluster {
	private int clusterId;
	private double centroidPosition;
	private double[] centroidAttributes;
	private double currentDistance;
	private ArrayList<Sentence> openingSentences, otherSentences;
	
	public SentenceCluster(Sentence sentence) {
		this.centroidPosition = sentence.getPosition();
		this.centroidAttributes = new double[sentence.getAttributes().length];
		this.openingSentences = new ArrayList<>();
		this.otherSentences = new ArrayList<>();
		
		for (int i=0; i<this.centroidAttributes.length; i++) {
			this.centroidAttributes[i] = sentence.getAttributes()[i];
		}
		
		this.currentDistance = 0;
		if (sentence.getPosition() == 1) 
			this.openingSentences.add(sentence);
		else
			this.otherSentences.add(sentence);
	}
	
	private void countCentroid() {
		centroidPosition = 0;
		ArrayList<Sentence> allSentences = getSentences();
		
		for (Sentence s : allSentences) {
			centroidPosition += s.getPosition();
		}
		
		for (int i=0; i<centroidAttributes.length; i++) {
			centroidAttributes[i] = 0;
			for (Sentence s : allSentences) {
				centroidAttributes[i] += s.getAttributes()[i];
			}
		}
		
		centroidPosition /= allSentences.size();
		for (int i=0; i<centroidAttributes.length; i++) {
			centroidAttributes[i] /= allSentences.size();
		}
	}
	
	public void sortSentences() {
		openingSentences.sort(new Comparator<Sentence>() {
			public int compare(Sentence s1, Sentence s2) {
				if (s1.getDistance() < s2.getDistance()) return -1;
			    if (s1.getDistance() > s2.getDistance()) return 1;
			    if (s1.getDistance() == s2.getDistance()) {
			    	return ThreadLocalRandom.current().nextInt(-1, 1);
			    }
			    
				return 0;
			}
		});
		
		otherSentences.sort(new Comparator<Sentence>() {
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
	
	public double getCentroidPosition() {
		return centroidPosition;
	}
	
	public double[] getCentroidAttributes() {
		return centroidAttributes;
	}
	
	public ArrayList<Sentence> getSentences() {
		ArrayList<Sentence> result = new ArrayList<>(openingSentences);
		result.addAll(otherSentences);
		
		return result;
	}
	
	public ArrayList<Sentence> getOpeningSentences() {
		return openingSentences;
	}
	
	public ArrayList<Sentence> getOtherSentences() {
		return otherSentences;
	}
	
	public void addSentence(Sentence sentence) {
		if (sentence.getPosition() == 1) 
			this.openingSentences.add(sentence);
		else
			this.otherSentences.add(sentence);
		
		countCentroid();
	}
	
	public void addSentences(List<Sentence> sentences) {
		for (Sentence s : sentences) {
			if (s.getPosition() == 1)
				this.openingSentences.add(s);
			else
				this.otherSentences.add(s);
		}
		
		countCentroid();		
	}
	
	public void merge(SentenceCluster cluster) {
		this.openingSentences.addAll(cluster.getOpeningSentences());
		this.otherSentences.addAll(cluster.getOtherSentences());
		
		countCentroid();
	}
	
	public double getCurrentDistance() {
		return currentDistance;
	}
	
	public void setCurrentDistance(double currentDistance) {
		this.currentDistance = currentDistance;
	}
	
	public void setClusterId(int clusterID) {
		this.clusterId = clusterID;
	}
	
	public int getClusterId() {
		return clusterId;
	}
}
