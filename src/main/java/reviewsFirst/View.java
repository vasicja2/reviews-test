package reviewsFirst;

import static spark.Spark.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import review.sentences.KeywordType;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

public class View {
	private static final String layout = "templates/layout.vtl";
	
	
	public void run() {
		org.apache.log4j.BasicConfigurator.configure();
		staticFiles.location("/src/main/resources");
		port(Integer.valueOf(System.getenv("PORT")));
		
		/**
		 * Step 1 - select review type and title.
		 */
    	get("/", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		Controller ctl;
    		if (request.session().isNew()) {				//create a new Controller with a new session
    			ctl = new Controller();
    			request.session().attribute("ctl", ctl);
    		} else {
    			ctl = request.session().attribute("ctl");
    		}
    		
    		model.put("template", "templates/step1.vtl");
    		model.put("options", ctl.getReviewTypes());		//I need values from the enum here
    		//model.put("sessionID", request.session().id());
    		
    		return new ModelAndView(model, layout);
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Step 2 - select paragraphs to use
    	 */
    	get("/paragraphs", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		Controller ctl;
    		if (request.session().isNew()) {					//new session -> redirect
    			ctl = new Controller();
    			request.session().attribute("ctl", ctl);
    			response.redirect("/");
    		} else {
    			ctl = request.session().attribute("ctl");
    		}
    		
    		model.put("template", "templates/step2.vtl");
    		model.put("stepNo", "2");
    		model.put("jsFile", "step2-keywords.js");
    		model.put("keywords", ctl.getAvailableKeywords());
    		//model.put("sessionID", request.session().id());
    		
    		String title = request.queryParams("title");
    		String type = request.queryParams("type");
    		String name = request.queryParams("reviewer");
    		
    		/*if (!ctl.reviewCreated()) */ctl.createReview(type, title, name);
    		
    		model.put("title", ctl.getReviewTitle());
    		model.put("type", ctl.getReviewType());
    		model.put("paragraphOptions", ctl.getAvailableParagraphs());
    		
    		return new ModelAndView(model, layout);
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Step2 - jQuery script for keywords
    	 */
    	get("/step2-keywords.js", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		model.put("kwtypes", KeywordType.values());
    		
    		return new ModelAndView(model, "scripts/step2-keywords.js");
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Step 3 - evaluate
    	 */
    	get("/questions", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		Controller ctl;
    		if (request.session().isNew()) {					//new session -> redirect
    			ctl = new Controller();
    			request.session().attribute("ctl", ctl);
    			response.redirect("/");
    		} else {
    			ctl = request.session().attribute("ctl");
    		}
    		
    		Map<String, String[]> fullMap = request.queryMap().toMap();
    		Map<String, String[]> paragraphMap = new HashMap<>();
    		Map<String, String[]> keywordMap = new HashMap<>();
    		for (Entry<String, String[]> e : fullMap.entrySet()) {
    			if (e.getKey().startsWith("0")) {
    				paragraphMap.put(e.getKey(), e.getValue());
    			} else {
    				keywordMap.put(e.getKey(), e.getValue());
    			}
    		}
    		
    		model.put("template", "templates/step3.vtl");
    		model.put("stepNo", "3");
    		model.put("jsFile", "step3-questions.js");
    		model.put("title", ctl.getReviewTitle());
    		//model.put("sessionID", request.session().id());
    		
    		if (!request.queryParams().isEmpty()) {			//just in case the user types in the address manually	
    			ctl.clearParagraphs();
        		ctl.addParagraphs(paragraphMap.keySet());
        		ctl.addKeywords(keywordMap);
    		}
    		
    		model.put("paragraphs", ctl.getUsedParagraphs());	

    		return new ModelAndView(model, layout);
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Step 3 - jQuery script
    	 */
    	get("/step3-questions.js", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		Controller ctl = request.session().attribute("ctl");
    		model.put("paragraphs", ctl.getUsedParagraphs());
    		
    		return new ModelAndView(model, "scripts/step3-questions.js");
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Step 4 - write the review
    	 */
    	get("/review", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		Controller ctl;
    		if (request.session().isNew()) {					//new session -> redirect
    			ctl = new Controller();
    			request.session().attribute("ctl", ctl);
    			response.redirect("/");
    		} else {
    			ctl = request.session().attribute("ctl");
    		}
    		
    		model.put("template", "templates/step4.vtl");
    		model.put("stepNo", "4");
    		model.put("jsFile", "step4-sentences.js");
    		model.put("title", ctl.getReviewTitle());

    		//model.put("sessionID", request.session().id());
    		TreeMap<String, String[]> params = new TreeMap<String, String[]>(request.queryMap().toMap());
    		model.put("recommended", ctl.recommend(params));
    		
    		return new ModelAndView(model, layout);
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Step 4 - jQuery script
    	 */
    	get("/step4-sentences.js", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		Controller ctl = request.session().attribute("ctl");
    		model.put("paragraphs", ctl.getUsedParagraphs());
    		model.put("keywords", ctl.getUsedKeywords());
    		model.put("kwtypes", KeywordType.values());
    		
    		return new ModelAndView(model, "scripts/step4-sentences.js");
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Step 4 - recommend more sentences
    	 */
    	post("/review", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		Controller ctl;
    		if (request.session().isNew()) {					//new session -> redirect
    			ctl = new Controller();
    			request.session().attribute("ctl", ctl);
    			response.redirect("/");
    		} else {
    			ctl = request.session().attribute("ctl");
    		}
    		
    		ctl.saveInput(request.queryMap().toMap());

    		model.put("template", "templates/step4.vtl");
    		model.put("stepNo", "4");
    		model.put("jsFile", "step4-sentences.js");
    		model.put("title", ctl.getReviewTitle());
    		model.put("recommended", ctl.recommend());
    		
    		
    		return new ModelAndView(model, layout);
    	}, new VelocityTemplateEngine());
    	
    	post("/questions", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		Controller ctl = request.session().attribute("ctl");

    		ctl.saveInput(request.queryMap().toMap());
    		
    		model.put("template", "templates/step3.vtl");
    		model.put("stepNo", "3");
    		model.put("jsFile", "step3-questions.js");
    		model.put("title", ctl.getReviewTitle());
    		model.put("paragraphs", ctl.getUsedParagraphs());		//This needs a collection of Paragraphs

    		return new ModelAndView(model, layout);
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Step 5 - save the review
    	 */
    	post("/save", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		Controller ctl;
    		if (request.session().isNew()) {					//new session -> redirect
    			ctl = new Controller();
    			request.session().attribute("ctl", ctl);
    			response.redirect("/");
    		} else {
    			ctl = request.session().attribute("ctl");
    		}
    		
    		ctl.saveInput(request.queryMap().toMap());
    		
    		model.put("template", "templates/step5.vtl");
    		model.put("stepNo", "5");
    		model.put("sessionID", request.session().id());
    		model.put("title", ctl.getReviewTitle());
    		model.put("type", ctl.getReviewType());
    		model.put("reviewer", ctl.getReviewerName());
    		model.put("paragraphs", ctl.getUsedParagraphs());
    		

    		return new ModelAndView(model, layout);
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Download the saved review
    	 */
    	get("/review.tex", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		Controller ctl;
    		if (request.session().isNew()) {					//new session -> redirect
    			ctl = new Controller();
    			request.session().attribute("ctl", ctl);
    			response.redirect("/");
    		} else {
    			ctl = request.session().attribute("ctl");
    		}
    		/*String filename = "TEXfiles/" + request.session().id() + ".tex";
    		ctl.saveReview("src/main/resources/" + filename);*/
    		
    		model.put("title", ctl.getReviewTitle());
    		model.put("reviewer", ctl.getReviewerName());
    		model.put("type", ctl.getReviewType());
			LocalDate localDate = LocalDate.now();
			model.put("date", DateTimeFormatter.ofPattern("dd/MM/yyyy").format(localDate));
    		
    		
    		model.put("paragraphs", ctl.getUsedParagraphs());
    		response.type("text/tex");
    		
    		return new ModelAndView(model, "TEXfiles/template.tex");
    	}, new VelocityTemplateEngine());
		
	get("/aehlke-tag-it/css/jquery.tagit.css", (request, response) -> {		
    		Map<String, Object> model = new HashMap<String, Object>();
		
		return new ModelAndView(model, "/aehlke-tag-it/css/jquery.tagit.css");
    	}, new VelocityTemplateEngine());
		
	}
}
