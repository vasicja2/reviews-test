package review;

import java.util.Map;
import java.util.TreeMap;
  
/**
 * 
 * @author jakub
 *
 */
public enum ParagraphType {
	
	/**
	 * 
	 */
	TOPIC("01topic", "Originality and difficulty of the topic",
			"Specify what the thesis is about, what challenges does the topic pose and how original it is.",
			EvaluationType.FOUR_MARKS, "1. The topic of this thesis is current or original:", 1,
			EvaluationType.ZERO_TO_HUNDRED, "2. How challenging the topic is?", 1),
	
	/**
	 * 
	 */
	RESEARCH("02research", "Factual level of the thesis and bibliography",
			"This concerns the student's theoretical knowledge and their work with sources of information.",
			EvaluationType.FOUR_MARKS, "1. The length of the work meets all requirements:", 1,
			EvaluationType.FOUR_MARKS, "2. All parts of the thesis contain only necessary information:", 1,
			EvaluationType.ZERO_TO_HUNDRED, "3. Does the author have a good overview of the topic?", 1,
			EvaluationType.ZERO_TO_HUNDRED, "4. Does the author use relevant sources of information?", 1,
			EvaluationType.ZERO_TO_HUNDRED, "5. Does the author properly cite all used literature?", 1),
	
	
	/**
	 * 
	 */
	FORMAL("03formal", "Formal level of the written thesis",
			"Focuses on the formal aspects: structuring, consistency, language and typography.",
			EvaluationType.FOUR_MARKS, "1. The work is well structured and readable:", 1,
			EvaluationType.FOUR_MARKS, "2. The author does not use any unclear formulations:", 1,
			EvaluationType.ZERO_TO_HUNDRED, "3. Does the language meet the expected formal level?", 1,
			EvaluationType.FOUR_MARKS, "4. The work does not contain any typos and typographic viloations:", 1),
	
	/**
	 * 
	 */
	FULFILMENT("04fulfilment", "Fulfilment of the assignment and aplicability of the results",
			"Focuses on the final outcome of the thesis, its quality and usability.",
			EvaluationType.ZERO_TO_HUNDRED, "1. Did the author reach their goal?", 1,
			EvaluationType.FOUR_MARKS, "2. The results are valid and well-founded:", 1,
			EvaluationType.FOUR_MARKS, "3. The results are ready to be used in practice or published:", 1),

	/**
	 * 
	 */
	ACTIVITY("05activity", "Activity and self-reliance of the student",
			"Concerns the student's dedication, ability to communicate, punctuality in meeting deadlines, etc.",
			EvaluationType.ZERO_TO_HUNDRED, "1. How active was the student?", 1,
			EvaluationType.ZERO_TO_HUNDRED, "2. Did the student show interest in the topic?", 1,
			EvaluationType.FOUR_MARKS, "3. The student communicated well all the time:", 1,
			EvaluationType.FOUR_MARKS, "4. The student always met deadlines:", 1),
	
	/**
	 * 
	 */
	QUESTIONS("06questions", "Questions for the defence",
			"Do you want to ask the student any questions? (There will be nothing to suggest, I am leaving this completely up to you.)"),
	
	/**
	 * 
	 */
	OVERALL("07overall", "Overall evaluation",
			"Sums up all of the above.",
			EvaluationType.ZERO_TO_HUNDRED, "1. Please specify the final grade:", 1),
	
	/**
	 * 
	 */
	CUSTOM();
	
	private final String id;
	private final String title;
	private final String description;
	private final Map<String, EvaluationType> evaluation;
	private final int [] weights;
	
	private ParagraphType() {
		this.id = "";
		this.title = "";
		this.description = "";
		this.evaluation = new TreeMap<String, EvaluationType>();
		this.weights = null;
	}
	
	private ParagraphType(String id, String title, String description) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.evaluation = new TreeMap<String, EvaluationType>();
		this.weights = null;
	}
	
	private ParagraphType(String id, String title, String description,
			EvaluationType eval1, String question1, int w1) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.weights = new int [1];
		
		this.evaluation = new TreeMap<String, EvaluationType>();
		evaluation.put(question1, eval1);
		this.weights[0] = w1;
	}
	
	private ParagraphType(String id, String title, String description,
			EvaluationType eval1, String question1, int w1,
			EvaluationType eval2, String question2, int w2) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.weights = new int [2];
		
		this.evaluation = new TreeMap<String, EvaluationType>();
		evaluation.put(question1, eval1);
		evaluation.put(question2, eval2);
		this.weights[0] = w1;
		this.weights[1] = w2;
	}
	
	private ParagraphType(String id, String title, String description,
			EvaluationType eval1, String question1, int w1,
			EvaluationType eval2, String question2, int w2,
			EvaluationType eval3, String question3, int w3) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.weights = new int[3];
		
		this.evaluation = new TreeMap<String, EvaluationType>();
		evaluation.put(question1, eval1);
		evaluation.put(question2, eval2);
		evaluation.put(question3, eval3);
		weights[0] = w1;
		weights[1] = w2;
		weights[2] = w3;
	}
	
	private ParagraphType(String id, String title, String description,
			EvaluationType eval1, String question1, int w1,
			EvaluationType eval2, String question2, int w2,
			EvaluationType eval3, String question3, int w3,
			EvaluationType eval4, String question4, int w4) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.weights = new int[4];
		
		this.evaluation = new TreeMap<String, EvaluationType>();
		evaluation.put(question1, eval1);
		evaluation.put(question2, eval2);
		evaluation.put(question3, eval3);
		evaluation.put(question4, eval4);
		weights[0] = w1;
		weights[1] = w2;
		weights[2] = w3;
		weights[3] = w4;
	}
	
	private ParagraphType(String id, String title, String description,
			EvaluationType eval1, String question1, int w1,
			EvaluationType eval2, String question2, int w2,
			EvaluationType eval3, String question3, int w3,
			EvaluationType eval4, String question4, int w4,
			EvaluationType eval5, String question5, int w5) {
		this.id = id;
		this.title = title;
		this.description = description;
		this.weights = new int[5];
		
		this.evaluation = new TreeMap<String, EvaluationType>();
		evaluation.put(question1, eval1);
		evaluation.put(question2, eval2);
		evaluation.put(question3, eval3);
		evaluation.put(question4, eval4);
		evaluation.put(question5, eval5);
		weights[0] = w1;
		weights[1] = w2;
		weights[2] = w3;
		weights[3] = w4;
		weights[4] = w5;
	}
	
	public String getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public Map<String, EvaluationType> getEvaluation() {
		return evaluation;
	}
	
	public int[] getWeights() {
		return weights;
	}
	
	public String getDescription() {
		return description;
	}

}
