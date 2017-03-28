package reviewsFirst;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import review.Paragraph;
import review.Review;

public class TexSaver {
	private final Review review;
	private final String filename;
	
	public TexSaver(Review review, String filename) {
		this.review = review;
		this.filename = filename;
	}
	
	public void save() {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
			bw.write("\\documentclass[11pt]{article}");
			bw.newLine();
			bw.write("\\usepackage[utf8]{inputenc}");
			bw.newLine();
			bw.newLine();
			bw.write("\\begin{document}");
			bw.newLine();
			
			/*Head*/
			bw.write("\\title{" + review.getTitle() + "\\\\" + review.getType().getName() + " review}");
			bw.newLine();
			bw.write("\\author{" + review.getReviewerName() + "}");
			bw.newLine();
			LocalDate localDate = LocalDate.now();
	        bw.write("\\date{" + DateTimeFormatter.ofPattern("dd/MM/yyyy").format(localDate) + "}");
			bw.newLine();
	        bw.write("\\maketitle");
			bw.newLine();
			bw.newLine();
	        
	        /*Body*/
	        for (Paragraph p : review.getParagraphs()) {
	        	bw.write("\\section{" + p.getTitle() + "}");
				bw.newLine();
	        	bw.write("\t\\paragraph{}");
				bw.newLine();
	        	bw.write("\t" + p.getText());
				bw.newLine();
	        	bw.newLine();
	        }
	        
	        /*End*/
	        bw.write("\\end{document}");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
