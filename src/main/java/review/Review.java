package review;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import review.sentences.Keyword;
import review.sentences.SentenceCluster;
import review.sentences.SentenceRecommender;

public class Review {
	private ReviewType type;
	private String title;
	private String reviewerName;
	private ArrayList<Paragraph> paragraphs;
	private ArrayList<Keyword> chosenKeywords;
	
	public Review(ReviewType type, String title, String name) {
		this.type = type;
		this.title = title;
		this.reviewerName = name;
		this.paragraphs = new ArrayList<Paragraph>();
		this.chosenKeywords = new ArrayList<>();
	}
	
	/**
	 * Creates a new paragraph of the specified type.
	 * @param type
	 */
	public void addStandardParagraph(ParagraphType type) {
		paragraphs.add(new StandardParagraph(type));
	}
	
	/**
	 * Deletes all the paragraphs.
	 */
	public void clearParagraphs() {
		paragraphs.clear();
	}
	
	public void deleteParagraph(String ID) {
		Paragraph toRemove = null;
		
		for (Paragraph p : paragraphs) {
			if (p.getId() == ID){
				toRemove = p;
			}
		}
		
		if (toRemove != null) paragraphs.remove(toRemove);
	}
	
	/**
	 * Returns a map of all used paragraphs along with recommended sentences and saves the evaluation.
	 * @param attributes
	 * @return
	 */
	public Map<Paragraph, List<SentenceCluster>> recommend(Map<String, double[]> attributes) {
		Map<Paragraph, List<SentenceCluster>> result = new TreeMap<>();
		
		for(Paragraph p : paragraphs) {
			if(attributes.containsKey(p.getId())) {
				p.setEvaluation(attributes.get(p.getId()));
			}
		}
		
		for (Paragraph p : paragraphs) {
			if (!attributes.containsKey(p.getId())) {
				result.put(p, new ArrayList<>());
				continue;
			}			
			
			ArrayList<SentenceCluster> alreadyLoaded = p.getRecommended();
			
			if (alreadyLoaded.isEmpty()) {
				result.put(p, SentenceRecommender.getInstance().recommend(p, paragraphs, attributes.get(p.getId())));
			} else {
				result.put(p, SentenceRecommender.getInstance().recommend(p, paragraphs, attributes.get(p.getId()), alreadyLoaded));
			}
		}
		
		return result;
	}
	
	/**
	 * Returns a map of all used paragraphs along with recommended sentences 
	 * based on the evaluation that has been previously saved.
	 * @return
	 */
	public Map<Paragraph, List<SentenceCluster>> recommend() {
		Map<Paragraph, List<SentenceCluster>> result = new TreeMap<>();
		
		for (Paragraph p : paragraphs) {			
			ArrayList<SentenceCluster> alreadyLoaded = p.getRecommended();
		
			if (alreadyLoaded.isEmpty()) {
				result.put(p, SentenceRecommender.getInstance().recommend(p, paragraphs, p.getEvaluation()));
			} else {
				result.put(p, SentenceRecommender.getInstance().recommend(p, paragraphs, p.getEvaluation(), alreadyLoaded));				
			}
		}
		
		return result;
	}
	
	/**
	 * Deletes all used keywords
	 */
	public void clearKeywords() {
		this.chosenKeywords.clear();
	}
	
	/**
	 * Adds a keyword of the specified type to the review.
	 * @param type
	 * @param kw
	 */
	public void addKeyword(Keyword kw) {
		chosenKeywords.add(kw);
	}
	
	/**
	 * Returns all used paragraphs.
	 * @return
	 */
	public ArrayList<Paragraph> getParagraphs() {
		return paragraphs;
	}
	
	/**
	 * Returns the title of the review.
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Returns the paragraph of the specified ID.
	 * @param id
	 * @return
	 */
	public Paragraph getParagraph(String id) {
		for (Paragraph p : paragraphs) {
			if (p.getId().equals(id)) {
				return p;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the type of this review.
	 * @return
	 */
	public ReviewType getType() {
		return type;
	}
	
	/**
	 * Returns a map of chosen keywords.
	 * @return
	 */
	public ArrayList<Keyword> getChosenKeywords() {
		return chosenKeywords;
	}
	
	public String getReviewerName() {
		return reviewerName;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setType(ReviewType type) {
		this.type = type;
	}
	
	public void setReviewerName(String reviewerName) {
		this.reviewerName = reviewerName;
	}
}
