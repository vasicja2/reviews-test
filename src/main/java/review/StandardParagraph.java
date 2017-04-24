package review;

import java.util.Map;

import review.sentences.SentenceCluster;

public class StandardParagraph extends Paragraph {	
	public StandardParagraph(ParagraphType type) {
		super(type);
	}

	@Override
	public String getId() {
		return type.getId();
	}

	@Override
	public String getTitle() {
		return type.getTitle();
	}

	@Override
	public Map<String, EvaluationType> getOptions() {
		return type.getEvaluation();
	}

	@Override
	public void removeSentence(int clusterID, String type, int sentenceID) {
		SentenceCluster cluster = null;
		for (SentenceCluster c : sentences) {
			if (c.getClusterId() == clusterID) {
				cluster = c;
			}
		}
		
		int pos = getSentencePosition(cluster, type, sentenceID);
		
		if (type.endsWith("p")) {
			cluster.getOpeningSentences().remove(pos);
		} else {
			cluster.getOtherSentences().remove(pos);
		}
	}

	private int getSentencePosition(SentenceCluster cluster, String type, int sentenceID) {
		if (type.endsWith("p")) {
			for (int i=0; i<cluster.getOpeningSentences().size(); i++) {
				if (cluster.getOpeningSentences().get(i).getSentenceID() == sentenceID) {
					return i;
				}
			}
		} else {
			for (int i=0; i<cluster.getOtherSentences().size(); i++) {
				if (cluster.getOtherSentences().get(i).getSentenceID() == sentenceID) {
					return i;
				}
			}
		}
		
		
		return -1;
	}

}
