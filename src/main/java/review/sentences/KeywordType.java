package review.sentences;

public enum KeywordType {
	PROCESS("1Proc", "What did the author have to do? (e.g. analyse, design, implement, research)"),
	INPUT("2Inp", "What problem is the author dealing with? (e.g. nuclear physics, voice recognition, predicate logic)"),
	TECHNOLOGY("3Tech", "What kinds of technologies does the author use? (e.g. jQuery, Java Spark, MySQL)"),
	OUTPUT("4Out", "What is the result of the author's work? (e.g. application, algorithm, design)");
	
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
