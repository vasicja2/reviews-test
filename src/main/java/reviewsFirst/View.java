package reviewsFirst;

import static spark.Spark.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import review.Paragraph;
import review.sentences.*;
import spark.ModelAndView;
import spark.Request;
import spark.template.velocity.VelocityTemplateEngine;

public class View {
	private static final String layout = "templates/layout.vtl";
	private static final String head = "templates/head.vtl";
	private static final String headScript = "scripts/head.js";
	private static final String headStyle = "styles/head.css";
	
	public void run() {
		org.apache.log4j.BasicConfigurator.configure();
		staticFiles.location("/");
		port(processBuilder.environment().get("PORT"));
		
		homepage();
		step1();
		step2();
		step3();
		step4();
		admin();
		staticResources();
	}
	
	private Map<String, Object> createModel(String template, int completed, String where) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("template", template);
		model.put("completed", completed);
		model.put("where", where);
		model.put("head", head);
		model.put("headScript", headScript);
		model.put("headStyle", headStyle);
		
		return model;
	}
	
	private void requestFrom0(Request request, Controller ctl) {
		String title = request.queryParams("title");
		String type = request.queryParams("type");
		String name = request.queryParams("reviewer");
		
		if (!ctl.reviewCreated()) 
			ctl.createReview(type, title, name);
		else {
			ctl.changeTitle(title);
			ctl.changeReviewType(type);
			ctl.changeReviewerName(name);
		}
	}
	
	private void requestFrom1(Request request, Controller ctl) {
		Map<String, String[]> paragraphMap = new HashMap<>();
		Map<String, String[]> keywordMap = new HashMap<>();
		
		for (Entry<String, String[]> e : request.queryMap().toMap().entrySet()) {
			if (e.getKey().equals("where")) continue;
			if (e.getKey().startsWith("0")) {
				paragraphMap.put(e.getKey(), e.getValue());
			} else {
				keywordMap.put(e.getKey(), e.getValue());
			}
		}

		if (!paragraphMap.isEmpty()) ctl.addParagraphs(paragraphMap.keySet());
		if (!keywordMap.isEmpty()) ctl.addKeywords(keywordMap);
	}
	
	private void requestFrom4(Request request, Controller ctl) {
		ctl.changeTitle(request.queryParams("title"));
		ctl.changeReviewerName(request.queryParams("reviewer"));

		Map<String, String[]> inputMap = request.queryMap().toMap();
		inputMap.remove("title");
		inputMap.remove("reviewer");
		ctl.saveInput(inputMap);		
	}

	/**
	 * Homepage - select review type and title.
	 */
	private void homepage() {
    	get("/", (request, response) -> {
    		Controller ctl;
    		int completed = 0;
    		if (request.session().isNew()) {				//create a new Controller with a new session
    			ctl = new Controller();
    			request.session().attribute("ctl", ctl);
    			request.session().attribute("completed", completed);
    		} else {
    			completed = Math.max(request.session().attribute("completed"), 0);
    			request.session().attribute("completed", completed);
    			ctl = request.session().attribute("ctl");
    		}

    		Map<String, Object> model = createModel("templates/homepage.vtl", completed, "step0");
    		ArrayList<String> scripts = new ArrayList<>();
    		scripts.add("scripts/homepage.js");
    		
    		model.put("scripts", scripts);
    		model.put("options", ctl.getReviewTypes());		//I need values from the enum here
    		//model.put("sessionID", request.session().id());
    		
    		return new ModelAndView(model, layout);
    	}, new VelocityTemplateEngine());
    	
    	post("/", (request, response) -> {
			int completed = Math.max(request.session().attribute("completed"), 0);
			request.session().attribute("completed", completed);
			Controller ctl = request.session().attribute("ctl");

    		Map<String, Object> model = createModel("templates/homepage.vtl", completed, "step0");
    		ArrayList<String> scripts = new ArrayList<>();
    		scripts.add("scripts/homepage.js");
    		
    		String where = request.queryParams("where");
    		if (where.equals("step3")) {
    			ctl.saveInput(request.queryMap().toMap());
    		} else if (where.equals("step4")) {
    			requestFrom4(request, ctl);
    		}
    		
    		model.put("scripts", scripts);
    		model.put("options", ctl.getReviewTypes());		//I need values from the enum here
    		//model.put("sessionID", request.session().id());
    		
    		return new ModelAndView(model, layout);
    	}, new VelocityTemplateEngine());
	}
	
	/**
	 * Step 2 - select paragraphs to use and keywords
	 */
	private void step1() {
    	get("/paragraphs", (request, response) -> {

    		//			Handle a new session:
    		if (request.session().isNew()) {
    			Controller ctl = new Controller();
    			request.session().attribute("ctl", ctl);
    			request.session().attribute("completed", 0);
    			response.redirect("/");
    		} 

			return new ModelAndView(new HashMap<String, Object>(), layout);
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Step1 - jQuery script for keywords
    	 */
    	get("/step1-keywords.js", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		model.put("kwtypes", KeywordType.values());
    		
    		return new ModelAndView(model, "scripts/step1-keywords.js");
    	}, new VelocityTemplateEngine());
    	
    	post("/paragraphs", (request, response) -> {    		
    		int completed = Math.max(request.session().attribute("completed"), 1);
			request.session().attribute("completed", completed);
			Controller ctl = request.session().attribute("ctl");
			Map<String, Object> model = createModel("templates/step1.vtl", completed, "step1");
			
    		ArrayList<String> scripts = new ArrayList<>();
    		scripts.add("step1-keywords.js");
    		
    		String where = request.queryParams("where");
    		
    		if (where.equals("step0")) {
    			requestFrom0(request, ctl);
    			
    		} else if (where.equals("step3")) {
    			ctl.saveInput(request.queryMap().toMap());
    			
    		} else if (where.equals("step4")) {
    			requestFrom4(request, ctl);
    		}
    		
    		model.put("stepNo", "1");
    		model.put("scripts", scripts);
    		model.put("keywords", ctl.getAvailableKeywords());
    		
    		model.put("title", ctl.getReviewTitle());
    		model.put("type", ctl.getReviewType());
    		model.put("paragraphOptions", ctl.getAvailableParagraphs());
    		
    		return new ModelAndView(model, layout);
    	}, new VelocityTemplateEngine());
	}

	/**
	 * Step 2 - evaluate
	 */
	private void step2() {
    	get("/questions", (request, response) -> {

    		//			Handle a new session:
    		if (request.session().isNew()) {
    			Controller ctl = new Controller();
    			request.session().attribute("ctl", ctl);
    			request.session().attribute("completed", 0);
    			response.redirect("/");
    		} 

			return new ModelAndView(new HashMap<String, Object>(), layout);
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Step 2 - jQuery script
    	 */
    	get("/step2-questions.js", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		Controller ctl = request.session().attribute("ctl");
    		model.put("paragraphs", ctl.getUsedParagraphs());
    		
    		return new ModelAndView(model, "scripts/step2-questions.js");
    	}, new VelocityTemplateEngine());
    	
    	post("/questions", (request, response) -> {
    		int completed = Math.max(request.session().attribute("completed"), 2);
			request.session().attribute("completed", completed);
			Controller ctl = request.session().attribute("ctl");
			Map<String, Object> model = createModel("templates/step2.vtl", completed, "step2");

    		String where = request.queryParams("where");
    		
    		if (where.equals("step0")) {
    			requestFrom0(request, ctl);
    			
    		} else if (where.equals("step1")) {	
    			requestFrom1(request, ctl);
        		
    		} else if (where.equals("step3")) {
    			ctl.saveInput(request.queryMap().toMap());
    		} else if (where.equals("step4")) {
    			requestFrom4(request, ctl);
    		}
    		
    		ArrayList<String> scripts = new ArrayList<>();
    		scripts.add("step2-questions.js");
    		
    		model.put("stepNo", "2");
    		model.put("scripts", scripts);
    		model.put("title", ctl.getReviewTitle());
    		model.put("paragraphs", ctl.getUsedParagraphs());		//This needs a collection of Paragraphs

    		return new ModelAndView(model, layout);
    	}, new VelocityTemplateEngine());
	}
	
	/**
	 * Step 3 - write the review
	 */
	private void step3() {
    	get("/review", (request, response) -> {

    		//			Handle a new session:
    		if (request.session().isNew()) {
    			Controller ctl = new Controller();
    			request.session().attribute("ctl", ctl);
    			request.session().attribute("completed", 0);
    			response.redirect("/");
    		} 

			return new ModelAndView(new HashMap<String, Object>(), layout);
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Step 3 - jQuery script
    	 */
    	get("/step3.js", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		Controller ctl = request.session().attribute("ctl");
    		model.put("paragraphs", ctl.getUsedParagraphs());
    		
    		return new ModelAndView(model, "scripts/step3.js");
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Step 3 - jQuery script
    	 */
    	get("/step3-sentences.js", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		Controller ctl = request.session().attribute("ctl");
    		model.put("paragraphs", ctl.getUsedParagraphs());
    		
    		return new ModelAndView(model, "scripts/step3-sentences.js");
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Step 3 - jQuery script
    	 */
    	get("/step3-keywords.js", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		Controller ctl = request.session().attribute("ctl");
    		model.put("paragraphs", ctl.getUsedParagraphs());
    		model.put("keywords", ctl.getUsedKeywords());
    		model.put("kwtypes", KeywordType.values());
    		
    		return new ModelAndView(model, "scripts/step3-keywords.js");
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Step 3 - jQuery script
    	 */
    	get("/step3-clusters.js", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		//Controller ctl = request.session().attribute("ctl");
    		Map<Paragraph, ArrayList<SentenceCluster>> recommended = request.session().attribute("recommended");
    		model.put("recommended", recommended);
    		
    		return new ModelAndView(model, "scripts/step3-clusters.js");
    	}, new VelocityTemplateEngine());
    	

    	/**
    	 * Step 3 - jQuery script
    	 */
    	get("/step3-recommender.js", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		Controller ctl = request.session().attribute("ctl");
    		model.put("paragraphs", ctl.getUsedParagraphs());
    		
    		return new ModelAndView(model, "scripts/step3-recommender.js");
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Step 3 - recommend more sentences
    	 */
    	post("/review", (request, response) -> {
    		int completed = Math.max(request.session().attribute("completed"), 3);
			request.session().attribute("completed", completed);
			Controller ctl = request.session().attribute("ctl");
			Map<String, Object> model = createModel("templates/step3.vtl", completed, "step3");
    		
    		String where = request.queryParams("where");
    		
    		if (where.equals("step0")) {
    			requestFrom0(request, ctl);
    			
    		} else if (where.equals("step1")) {	
    			requestFrom1(request, ctl);
        		
    		} else if (where.equals("step2")) {
    			TreeMap<String, String[]> params = new TreeMap<String, String[]>(request.queryMap().toMap());
        		Map<Paragraph, List<SentenceCluster>> recommended = ctl.recommend(params);
        		         		
        		model.put("recommended", recommended);
        		request.session().attribute("recommended", recommended);
        		
    		} else if (where.equals("step3")) {
        		ctl.saveInput(request.queryMap().toMap());    
        		
    		} else if (where.equals("step4")) {
    			requestFrom4(request, ctl);
    		}

    		ArrayList<String> scripts = new ArrayList<>();
    		scripts.add("step3-sentences.js");
    		scripts.add("step3-keywords.js");
    		scripts.add("step3-clusters.js");
    		scripts.add("step3-recommender.js");
    		scripts.add("step3.js");

    		model.put("stepNo", "3");
    		model.put("scripts", scripts);
    		model.put("title", ctl.getReviewTitle());
    		Map<Paragraph, ArrayList<SentenceCluster>> recommended = request.session().attribute("recommended");
    		model.put("recommended", recommended);
    		
    		
    		return new ModelAndView(model, layout);
    	}, new VelocityTemplateEngine());
	}

	/**
	 * Step 4 - save the review
	 */
	private void step4() {
		get("/save", (request, response) -> {

    		//			Handle a new session:
    		if (request.session().isNew()) {
    			Controller ctl = new Controller();
    			request.session().attribute("ctl", ctl);
    			request.session().attribute("completed", 0);
    			response.redirect("/");
    		} 

			return new ModelAndView(new HashMap<String, Object>(), layout);
    	}, new VelocityTemplateEngine());
		
    	post("/save", (request, response) -> {
    		int completed = 4;
			request.session().attribute("completed", completed);
			Controller ctl = request.session().attribute("ctl");
			Map<String, Object> model = createModel("templates/step4.vtl", completed, "step4");
    		
			String where = request.queryParams("where");
			
			if (where.equals("step0")) {
    			requestFrom0(request, ctl);
    			
    		} else if (where.equals("step1")) {	
    			requestFrom1(request, ctl);
        		
    		} else if (where.equals("step2")) {
    			TreeMap<String, String[]> params = new TreeMap<String, String[]>(request.queryMap().toMap());
        		Map<Paragraph, List<SentenceCluster>> recommended = ctl.recommend(params);
        		
        		model.put("recommended", recommended);
        		request.session().attribute("recommended", recommended);
        		
    		} else if (where.equals("step3")) {
        		ctl.saveInput(request.queryMap().toMap());    
        		
    		}
    		
    		ArrayList<String> scripts = new ArrayList<>();
    		scripts.add("step4.js");
    		
    		model.put("scripts", scripts);
    		model.put("stepNo", "4");
    		model.put("sessionID", request.session().id());
    		model.put("title", ctl.getReviewTitle());
    		model.put("type", ctl.getReviewType());
    		model.put("reviewer", ctl.getReviewerName());
    		model.put("paragraphs", ctl.getUsedParagraphs());
    		

    		return new ModelAndView(model, layout);
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Step 4 - jQuery script
    	 */
    	get("/step4.js", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		Controller ctl = request.session().attribute("ctl");
    		model.put("paragraphs", ctl.getUsedParagraphs());
    		return new ModelAndView(model, "scripts/step4.js");
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Download the saved review
    	 */
    	get("/review.tex", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();    		
    		Controller ctl = request.session().attribute("ctl");
    		
    		requestFrom4(request, ctl);
    		
    		model.put("revTitle", ctl.getReviewTitle());
    		model.put("reviewedBy", ctl.getReviewerName());
    		model.put("type", ctl.getReviewType());
			LocalDate localDate = LocalDate.now();
			model.put("date", DateTimeFormatter.ofPattern("dd/MM/yyyy").format(localDate));
    		
    		
    		model.put("paragraphs", ctl.getUsedParagraphs());
    		response.type("text/tex");
    		
    		return new ModelAndView(model, "TEXfiles/template.tex");
    	}, new VelocityTemplateEngine());
	}

	private void admin() {
		get("/admin", (request, response) -> {
			Controller ctl;
    		int completed = -1;
			request.session().attribute("completed", -1);
			
    		if (request.session().isNew()) {				//create a new Controller with a new session
    			ctl = new Controller();
    			request.session().attribute("ctl", ctl);
    		} else {
    			ctl = request.session().attribute("ctl");
    		}
    		
    		ArrayList<String> scripts = new ArrayList<>();
    		scripts.add("admin.js");
    		
    		Map<String, Object> model = createModel("templates/admin.vtl", completed, "step0");
    		model.put("template", "templates/admin.vtl");
    		model.put("scripts", scripts);
    		model.put("sentenceDB", ctl.getSentenceDB());
			
			return new ModelAndView(model, layout);
    	}, new VelocityTemplateEngine());
		
		/**
    	 * Admin - jQuery script
    	 */
    	get("/admin.js", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		Controller ctl = request.session().attribute("ctl");
    		model.put("paragraphs", ctl.getAvailableParagraphs());
    		
    		return new ModelAndView(model, "scripts/admin.js");
    	}, new VelocityTemplateEngine());
    	
    	/**
    	 * Admin - save new sentences and redirect to homepage
    	 */
    	post("/admin", (request, response) -> {
    		Map<String, Object> model = new HashMap<String, Object>();
    		Controller ctl = request.session().attribute("ctl");
    		Map<String, String[]> params = new HashMap<>();
    		
    		for (Entry<String, String[]> e : request.queryMap().toMap().entrySet()) {
    			if (e.getKey().endsWith("new")) {
    				params.put(e.getKey(), e.getValue());
    			}
    		}
    		
    		ctl.updateSentenceDB(params);
    		response.redirect("/");
    		
			return new ModelAndView(model, layout);
    	}, new VelocityTemplateEngine());
	}
	
	private void staticResources() {
		get("aehlke-tag-it/css/jquery.tagit.css", (request, response) -> {
			
			return new ModelAndView(new HashMap<String, Object>(), "aehlke-tag-it/css/jquery.tagit.css");
    	}, new VelocityTemplateEngine());
		

		get("aehlke-tag-it/js/tag-it.js", (request, response) -> {
			
			return new ModelAndView(new HashMap<String, Object>(), "aehlke-tag-it/js/tag-it.js");
    	}, new VelocityTemplateEngine());
		
		get("slider/css/slider.css", (request, response) -> {
			
			return new ModelAndView(new HashMap<String, Object>(), "slider/css/slider.css");
    	}, new VelocityTemplateEngine());


		get("slider/js/bootstrap-slider.js", (request, response) -> {
			
			return new ModelAndView(new HashMap<String, Object>(), "slider/js/bootstrap-slider.js");
		}, new VelocityTemplateEngine());
		
		get("scripts/homepage.js", (request, response) -> {
			
			return new ModelAndView(new HashMap<String, Object>(), "scripts/homepage.js");
    	}, new VelocityTemplateEngine());
	}
}
