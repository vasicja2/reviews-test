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
	public void removeSentence(int clusterID, String type, int pos) {
		SentenceCluster cluster = sentences.get(clusterID);
		
		if (type.endsWith("p")) {
			cluster.getOpeningSentences().remove(pos);
		} else {
			cluster.getOtherSentences().remove(pos);
		}
	}

}
