package review;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import review.sentences.Keyword;
import review.sentences.KeywordType;
import review.sentences.Sentence;
import review.sentences.SentenceLoader;
import review.sentences.SentenceRecommender;

public class Review {
	private ReviewType type;
	private String title;
	private String reviewerName;
	private ArrayList<Paragraph> paragraphs;
	private SentenceLoader sentenceLoader;
	private SentenceRecommender sentenceRecommender;
	private Map<KeywordType, ArrayList<Keyword>> chosenKeywords;
	
	public Review(ReviewType type, String title, String name) {
		this.type = type;
		this.title = title;
		this.reviewerName = name;
		this.paragraphs = new ArrayList<Paragraph>();
		this.sentenceLoader = new SentenceLoader(type);
		this.sentenceRecommender = new SentenceRecommender();
		this.chosenKeywords = new HashMap<>();
		for (KeywordType t : KeywordType.values()) {
			this.chosenKeywords.put(t, new ArrayList<>());
		}
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
	
	/**
	 * Loads available sentences from the database for all the paragraphs. 
	 * This should be called after all the paragraphs were added.
	 */
	public void loadSentenceDB() {
		for (Paragraph p : paragraphs) {
			if (p.getType() == ParagraphType.CUSTOM) continue;
			sentenceRecommender.addAvaliableSentences(p.getType(), sentenceLoader.loadSentences(p.getType()));
		}
	}
	
	/**
	 * Returns a map of all used paragraphs along with recommended sentences and saves the evaluation.
	 * @param attributes
	 * @return
	 */
	public Map<Paragraph, ArrayList<Sentence>> recommend(ArrayList<float[]> attributes) {
		Map<Paragraph, ArrayList<Sentence>> result = new TreeMap<>();
		
		int i=0;
		for (Paragraph p : paragraphs) {
			result.put(p, sentenceRecommender.recommend(p, attributes.get(i)));
			p.setEvaluation(attributes.get(i));
			i++;
		}
		
		return result;
	}
	
	/**
	 * Returns a map of all used paragraphs along with recommended sentences 
	 * based on the evaluation that has been previously saved.
	 * @return
	 */
	public Map<Paragraph, ArrayList<Sentence>> recommend() {
		Map<Paragraph, ArrayList<Sentence>> result = new TreeMap<>();
		
		for (Paragraph p : paragraphs) {
			result.put(p, sentenceRecommender.recommend(p, p.getEvaluation()));
		}
		
		return result;
	}
	
	/**
	 * Deletes all used keywords
	 */
	public void clearKeywords() {
		for(Entry<KeywordType, ArrayList<Keyword>> e : chosenKeywords.entrySet()){
			e.getValue().clear();
		}
	}
	
	/**
	 * Adds a keyword of the specified type to the review.
	 * @param type
	 * @param kw
	 */
	public void addKeyword(KeywordType type, Keyword kw) {
		chosenKeywords.get(type).add(kw);
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
	public Map<KeywordType, ArrayList<Keyword>> getChosenKeywords() {
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
}
