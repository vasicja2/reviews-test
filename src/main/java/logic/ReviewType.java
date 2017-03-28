package logic;

public enum ReviewType {
	BC_THESIS("Bachelor thesis"), 
	MG_THESIS("Master thesis"), 
	DISSERTATION("Dissertation"), 
	ARTICLE("Article"), 
	HAB_THESIS("Habilitaton thesis"); 
	
	private final String name;
	
	ReviewType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
