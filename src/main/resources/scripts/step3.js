var submitForm = function() {
	#foreach ($paragraph in $paragraphs)
	$('#$paragraph.getId()-textarea').prop("disabled", false);
	#end
	$("#mainForm").submit();
}

$(document).ready(function(){
	
	//				Define all actions on buttons
	var form = $("#form");
	

	$('#btn-save').click(function(){
		form.attr("action", "/review");
		form.attr("method", "POST");
		submitForm();
	});
	$('#btn-next').click(function(){
		form.attr("action", '/save');
		form.attr("method", "POST");
		submitForm();
	});
	$('#btn-back').click(function(){
		form.attr("action", '/questions');
		form.attr("method", "POST");
		submitForm();
	});
	
    $( "#accordion" ).accordion({
	    autoHeight: false
	});	
    
	//					Prevent user from submitting by enter
	$(window).keydown(function(event){
	    if(event.keyCode == 13) {
	      event.preventDefault();
	      return false;
	    }
	});
});