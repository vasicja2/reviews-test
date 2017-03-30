package review.sentences;

import java.util.HashMap;
import java.util.Map;

public class Keyword {
	private String defaultVal;
	private Map<String, String> variations;
	
	public Keyword(String defVal) {
		this.defaultVal = defVal;
		this.variations = new HashMap<>();
	}
	
	public String getDefaultVal() {
		return defaultVal;
	}
	
	public Map<String, String> getVariations() {
		return variations;
	}
	
	public String getVatiation(String varID) {
		return variations.get(varID);
	}
	
	public void addVariation(String varID, String value) {
		variations.put(varID, value);
	}
}
