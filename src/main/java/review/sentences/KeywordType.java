package review.sentences;

public enum KeywordType {
	PROCESS("1Proc", "What did the author have to do?"),
	INPUT("2Inp", "What problem is the author dealing with?"),
	TECHNOLOGY("3Tech", "What kinds of technologies does the author use?"),
	OUTPUT("4Out", "What is the result of the author's work?");
	
	private final String id;
	private final String question;
	
	private KeywordType(String id, String question) {
		this.id = id;
		this.question = question;
	}
	
	public String getId() {
		return id;
	}
	
	public String getQuestion() {
		return question;
	}
}
