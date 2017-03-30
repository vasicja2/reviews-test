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
	private boolean recommended;
	private String value;
	
	public Sentence(int clusterID, int position, double[] attributes, String value) {
		this.clusterID = clusterID;
		this.position = position;
		this.attributes = attributes;
		this.recommended = false;
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
	
	public int getPosition() {
		return position;
	}
	
	public String getValue() {
		return value;
	}
	
	public void recommend() {
		this.recommended = true;
	}
	
	public boolean isRecommended() {
		return recommended;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public double getDistance() {
		return distance;
	}
}
