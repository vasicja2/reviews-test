package review;

public enum ReviewType {
	BC_THESIS("Bachelor thesis"), 
	MG_THESIS("Master thesis");
	
	private final String name;
	
	ReviewType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
