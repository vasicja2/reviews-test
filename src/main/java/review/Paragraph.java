package review;

import java.util.Map;

/**
 * 
 * @author jakub
 *
 */
public abstract class Paragraph implements Comparable<Paragraph> {
	protected int order;
	protected String text;
	protected final ParagraphType type;
	protected float [] evaluation;
	protected int [] sectionsOccupied;
	
	public Paragraph(ParagraphType type) {
		this.type = type;
		this.text = "";
		this.evaluation = new float[this.type.getEvaluation().size()];
		this.sectionsOccupied = new int[this.type.getSectionLengths().length];
		for (int i=0; i<this.sectionsOccupied.length; i++) {
			this.sectionsOccupied[i] = 0;
		}
	}
	
	abstract public String getId();	
	abstract public String getTitle();	
	abstract public Map<String, EvaluationType> getOptions();
		
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
	
	public float[] getEvaluation() {
		return evaluation;
	}
	
	public void setEvaluation(float[] evaluation) {
		this.evaluation = evaluation;
	}
	
	public void addToSection(int section) {
		sectionsOccupied[section]++;
	}
	
	public int[] getSectionsOccupied() {
		return sectionsOccupied;
	}
	
	@Override
	public int compareTo(Paragraph o) {
		return this.order - o.order;
	}
}
