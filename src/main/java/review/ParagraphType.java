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
			EvaluationType.YES_NO, "1. Is the topic current or original?", 1,
			EvaluationType.ZERO_TO_HUNDRED, "2. How challenging the topic is?", 1),
	
	/**
	 * 
	 */
	RESEARCH("02research", "Research and the written thesis",
			EvaluationType.FOUR_MARKS, "1. The length of the work meets all requirements:", 1,
			EvaluationType.ZERO_TO_HUNDRED, "2. Does the author have a good overview of the topic?", 1,
			EvaluationType.FOUR_MARKS, "3. The work is well structured and readable:", 1),
	
	
	/**
	 * 
	 */
	FORMAL("03formal", "Bibliography and formal level",
			EvaluationType.FOUR_MARKS, "1. The author does not use any unclear formulations:", 1,
			EvaluationType.ZERO_TO_HUNDRED, "2. Does the language meets the expected formal level?", 1,
			EvaluationType.FOUR_MARKS, "3. The work does not contain any typos and typographic viloations:", 1,
			EvaluationType.ZERO_TO_HUNDRED, "4. Does the author properly cite all used literature?", 1),
	
	/**
	 * 
	 */
	FULFILMENT("04fulfilment", "Fulfilment of the assignment and aplicability of the results",
			EvaluationType.ZERO_TO_HUNDRED, "1. Did the author reach their goal?", 1,
			EvaluationType.FOUR_MARKS, "2. The results are valid and well-founded:", 1,
			EvaluationType.YES_NO, "3. Will the results be used in the future?", 1),

	/**
	 * 
	 */
	ACTIVITY("05activity", "Activity and self-reliance of the student",
			EvaluationType.FOUR_MARKS, "1. The author's own contribution is sufficent:", 1),
	
	/**
	 * 
	 */
	OVERALL("06overall", "Overall evaluation",
			EvaluationType.YES_NO, "1. Does the work meet the criteria and is it acceptable?", 1),
	
	/**
	 * 
	 */
	CUSTOM();
	
	private final String id;
	private final String title;
	private final Map<String, EvaluationType> evaluation;
	private final int [] weights;
	
	private ParagraphType() {
		this.id = "";
		this.title = "";
		this.evaluation = new TreeMap<String, EvaluationType>();
		this.weights = null;
	}
	
	private ParagraphType(String id, String title,
			EvaluationType eval1, String question1, int w1) {
		this.id = id;
		this.title = title;
		this.weights = new int [1];
		
		this.evaluation = new TreeMap<String, EvaluationType>();
		evaluation.put(question1, eval1);
		this.weights[0] = w1;
	}
	
	private ParagraphType(String id, String title,
			EvaluationType eval1, String question1, int w1,
			EvaluationType eval2, String question2, int w2) {
		this.id = id;
		this.title = title;
		this.weights = new int [2];
		
		this.evaluation = new TreeMap<String, EvaluationType>();
		evaluation.put(question1, eval1);
		evaluation.put(question2, eval2);
		this.weights[0] = w1;
		this.weights[1] = w2;
	}
	
	private ParagraphType(String id, String title,
			EvaluationType eval1, String question1, int w1,
			EvaluationType eval2, String question2, int w2,
			EvaluationType eval3, String question3, int w3) {
		this.id = id;
		this.title = title;
		this.weights = new int[3];
		
		this.evaluation = new TreeMap<String, EvaluationType>();
		evaluation.put(question1, eval1);
		evaluation.put(question2, eval2);
		evaluation.put(question3, eval3);
		weights[0] = w1;
		weights[1] = w2;
		weights[2] = w3;
	}
	
	private ParagraphType(String id, String title,
			EvaluationType eval1, String question1, int w1,
			EvaluationType eval2, String question2, int w2,
			EvaluationType eval3, String question3, int w3,
			EvaluationType eval4, String question4, int w4) {
		this.id = id;
		this.title = title;
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

}
