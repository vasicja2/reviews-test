package reviewsFirst;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;

import review.*;
import review.sentences.Keyword;
import review.sentences.KeywordType;
import review.sentences.Sentence;
import review.sentences.SentenceCluster;
import review.sentences.SentenceDBHandler;

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
	 * Checks if the review has been created.
	 * @return
	 */
	public boolean reviewCreated() {
		return (review != null);
	}
	
	/**
	 * Creates a new review.
	 * @param typeOption - Option in the HTML request, specifying the desired review type
	 * @param title - The title of the review
	 * @param name - The name of the reviewer
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
	 * Change the title of the review
	 * @param title
	 */
	public void changeTitle(String title) {
		review.setTitle(title);
	}
	
	/**
	 * Chenge type of the review
	 * @param typeOption
	 */
	public void changeReviewType(String typeOption) {
		ReviewType type = null;
		
		for (ReviewType t : ReviewType.values()) {
			if (typeOption.equals(t.getName())) {
				type = t;
			}
		}
		
		review.setType(type);
	}
	
	/**
	 * Change name of th reviewer
	 * @param name
	 */
	public void changeReviewerName(String name) {
		review.setReviewerName(name);
	}
	
	/**
	 * Creates the paragraphs in the review.
	 * @param paragraphs - Set of paragraph type IDs from the HTML request.
	 */
	public void addParagraphs(Set<String> paragraphs) {
		PriorityQueue<String> queue = new PriorityQueue<>(paragraphs);	//the paragraphs come unsorted -> sort them
		ArrayList<String> alreadyAdded = new ArrayList<>();
		
		if (!review.getParagraphs().isEmpty()) {
			for (Paragraph p : review.getParagraphs()) {
				alreadyAdded.add(p.getId());
			}
		}
		
		String parID = queue.poll();									
		int order = 1;
		
		while(parID != null) {											//make sure they are added in the right order
			
			if (alreadyAdded.contains(parID)) {
				alreadyAdded.remove(parID);
				parID = queue.poll();
				order++;
				continue;
			}
			
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
		
		for (String ID : alreadyAdded) {
			review.deleteParagraph(ID);
		}
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
			if (id.endsWith("hidden")) {
				if (input.get(id)[0].trim().isEmpty()) continue;
				
				Paragraph par = review.getParagraph(id.split("-")[0]);
				
				for (String s : input.get(id)[0].split(" ")) {
					int clusterID = Integer.parseInt(s.split("\\.")[1]);
					String type = s.split("\\.")[2];
					int pos = Integer.parseInt(s.split("\\.")[2].split("o")[0]);
					
					par.removeSentence(clusterID, type, pos);
				}
				continue;
			}
			
			if(id.equals("where")) continue;
			Paragraph par = review.getParagraph(id);
			par.setText(input.get(id)[0]);
		}
	}
	
	/**
	 * Processes the input from HTML request into attributes and returns recommended sentences for each paragraph.
	 * @param attributeMap
	 * @return
	 */
	public Map<Paragraph, List<SentenceCluster>> recommend(TreeMap<String, String[]> attributeMap) {
		Map<String, double[]> attributes = new TreeMap<>();
		
		/*parse input from HTML request*/
		String prev = "";
		int index = 0;
		ArrayList<String> params = new ArrayList<>();
		for (Entry<String, String[]> e : attributeMap.entrySet()) {
			if (e.getKey().equals("where")) continue;
			if ((index != 0) && !e.getKey().startsWith(prev)) {
				double[] attr = new double[params.size()];
				for(int i=0; i<params.size(); i++) {
					attr[i] = Double.parseDouble(params.get(i));
				}
				attributes.put(prev, attr);
				params.clear();
			}
			
			params.add(e.getValue()[0]);
			prev = e.getKey().substring(0, e.getKey().length()-1);
			index++;
		}
		double[] attr = new double[params.size()];
		for(int i=0; i<params.size(); i++) {
			attr[i] = Double.parseDouble(params.get(i));
		}
		attributes.put(prev, attr);
		
		return review.recommend(attributes);
	}
	
	/**
	 * Returns recommended sentences for each paragraph based on previously saved evaluation.
	 * @return 
	 */
	public Map<Paragraph, List<SentenceCluster>> recommend() {
		return review.recommend();
	}
	
	/**
	 * Returns all avaliable keywords and their types
	 * @return
	 */
	
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
				Keyword k = new Keyword(type, val);
				review.addKeyword(k);
			}
		}
	}
	
	public Map<ParagraphType, ArrayList<Sentence>> getSentenceDB() {
		Map<ParagraphType, ArrayList<Sentence>> result = new TreeMap<>();
		
		for (ParagraphType type : ParagraphType.values()) {
			if (type == ParagraphType.CUSTOM) continue;
			result.put(type, SentenceDBHandler.getInstance().loadSentencesSimple(type));
		}
		
		return result;
	}
	
	public void updateSentenceDB(Map<String, String[]> input) {
		for (String id : input.keySet()) {
			String parID = id.substring(0, id.length()-4);
			ParagraphType type = ParagraphType.TOPIC;
			for (ParagraphType t : ParagraphType.values()) {
				if (t.getId().equals(parID)) {
					type = t;
					break;
				}
			}
			
			
			SentenceDBHandler.getInstance().saveNewSentences(type, input.get(id)[0]);
		}
	}
	
	public ArrayList<Keyword> getUsedKeywords() {
		return review.getChosenKeywords();
	}
	
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
	
	public double getFinalGrade() {
		Paragraph par = review.getParagraph("07overall");
		
		if (par != null) {
			return par.getEvaluation()[0];
		} else {
			return 0;
		}
	}

	public KeywordType[] getAvailableKeywords() {
		return KeywordType.values();
	}
}
