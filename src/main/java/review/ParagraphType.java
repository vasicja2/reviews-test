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
	TOPIC("01topic", "Introduction: Originality and difficulty of the topic",
			4, "1 2 2 5",
			EvaluationType.YES_NO, "1. Is the topic current or original?", 1,
			EvaluationType.ZERO_TO_HUNDRED, "2. How challenging the topic is?", 1),
	
	/**
	 * 
	 */
	RESEARCH("02research", "Research and theoretical framework",
			4, "1 1 4 1",
			EvaluationType.YES_NO, "1. Does the author discuss sufficent number of possible ways to approach the problem?", 4,
			EvaluationType.ZERO_TO_HUNDRED, "2. Does the author have a good overview of the topic?", 1,
			EvaluationType.FOUR_MARKS, "3. The work is well structured and readable:", 1),
	
	/**
	 * 
	 */
	CONTRIBUTION("03contribution", "Author's own contribution to the work",
			1, "1",
			EvaluationType.FOUR_MARKS, "1. The author's own contribution is sufficent:", 1,
			EvaluationType.YES_NO, "2. Is it part of a team project?", 1,
			EvaluationType.YES_NO, "3. Is it clear which parts are the author's own work?", 1),
	
	/**
	 * 
	 */
	FORMAL("04formal", "Bibliography and formal level",
			1, "1",
			EvaluationType.YES_NO, "1. Does the author use unclear formulations?", 1,
			EvaluationType.FOUR_MARKS, "2. The works meets the formal level:", 1),
	
	/**
	 * 
	 */
	FULFILMENT("05fulfilment", "Fulfilment of the assignment and aplicability of the results",
			1, "1",
			EvaluationType.YES_NO, "1. Did the author reach their goal?", 1,
			EvaluationType.FOUR_MARKS, "2. The results are well valid and well-founded:", 1/*,
			* 
			* Don't know what to do with optional questions yet...
			* 
			* EvaluationType.YES_NO, "Does the work bring an innovation for the author's field of science?"*/),
	
	/**
	 * 
	 */
	OVERALL("06overall", "Overall evaluation",
			1, "1",
			EvaluationType.YES_NO, "1. Does the work meet the criteria and is it acceptable?", 1),
	
	/**
	 * 
	 */
	CUSTOM();
	
	private final String id;
	private final String title;
	private final Map<String, EvaluationType> evaluation;
	private final int [] weights;
	private final int[] sectionLengths;
	
	private ParagraphType() {
		this.id = "";
		this.title = "";
		this.evaluation = new TreeMap<String, EvaluationType>();
		this.sectionLengths = null;
		this.weights = null;
	}
	
	private ParagraphType(String id, String title, int secCount, String secLengths,
			EvaluationType eval1, String question1, int w1) {
		this.id = id;
		this.title = title;
		this.weights = new int [1];
		
		this.evaluation = new TreeMap<String, EvaluationType>();
		evaluation.put(question1, eval1);
		this.weights[0] = w1;
		
		/*This is to avoid having too many constructors*/
		this.sectionLengths = new int [secCount];
		int i=0;
		for(String l : secLengths.split(" ")) {
			this.sectionLengths[i] = Integer.parseInt(l);
			i++;
		}
	}
	
	private ParagraphType(String id, String title, int secCount, String secLengths, 
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

		/*This is to avoid having too many constructors*/
		this.sectionLengths = new int [secCount];
		int i=0;
		for(String l : secLengths.split(" ")) {
			this.sectionLengths[i] = Integer.parseInt(l);
			i++;
		}
	}
	
	private ParagraphType(String id, String title, int secCount, String secLengths, 
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

		/*This is to avoid having too many constructors*/
		this.sectionLengths = new int [secCount];
		int i=0;
		for(String l : secLengths.split(" ")) {
			this.sectionLengths[i] = Integer.parseInt(l);
			i++;
		}
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
	
	public int[] getSectionLengths() {
		return sectionLengths;
	}

}
