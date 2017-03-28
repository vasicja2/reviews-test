package logic.sentences;


/**
 * 
 * @author jakub
 *
 */
public class Sentence {
	private int position;
	private float[] attributes;
	private float distance;
	private boolean recommended;
	private String value;
	
	public Sentence(int position, float[] attributes, String value) {
		this.position = position;
		this.attributes = attributes;
		this.distance = 0;
		this.recommended = false;
		this.value = value;
	}
	
	public float[] getAttributes() {
		return attributes;
	}
	
	public int getPosition() {
		return position;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setDistance(float distance) {
		this.distance = distance;
	}
	
	public float getDistance() {
		return distance;
	}
	
	public void recommend() {
		this.recommended = true;
	}
	
	public boolean isRecommended() {
		return recommended;
	}
}
