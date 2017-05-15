package review;

import java.util.HashMap;
import java.util.Map;

public class CustomParagraph extends Paragraph {
	public CustomParagraph(String id, String title) {
		super(ParagraphType.CUSTOM);
		this.title = title;
		this.id = id;
	}

	@Override
	public Map<String, EvaluationType> getOptions() {
		return new HashMap<>();
	}

	@Override
	public void removeSentence(int clusterID, int sentenceID) {
		//		Not implemented		
	}

}
