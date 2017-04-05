var parseText = function(text, parID) {
	var result = [];
	var prefix = "<p class="+parID+"-sortable id=\"x\" style=display:inline>";
	var postfix = " </p>";
	var buffer = "";
	
	for (var i=0; i<text.length; i++) {
		buffer += text[i];
		if (i == text.length-1) {
			result.push(prefix + buffer + postfix);
			buffer = "";
			i++;
		} else if (text[i] == '.' && i < text.length-2) {
			if (text[i+1] == ' ' && text[i+2] <= 'Z' && text[i+2] >= 'A') {
				result.push(prefix + buffer + postfix);
				buffer = "";
				i++;				
			}
		}
	}
	
	return result;
}

var getColour = function(ID) {
	if (ID.endsWith("op"))
		return 'red';
	return 'purple';
}

var dialogToComplete = function(sentence, parID) {
	var result_tmp = "";
	var buffer = "";
	var parts = 0;
	
	for(var i=1; i<sentence.length; i++) {
		if ((sentence[i-1] === '_') && (sentence[i] === '_')) {
			result_tmp += "<p id=\"" + parts.toString() + "-part\" style=display:inline>" + buffer + "</p>";
			result_tmp += "<input type=text name=\"" + parts.toString() + "-input\" id=\"" 
					+ parts.toString() + "-input\" style=\"display:inline\" class=\"text ui-widget-content ui-corner-all\">";
			i++;
			parts++;
			buffer = "";
		} else {
			buffer += sentence[i-1];			
		}
	}
	buffer += sentence[sentence.length-1];	
	result_tmp += "<p id=\"" + parts.toString() + "-part\" style=display:inline>" + buffer + "</p>";
	

	var result = "<div class=\"dialog-compl-" + parID + "\" id=\"" + parts.toString() + "\"title=\"Complete the sentence\">" +
			"<form>";
	result += result_tmp;
	result += "</form>" +
			"</div>";
	
	return result;
}

var dialogToEdit = function(sentence, parID) {
	var result = "<div class=\"dialog-edit-" + parID + "\" title=\"Edit the sentence\">" +
	"<form>";
	result += "<input type=text id=\"" + parID + "-editSentence\" value=\"" + sentence + "\" class=\"text ui-widget-content ui-corner-all\">";
	result += "</form>" +
			"</div>";
	
	return result;
}

#foreach ($paragraph in $paragraphs)
var $paragraph.getId().substring(2,4)_addSentence = function(value, senID) {
	if ($('#$paragraph.getId()-textarea').val()) {
		$('#$paragraph.getId()-textarea').val($('#$paragraph.getId()-textarea').val() + " " + value);
	} else {
		$('#$paragraph.getId()-textarea').val(value);
	}
	
	var toAdd = "<p class=\"$paragraph.getId()-sortable\" style=\"color:";
	toAdd += getColour(senID);
	toAdd += ";display:inline\" id=" + senID + ">" + value + " </p>";
	
	$("#$paragraph.getId()-order").append(toAdd);
}
#end

