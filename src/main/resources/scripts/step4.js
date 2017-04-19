var submitForm = function() {
	#foreach ($paragraph in $paragraphs)
	$('#$paragraph.getId()-textarea').prop("disabled", false);
	#end
	$("#mainForm").submit();
}

$(document).ready(function() {
	//				Define all actions on buttons
	var form = $("#mainForm");
	
	//				Hide all form elements
	$("#reviewTitle").hide();
	$("#reviewer").hide();
	
	$("#title-editable").live('input', function(){
		$("#reviewTitle").val($(this).text());
	});
	$("#reviewer-editable").live('input', function(){
		$("#reviewer").val($(this).text());
	});	
	
	#foreach($paragraph int $paragraphs)
	$("#$paragraph.getId()-textarea").hide();
	$("#$paragraph.getId()-textarea").val($("#$paragraph.getId()-editable").text());
	
	$("#$paragraph.getId()-editable").live('input', function(){
		$("#$paragraph.getId()-textarea").val($(this).text());
	});	
	#end
	
	$("#btn_back").click(function() {
		form.attr("action", '/review');
		form.attr("method", "POST");
		submitForm();		
	});
	
	//				Download button handler
	$("#btn_download").click(function(){
		form.attr("action", '/review.tex');
		form.attr("method", "GET");
		//form.attr("target", "_blank");
		submitForm();
	});
});