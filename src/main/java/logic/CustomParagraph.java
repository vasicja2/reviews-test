package logic;

import java.util.HashMap;
import java.util.Map;

public class CustomParagraph extends Paragraph {
	private String id;
	private String title;

	public CustomParagraph() {
		super(ParagraphType.CUSTOM);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public Map<String, EvaluationType> getOptions() {
		return new HashMap<>();
	}

}