$(document).ready(function(){	
	//					Allow/disable manual edit
	$("#manual-edit").on("change", function(){
		if ($(this).prop("checked")) {
			#foreach ($paragraph in $paragraphs)
			$("#$paragraph.getId()-choice").hide();
			$("#$paragraph.getId()-orderWhole").hide();
			$('#$paragraph.getId()-textarea').prop("disabled", false);
			#end
		} else {
			#foreach ($paragraph in $paragraphs)
			$("#$paragraph.getId()-order").empty();
			var sentencesToAdd = parseText($('#$paragraph.getId()-textarea').val(), "$paragraph.getId()");	
			for (var i=0; i<sentencesToAdd.length; i++) {
				$("#$paragraph.getId()-order").append(sentencesToAdd[i]);
			}
			
			$("#$paragraph.getId()-choice").show();
			$("#$paragraph.getId()-orderWhole").show();
			$('#$paragraph.getId()-textarea').prop("disabled", true);
			#end
		}
	});
	
	//					For each paragraph	
	//					Initialize
	#foreach ($paragraph in $paragraphs)
	#if($paragraph.getText().isEmpty())
	#else
		//				Get the text if there's any
		$('#$paragraph.getId()-textarea').val("$paragraph.getText()");	
		//				Put the sentences into the sorting div
		var sentencesToAdd = parseText("$paragraph.getText()", "$paragraph.getId()");	
		for (var i=0; i<sentencesToAdd.length; i++) {
			$("#$paragraph.getId()-order").append(sentencesToAdd[i]);
		}
	#end
	
	$('#$paragraph.getId()-textarea').prop("disabled", true);
	$("#$paragraph.getId()-order").sortable('enable');
	
	//					After the order is changed, write the sentences in the new order to the textarea.
	$("#$paragraph.getId()-order").sortable({
		stop: function( event, ui ) {
			var text = "";
			var ids = "";
			$(".$paragraph.getId()-sortable").each(function(){
				text += $(this).text();
				if ($(this).attr('id') !== "x") {
					ids += $(this).attr('id') + " ";
				}
			});
			$('#$paragraph.getId()-textarea').val(text);
		}		
	});	

	//					Edit or remove the sentence from sorting div on click
	$(".$paragraph.getId()-sortable").live("click", function() {
		var senID = $(this).attr('id');
		var thisPar = $(this);
		var value = $(this).text();
		
		$("#$paragraph.getId()-row").append(dialogToEdit($(this).text(), "$paragraph.getId()"));
		$(".dialog-edit-$paragraph.getId()").dialog({
			buttons: {
				"Done" : function() {
					thisPar.text($("#$paragraph.getId()-editSentence").val());
					
					var text = "";
					$(".$paragraph.getId()-sortable").each(function(){
						text += $(this).text();
					});
					$('#$paragraph.getId()-textarea').val(text);
					
					$(".dialog-edit-$paragraph.getId()").remove();
				},
				"Remove" : function() {
					var toAdd = "<p class=\"$paragraph.getId()-sentence\" id=\"" + senID + "\">";
					toAdd += value + "</p>";
					$("#$paragraph.getId()-choice").append(toAdd);
					thisPar.remove();
					
					var text = "";
					var ids = "";
					$(".$paragraph.getId()-sortable").each(function(){
						text += $(this).text();
						if ($(this).attr('id') !== "x") {
							ids += $(this).attr('id') + " ";
						}
					});
					$('#$paragraph.getId()-textarea').val(text);
					$(".dialog-edit-$paragraph.getId()").remove();
				}
			},
			close : function() {
				$(".dialog-edit-$paragraph.getId()").remove();
			}
		});
	});
	
	//					On click move the sentence to the sorting div and the textarea 
	//					and remember which type of sentence the user chose
	$('.$paragraph.getId()-sentence').live("click", function(){
		var value = $(this).text();
		var senID = $(this).attr('id');
		
		if(value.includes("__")) {
			$("#$paragraph.getId()-row").append(dialogToComplete(value, "$paragraph.getId()"));
			$(".dialog-compl-$paragraph.getId()").dialog({
				buttons: {
					"Done" : function(){
						var text = "";
						for(var i=0; i<parseInt($(this).attr('id')); i++) {
							text += $("#"+i.toString()+"-part").text();
							text += $("#"+i.toString()+"-input").val();
						}
						text += $("#"+$(this).attr('id')+"-part").text();
						$paragraph.getId().substring(2,4)_addSentence(text, senID);
						$(".dialog-compl-$paragraph.getId()").remove();
					},
					"Close" : function(){
						$(".dialog-compl-$paragraph.getId()").dialog('close');
					}
				},
			close : function () {
				var toAdd = "<p class=\"$paragraph.getId()-sentence\" id=\"" + senID + "\">";
				toAdd += value + "</p>";
				$("#$paragraph.getId()-choice").append(toAdd);
				$(".dialog-compl-$paragraph.getId()").remove();
			}
			});
		} else {
			$paragraph.getId().substring(2,4)_addSentence(value, senID);
		}
		
		$(this).remove();
	});
	#end
});