package logic;

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
			4, "1 2 5 5",
			EvaluationType.YES_NO, "1. Is the topic current or original?", 
			EvaluationType.FOUR_MARKS, "2. The topic is very challenging:"),
	
	/**
	 * 
	 */
	RESEARCH("02research", "Research and theoretical framework",
			1, "1",
			EvaluationType.YES_NO, "1. Does the author discuss sufficent number of possible ways to approach the problem?",
			EvaluationType.YES_NO, "2. Does the author have a good overview of the topic?"),
	
	/**
	 * 
	 */
	CONTRIBUTION("03contribution", "Author's own contribution to the work",
			1, "1",
			EvaluationType.FOUR_MARKS, "1. The author's own contribution is sufficent:",
			EvaluationType.YES_NO, "2. Is it part of a team project?",
			EvaluationType.YES_NO, "3. Is it clear which parts are the author's own work?"),
	
	/**
	 * 
	 */
	FORMAL("04formal", "Bibliography and formal level",
			1, "1",
			EvaluationType.YES_NO, "1. Does the author use unclear formulations?",
			EvaluationType.FOUR_MARKS, "2. The works meets the formal level:"),
	
	/**
	 * 
	 */
	FULFILMENT("05fulfilment", "Fulfilment of the assignment and aplicability of the results",
			1, "1",
			EvaluationType.YES_NO, "1. Did the author reach their goal?",
			EvaluationType.FOUR_MARKS, "2. The results are well valid and well-founded:"/*,
			* 
			* Don't know what to do with optional questions yet...
			* 
			* EvaluationType.YES_NO, "Does the work bring an innovation for the author's field of science?"*/),
	
	/**
	 * 
	 */
	OVERALL("06overall", "Overall evaluation",
			1, "1",
			EvaluationType.YES_NO, "1. Does the work meet the criteria and is it acceptable?"),
	
	/**
	 * 
	 */
	CUSTOM();
	
	private final String id;
	private final String title;
	private final Map<String, EvaluationType> evaluation;
	private final int[] sectionLengths;
	
	private ParagraphType() {
		this.id = "";
		this.title = "";
		this.evaluation = new TreeMap<String, EvaluationType>();
		this.sectionLengths = null;
	}
	
	private ParagraphType(String id, String title, int secCount, String secLengths,
			EvaluationType eval1, String question1) {
		this.id = id;
		this.title = title;
		
		this.evaluation = new TreeMap<String, EvaluationType>();
		evaluation.put(question1, eval1);
		
		/*This is to avoid having too many constructors*/
		this.sectionLengths = new int [secCount];
		int i=0;
		for(String l : secLengths.split(" ")) {
			this.sectionLengths[i] = Integer.parseInt(l);
			i++;
		}
	}
	
	private ParagraphType(String id, String title, int secCount, String secLengths, 
			EvaluationType eval1, String question1, 
			EvaluationType eval2, String question2) {
		this.id = id;
		this.title = title;
		
		this.evaluation = new TreeMap<String, EvaluationType>();
		evaluation.put(question1, eval1);
		evaluation.put(question2, eval2);		

		/*This is to avoid having too many constructors*/
		this.sectionLengths = new int [secCount];
		int i=0;
		for(String l : secLengths.split(" ")) {
			this.sectionLengths[i] = Integer.parseInt(l);
			i++;
		}
	}
	
	private ParagraphType(String id, String title, int secCount, String secLengths, 
			EvaluationType eval1, String question1, 
			EvaluationType eval2, String question2, 
			EvaluationType eval3, String question3) {
		this.id = id;
		this.title = title;
		
		this.evaluation = new TreeMap<String, EvaluationType>();
		evaluation.put(question1, eval1);
		evaluation.put(question2, eval2);
		evaluation.put(question3, eval3);		

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
	
	public int[] getSectionLengths() {
		return sectionLengths;
	}

}
