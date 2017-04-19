package review;

import java.util.ArrayList;
import java.util.Map;

import review.sentences.SentenceCluster;

/**
 * 
 * @author jakub
 *
 */
public abstract class Paragraph implements Comparable<Paragraph> {
	protected int order;
	protected String text;
	protected final ParagraphType type;
	protected double [] evaluation;
	protected ArrayList<SentenceCluster> sentences;
	
	public Paragraph(ParagraphType type) {
		this.type = type;
		this.text = "";
		this.evaluation = new double[this.type.getEvaluation().size()];
		this.sentences = new ArrayList<>();
	}
	
	abstract public String getId();	
	abstract public String getTitle();	
	abstract public Map<String, EvaluationType> getOptions();
	abstract public void removeSentence(int cluster, String type, int pos);
	
	public void addSentence(SentenceCluster cluster) {
		this.sentences.add(cluster);
	}
	
	public ArrayList<SentenceCluster> getRecommended() {
		return sentences;
	}
		
	public void setOrder(int order) {
		this.order = order;
	}
	
	public int getOrder() {
		return order;
	}
	
	public ParagraphType getType() {
		return type;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public double[] getEvaluation() {
		return evaluation;
	}
	
	public void setEvaluation(double[] evaluation) {
		this.evaluation = evaluation;
	}
	
	@Override
	public int compareTo(Paragraph o) {
		return this.order - o.order;
	}
}
