var submitForm = function() {
	$("#mainForm").submit();
}

$(document).ready(function(){
	var form = $("#mainForm");
	$("#btn_submit").click(function(){
		form.submit();
	});
	
	$("#btn_back").click(function(){
		form.attr('action', "/paragraphs");
		form.attr('method', "POST");
		form.submit();
	});
	
    $( "#accordion" ).accordion({
	    autoHeight: false,
	    collapsible : true
	});
	    
    var usedParagraphs = [];
    
	#foreach ($paragraph in $paragraphs)
		$(".$paragraph.getId()-slider").slider({
			tooltip: "show"
		});
		usedParagraphs.push("$paragraph.getId()");
		
		var marks = [];
		var i=0;
		
		/*Show the correct evaluation*/
		#foreach($mark in $paragraph.getEvaluation())
		   marks.push($mark);
		#end
		
		#foreach ($eval in $paragraph.getOptions().entrySet())
		
			#if($eval.getValue().isFourMarks())
				if (marks[i] === 100) {
					$("#$paragraph.getId()$eval.getKey().charAt(0)-100").attr('checked', true);
				} else if (marks[i] === 66) {
					$("#$paragraph.getId()$eval.getKey().charAt(0)-66").attr('checked', true);					
				} else if (marks[i] === 33) {
					$("#$paragraph.getId()$eval.getKey().charAt(0)-33").attr('checked', true);
				} else if (marks[i] === 0) {
					$("#$paragraph.getId()$eval.getKey().charAt(0)-0").attr('checked', true);
				}
			#end
			
			#if($eval.getValue().isZeroToHundred())
				$("#$paragraph.getId()$eval.getKey().charAt(0)").val(marks[i].toString());
				$("#$paragraph.getId()$eval.getKey().charAt(0)").slider('setValue', marks[i].toString());
			#end
			i++;
		#end
	#end
	
	if (usedParagraphs[usedParagraphs.length - 1] === "07overall") {
		$(".07overall-slider").on('slide', function(){
			var text = $(".07overall-evalTitle").text().split(":")[0] + ": ";
			var points = $(this).val();
			if (points < 50) {
				text += "F";
			} else if (points < 60) {
				text += "E";
			} else if (points < 70) {
				text += "D";
			} else if (points < 80) {
				text += "C";
			} else if (points < 90) {
				text += "B";
			} else {
				text += "A";
			}
			
			$(".07overall-evalTitle").text(text);
		});
	}
});