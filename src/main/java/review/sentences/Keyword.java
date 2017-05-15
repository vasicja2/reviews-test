package review.sentences;

public class Keyword {
	private String val;
	private KeywordType type;
	
	public Keyword(KeywordType type, String val) {
		this.type = type;
		this.val = val;
	}
	
	public String getValue() {
		return val;
	}
	
	public KeywordType getType() {
		return type;
	}
}
