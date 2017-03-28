package logic;

import java.util.Map;

/**
 * 
 * @author jakub
 *
 */
public abstract class Paragraph {
	protected int order;
	protected String text;
	protected final ParagraphType type;
	
	public Paragraph(ParagraphType type) {
		this.type = type;
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
}
