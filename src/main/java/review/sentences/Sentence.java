package review.sentences;


/**
 * 
 * @author jakub
 *
 */
public class Sentence {
	private int clusterID;
	private int position;
	private double distance;
	private double[] attributes;
	private String[] additionalAttributes;
	private String value;
	
	public Sentence(int clusterID, int position, double[] attributes, String[] additionalAttributes, String value) {
		this.clusterID = clusterID;
		this.position = position;
		this.attributes = attributes;
		this.additionalAttributes = additionalAttributes;
		this.value = value;
		this.distance = 0;
	}
	
	public int getClusterID() {
		return clusterID;
	}
	
	public void setClusterID(int clusterID) {
		this.clusterID = clusterID;
	}
	
	public double[] getAttributes() {
		return attributes;
	}
	
	public String[] getAdditionalAttributes() {
		return additionalAttributes;
	}
	
	public int getPosition() {
		return position;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public double getDistance() {
		return distance;
	}
}
