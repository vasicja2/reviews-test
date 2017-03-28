package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import logic.sentences.Sentence;
import logic.sentences.SentenceLoader;
import logic.sentences.SentenceRecommender;

public class Review {
	private ReviewType type;
	private String title;
	private ArrayList<Paragraph> paragraphs;
	private SentenceLoader sentenceLoader;
	private SentenceRecommender sentenceRecommender;
	
	public Review(ReviewType type, String title) {
		this.type = type;
		this.title = title;
		this.paragraphs = new ArrayList<Paragraph>();
		this.sentenceLoader = new SentenceLoader(type);
		this.sentenceRecommender = new SentenceRecommender();
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
	 * Returns a map of all used paragraphs along with recommended sentences.
	 * @param attributes
	 * @return
	 */
	public Map<Paragraph, ArrayList<Sentence>> recommend(ArrayList<float[]> attributes) {
		Map<Paragraph, ArrayList<Sentence>> result = new HashMap<>();
		
		int i=0;
		for (Paragraph p : paragraphs) {
			result.put(p, sentenceRecommender.recommend(p.getType(), attributes.get(i)));
			i++;
		}
		
		return result;
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
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setType(ReviewType type) {
		this.type = type;
	}
}
