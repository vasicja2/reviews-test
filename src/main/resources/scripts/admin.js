$(document).ready(function(){
	$("#accordion").accordion({
	    autoHeight: false,
	    collapsible: true
	});
	
	
	#foreach($paragraph in $paragraphs)
	//$("#$paragraph.getId()-hidden").hide();
	/*$(".$paragraph.getId()-newPar").live('click', function(){
		$(this).remove();
		$("#$paragraph.getId()-hidden").val("");
		$(".$paragraph.getId()-newPar").each(function(){
			$("#$paragraph.getId()-hidden").val($("#$paragraph.getId()-hidden").val() + $(this).text() + "\n");
		});		
	});*/
	
	$("#$paragraph.getId()-save").click(function(){
		var text = "-1;" + $("#$paragraph.getId()-pos").val() + ";";
		#foreach($eval in $paragraph.getEvaluation().entrySet())
		text += $("#$paragraph.getId()-attr$eval.getKey().substring(0,1)").val() + ";";
		#end
		text += $("#$paragraph.getId()-val").val();
		
		$("#$paragraph.getId()-subrow3").append("<p class=$paragraph.getId()-new>"+text+"</p>");
		text += "\n";
		$("#$paragraph.getId()-hidden").val($("#$paragraph.getId()-hidden").val() + text);
		
		$("#$paragraph.getId()-val").val("");
	});
	#end
});	