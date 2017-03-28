package reviewsFirst;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

import review.*;
import review.sentences.Keyword;
import review.sentences.KeywordHandler;
import review.sentences.KeywordType;
import review.sentences.Sentence;

/**
 * 
 * @author jakub
 *
 */
public class Controller {
	private Review review = null;
		
	/**
	 * Returns all available types of paragraph.
	 * @return
	 */
	public ArrayList<ParagraphType> getAvailableParagraphs() {		
		ArrayList<ParagraphType> result = new ArrayList<>();
			for (ParagraphType type : ParagraphType.values()) {
				if (type == ParagraphType.CUSTOM) continue;
				result.add(type);
			}
		
		return result;
	}
	
	/**
	 * Returns all available types of review.
	 * @return
	 */
	public ReviewType[] getReviewTypes() {		
		return ReviewType.values();
	}
	
	/**
	 * Checks of the review was created.
	 * @return
	 */
	public boolean reviewCreated() {
		if (review == null) return false;
		else return true;
	}
	
	/**
	 * Creates a new review.
	 * @param typeOption - Option in the HTML request, specifying the desired review type
	 * @param title - The title of the review
	 */
	public void createReview(String typeOption, String title, String name) {
		ReviewType type = null;
		
		for (ReviewType t : ReviewType.values()) {
			if (typeOption.equals(t.getName())) {
				type = t;
			}
		}
		
		review = new Review(type, title, name);
	}
	
	/**
	 * Creates the paragraphs in the review.
	 * @param paragraphs - Set of paragraph type IDs from the HTML request.
	 */
	public void addParagraphs(Set<String> paragraphs) {
		PriorityQueue<String> queue = new PriorityQueue<>(paragraphs);	//the paragraphs come unsorted -> sort them
		
		String parID = queue.poll();									
		int order = 1;
		
		while(parID != null) {											//make sure they are added in the right order
			for (ParagraphType type : ParagraphType.values()) {
				if (type.getId().equals(parID)) {
					review.addStandardParagraph(type);
					review.getParagraph(parID).setOrder(order);
					order++;
					break;
				}
			}
			
			parID = queue.poll();
		}
		
		review.loadSentenceDB();
	}
	
	/**
	 * Deletes all the paragraphs in the review.
	 */
	public void clearParagraphs() {
		review.clearParagraphs();
	}
	
	/**
	 * Saves the text of all paragraphs.
	 * @param input - Key = paragraph ID, Value = the text to save
	 */
	public void saveInput(Map<String, String[]> input) {
		for (String id : input.keySet()) {
			if (id.startsWith("hidden")) {
				String parId = id.substring(6, id.length());
				Paragraph par = review.getParagraph(parId);
				String sections = input.get(id)[0];
				for(String sec : sections.split(" ")) {
					if (!sec.isEmpty()) 
						par.addToSection(Integer.parseInt(sec));
				}
			} else {
				Paragraph par = review.getParagraph(id);
				par.setText(input.get(id)[0]);
			}
		}
	}
	
	/**
	 * Processes the input from HTML request into attributes and returns recommended sentences for each paragraph.
	 * @param attributeMap
	 * @return
	 */
	public Map<Paragraph, ArrayList<Sentence>> recommend(TreeMap<String, String[]> attributeMap) {
		ArrayList<float[]> attributes = new ArrayList<>();
		
		/*parse input from HTML request*/
		String prev = "";
		int index = 0;
		ArrayList<String> params = new ArrayList<>();
		for (Entry<String, String[]> e : attributeMap.entrySet()) {
			if ((index != 0) && !e.getKey().startsWith(prev)) {
				float[] attr = new float[params.size()];
				for(int i=0; i<params.size(); i++) {
					attr[i] = Float.parseFloat(params.get(i));
				}
				attributes.add(attr);
				params.clear();
			}
			
			params.add(e.getValue()[0]);
			prev = e.getKey().substring(0, e.getKey().length()-1);
			index++;
		}
		float[] attr = new float[params.size()];
		for(int i=0; i<params.size(); i++) {
			attr[i] = Float.parseFloat(params.get(i));
		}
		attributes.add(attr);
		
		return review.recommend(attributes);
	}
	
	/**
	 * Returns recommended sentences for each paragraph based on previously saved evaluation.
	 * @return 
	 */
	public Map<Paragraph, ArrayList<Sentence>> recommend() {
		return review.recommend();
	}
	
	/**
	 * Returns all avaliable keywords and their types
	 * @return
	 */
	public Map<KeywordType, ArrayList<Keyword>> getAvailableKeywords() {
		return KeywordHandler.getInstance().getKeywordDB();
	}
	
	private Keyword findKwByValue(KeywordType type, String value) {
		for (Keyword k : KeywordHandler.getInstance().getKeywordDB().get(type)) {
			if (k.getDefaultVal().equals(value)) {
				return k;
			}
		}
		
		return null;
	}
	
	/**
	 * Adds all chosen kewords to the review.
	 * @param map
	 */
	public void addKeywords(Map<String, String[]> map) {
		review.clearKeywords();
		
		for (Entry<String, String[]> e : map.entrySet()) {
			String typeID = e.getKey();
			typeID = typeID.substring(0, typeID.length()-5);
			KeywordType type = null;
			
			for (KeywordType t : KeywordType.values()) {
				if (t.getId().equals(typeID)) {
					type = t;
					break;
				}
			}
			
			for (String val : e.getValue()) {
				Keyword k = findKwByValue(type, val);
				if (k == null) {
					k = new Keyword(val);
				}
				review.addKeyword(type, k);
			}
		}
	}
	
	public Map<KeywordType, ArrayList<Keyword>> getUsedKeywords() {
		return review.getChosenKeywords();
	}
	
	/*public void saveReview(String filepath) {
		TexSaver texsaver = new TexSaver(review, filepath);
		texsaver.save();
	}*/
	
	public ArrayList<Paragraph> getUsedParagraphs() {		
		return review.getParagraphs();
	}
	
	public String getReviewTitle() {
		return review.getTitle();
	}
	
	public String getReviewType() {
		return review.getType().getName();
	}
	
	public String getReviewerName() {
		return review.getReviewerName();
	}
}
