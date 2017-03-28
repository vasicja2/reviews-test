package review;

import java.util.Map;

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

}
