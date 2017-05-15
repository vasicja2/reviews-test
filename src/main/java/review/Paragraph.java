package review;

import java.util.ArrayList;
import java.util.Map;

import review.sentences.SentenceCluster;

/**
 * This class represents a single paragraph of the review. It's a polymorph type with two subclasses: 
 * StandardParagraph, which represents one of the paragraphs given by the enum ParagraphType, and
 * Custom Paragraph, so that you can add your own paragraph to the review easily.
 * Paragraphs are compared by their order, don't forget to set it properly!
 * @author jakub
 *
 */
public abstract class Paragraph implements Comparable<Paragraph> {
	protected int order;
	protected String id;
	protected String title;
	protected String text;
	protected final ParagraphType type;
	protected double [] evaluation;
	protected ArrayList<SentenceCluster> sentences;
	
	public Paragraph(ParagraphType type) {
		this.order = 0;
		this.type = type;
		this.text = "";
		this.evaluation = new double[this.type.getEvaluation().size()];
		this.sentences = new ArrayList<>();
		
		for (int i=0; i<this.evaluation.length; i++) {
			this.evaluation[i] = 100;
		}
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	abstract public Map<String, EvaluationType> getOptions();
	abstract public void removeSentence(int clusterID, int sentenceID);
	
	public void addSentenceCluster(SentenceCluster cluster) {
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
