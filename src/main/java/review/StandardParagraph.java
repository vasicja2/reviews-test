package review;

import java.util.Map;

import review.sentences.SentenceCluster;

public class StandardParagraph extends Paragraph {	
	public StandardParagraph(ParagraphType type) {
		super(type);
		this.id = type.getId();
		this.title = type.getTitle();
	}

	@Override
	public Map<String, EvaluationType> getOptions() {
		return type.getEvaluation();
	}

	@Override
	public void removeSentence(int clusterID, int sentenceID) {
		SentenceCluster cluster = null;
		for (SentenceCluster c : sentences) {
			if (c.getClusterId() == clusterID) {
				cluster = c;
			}
		}
		
		int pos = getSentencePosition(cluster, sentenceID);
		
		cluster.getSentences().remove(pos);
	}

	private int getSentencePosition(SentenceCluster cluster, int sentenceID) {
		for (int i=0; i<cluster.getSentences().size(); i++) {
			if (cluster.getSentences().get(i).getSentenceID() == sentenceID) {
				return i;
			}
		}
				
		return -1;
	}

}
