package review.sentences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import review.ParagraphType;

public class SentenceDBHandler {	
	private static final SentenceDBHandler instance = new SentenceDBHandler();
	private static final double MIN_CLUSTER_DISTANCE = 10;
	
	/**
	 * Reads all the sentences form the specified file.
	 * @param filename
	 * @param attrCount
	 * @return
	 */
	private synchronized ArrayList<Sentence> readFile(String filename, int attrCount) {
		ArrayList<Sentence> result = new ArrayList<>();
		
		try (BufferedReader breader = new BufferedReader(new FileReader(filename))) {
			String line = breader.readLine();			/*Skip the head of the log file*/
			
			while (true) {
				line = breader.readLine();
				
				if (line == null || line.isEmpty()) break;
				
				result.add(processLine(line, attrCount));
			} 
		} catch (IOException e) {
			System.err.println("Problem reading the file " + filename + "!");
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Returns the distance of two clusters (based on the distance of their centroids).
	 * @param cl1
	 * @param cl2
	 * @return
	 */
	private double countDistance(SentenceCluster cl1, SentenceCluster cl2) {
		double [] attr1 = cl1.getCentroidAttributes();
		double [] attr2 = cl2.getCentroidAttributes();
		
		/*if ((cl1.getCentroidPosition() == 1 && cl2.getCentroidPosition() != 1)
				|| (cl2.getCentroidPosition() == 1 && cl1.getCentroidPosition() != 1)) {
			return Double.MAX_VALUE;
		}*/
		
		for (int i=0; i<attr1.length; i++) {
			if ((attr1[i] == -1 && attr2[i] != -1) || (attr2[i] == -1 && attr1[i] != -1)) {
				return Double.MAX_VALUE;
			}
		}
		
		double result = 0;
		
		for (int i=0; i<attr1.length; i++) {
			result += Math.pow(attr1[i] - attr2[i], 2);
		}		
		
		return Math.sqrt(result);
	}
	
	/**
	 * Called when a new sentence was added to the database.
	 * This reviews the distribution of sentences between clusters with the agglomerative approach.
	 * @param clusters
	 * @return
	 */
	private ArrayList<SentenceCluster> recountClusters (ArrayList<SentenceCluster> clusters) {
		//double [][] distances = new double [clusters.size()][clusters.size()];
		double minDistance = 0; 
		double distance;
		int minFrom = 0, minTo = 0;
		
		while (true) {
			minDistance = Double.MAX_VALUE;
			
			for (int i=0; i<clusters.size(); i++) {				
				for (int j=i+1; j<clusters.size(); j++) {					
					//distances[i][j] = countDistance(clusters.get(i), clusters.get(j));
					//distances[j][i] = distances[i][j];
					distance = countDistance(clusters.get(i), clusters.get(j));
					if (distance < minDistance) {
						minDistance = distance;
						minFrom = i;
						minTo = j;
					}
				}
			}
			
			if (minDistance > MIN_CLUSTER_DISTANCE) break;
			
			clusters.get(minFrom).merge(clusters.get(minTo));
			clusters.remove(minTo);			
		}
		
		return clusters;
	}
	
	/**
	 * Called when the clusters were changed - saves the database with the corresponding distribution to new clusters
	 * @param parType
	 * @param clusters
	 */
	private synchronized void saveSentences(ParagraphType parType, ArrayList<SentenceCluster> clusters) {
		String filename = "src/main/resources/sentenceDB/" + parType.getId() + ".csv";	
		String text = "Cluster;Position;";
		for (int i=0; i<clusters.get(0).getCentroidAttributes().length; i++) {
			text += "Attr" + Integer.toString(i) + ";";
		}
		text += "Value\n";
		
		for (int i=0; i<clusters.size(); i++) {
			clusters.get(i).setClusterId(i);
			for (Sentence s  : clusters.get(i).getSentences()) {
				text += Integer.toString(i) + ";";
				text += Integer.toString(s.getPosition()) + ";";
				for (double attr : s.getAttributes()) {
					text += Double.toString(attr) + ";";
				}
				text += s.getValue() + "\n";
			}
		}
		//text += "\n";
		
		try {
			File f = new File(filename);
			FileOutputStream fostream = new FileOutputStream(f, false);
			fostream.write(text.getBytes());
			fostream.close();
		} catch (IOException e) {
			System.err.println("Problem writing the file " + filename + "!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads all the sentences in the database fit for the specified paragraph and review types and divides them into clusters.
	 * @param parType - type of the paragraph
	 * @return An ArrayList of all clusters of Sentences
	 */
	public synchronized ArrayList<SentenceCluster> loadSentences(ParagraphType parType) {
		String filename = "src/main/resources/sentenceDB/" + parType.getId() + ".csv";
		ArrayList<SentenceCluster> result = new ArrayList<>();

		/*get the sentences form the file*/
		ArrayList<Sentence> sentences = readFile(filename, parType.getEvaluation().size());
		
		/*split them into clusters*/
		boolean foundNew = false;
		
		sentences.sort(new Comparator<Sentence>() {
			public int compare(Sentence s1, Sentence s2) {
				if (s1.getClusterID() < s2.getClusterID()) return -1;
			    if (s1.getClusterID() > s2.getClusterID()) return 1;
			    
				return 0;
			}
		});
		
		int lower = 0;
		int upper = 1;
		while(sentences.size() > upper) {
			while ((sentences.size() > upper) && 
					(sentences.get(upper).getClusterID() == sentences.get(upper-1).getClusterID())) {
				upper++;
			}			
			
			if (sentences.get(upper-1).getClusterID() == -1) {		/*found some new sentences without a cluster assigned*/ 				
				for (Sentence s : sentences.subList(lower, upper)) {
					SentenceCluster cluster = new SentenceCluster(s);
					result.add(cluster);
				}
				foundNew = true;				
			} else {
				SentenceCluster cluster = new SentenceCluster(sentences.get(upper-1));
				cluster.addSentences(sentences.subList(lower, upper-1));
				cluster.setClusterId(sentences.get(upper-1).getClusterID());
				result.add(cluster);
			}
			
			lower = upper;
			upper++;
		}
		
		if (foundNew) {
			result = recountClusters(result);
			saveSentences(parType, result);
			return result;
		}
		
		return result;
	}
	
	/**
	 * Turns the sentence line in the database into the Sentence object.
	 * @param line
	 * @param attrCount
	 * @return
	 */
	private Sentence processLine(String line, int attrCount) {
		int i = 0, clusterID = -1;
		double [] attributes = new double[attrCount];
		int position = 1;
		String value = "";
		
		for (String token : line.split(";")) {
			if (i == 0) {
				clusterID = Integer.valueOf(token);
				i++;				
			} else if (i == 1) {
				position = Integer.valueOf(token);
				i++;
			} else if (i == attrCount+2) {
				value = token;
				i++;
			} else {
				attributes[i-2] = Double.parseDouble(token);
				i++;
			}
		}
		
		return new Sentence(clusterID, position, attributes, value);	
	}

	/**
	 * Appends new sentences to the file.
	 * @param parType
	 * @param input
	 */
	public synchronized void saveNewSentences(ParagraphType parType, String input) {
		String filename = "src/main/resources/sentenceDB/" + parType.getId() + ".csv";	
		if (input.isEmpty()) return;
		
		try {
			File f = new File(filename);
			FileOutputStream fostream = new FileOutputStream(f, true);
			fostream.write(input.getBytes());
			fostream.close();
		}catch (IOException e) {
			System.err.println("Problem writing the file " + filename + "!");
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads sentences without clustering.
	 * @param parType
	 * @return
	 */
	public ArrayList<Sentence> loadSentencesSimple(ParagraphType parType) {
		String filename = "src/main/resources/sentenceDB/" + parType.getId() + ".csv";
		
		/*ONLY FOR TESTING!*/
		resetClusters(parType);
		
		return readFile(filename, parType.getEvaluation().size());
	}
	
	/**
	 * For testing - resets assigned clusters
	 * @param parType
	 */
	private void resetClusters(ParagraphType parType) {
		String filename = "src/main/resources/sentenceDB/" + parType.getId() + ".csv";	
		String text = "Cluster;Position;";
		ArrayList<Sentence> sentences = readFile(filename, parType.getEvaluation().size());
		
		if (sentences.isEmpty()) return;
		
		for (int i=0; i<sentences.get(0).getAttributes().length; i++) {
			text += "Attr" + Integer.toString(i) + ";";
		}
		text += "Value\n";
		
		for (Sentence s  : sentences) {
			text += "-1;";
			text += Integer.toString(s.getPosition()) + ";";
			for (double attr : s.getAttributes()) {
				text += Double.toString(attr) + ";";
			}
			text += s.getValue() + "\n";
		}
		//text += "\n";
		
		try {
			File f = new File(filename);
			FileOutputStream fostream = new FileOutputStream(f, false);
			fostream.write(text.getBytes());
			fostream.close();
		} catch (IOException e) {
			System.err.println("Problem writing the file " + filename + "!");
			e.printStackTrace();
		}
	}
	
	public static SentenceDBHandler getInstance() {
		return instance;
	}
}
