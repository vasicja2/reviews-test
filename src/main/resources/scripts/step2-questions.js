var submitForm = function() {
	$("#mainForm").submit();
}

$(document).ready(function(){
	var form = $("#mainForm");
	$("#btn_submit").click(function(){
		form.submit();
	});
	
    $( "#accordion" ).accordion({
	    autoHeight: false
	});
	
	#foreach ($paragraph in $paragraphs)
		$(".$paragraph.getId()-slider").slider({
			tooltip: "show"
		});
	#end
});