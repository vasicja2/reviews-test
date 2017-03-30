var TO_RECOMMEND = 4;

var countSentences = function(text){
	if (text.length === 0) return 0;
	var result = 0;
	
	for (var i=0; i<text.length; i++) {
		if ((text[i] === '.') && 
				(i < text.length-2) &&
				(text[i+1] === ' ') &&
				(text[i+2] <= 'Z') &&
				(text[i+2] >= 'A')) {
			result++;
		}
	}
	result++;
	
	return result;
}

var dialogToAlter = function(senID, clusters) {
	var parID = senID.split(".")[0];
	var clusterID = senID.split(".")[1];
	var sentenceOrder = senID.split(".")[2].split("o")[0];
	var sentenceType = senID.split(".")[2].split("o")[1];
	
	var result = "<div class=\"dialog-alter-" + parID + "\" title=\"Alternative formulaions:\">" +
	"<form>";
	result += "<fieldset>";
	var option = "";
	for (var i=0; i<clusters[clusterID].cOpeningSentences.length; i++) {
		var newID = parID + "." + clusterID + "." + i + "op";
		
		option = "<div class=\"form-check\">";
		option += "<label class=\"form-check-label\"><input class=\"alterRadio\" type=\"radio\" name=\"" + newID + "\" id=\"" + newID + "\"";
		if (newID === senID) {
			option +=" checked";
		}
		option += "/>" + clusters[clusterID].cOpeningSentences[i].sValue;
		option += "</label>";
		option += "</div>";
		
		result += option;
	}
	
	for (var i=0; i<clusters[clusterID].cOtherSentences.length; i++) {
		var newID = parID + "." + clusterID + "." + i + "ot";
		
		option = "<div class=\"form-check\">";
		option += "<label class=\"form-check-label\"><input class=\"alterRadio\" type=\"radio\" name=\"" + newID + "\" id=\"" + newID + "\"";
		if (newID === senID) {
			option +=" checked";
		}
		option += "/>" + clusters[clusterID].cOtherSentences[i].sValue;
		option += "</label>";
		option += "</div>";
		
		result += option;
	}
	result += "</fieldset>";
	result += "</form>";
	result += "</div>";
	
	return result;
}

var recommend = function(parID, clusters, maxSentences) {
	var sentenceDiv = $("#"+parID+"-choice");
	var currentPos = countSentences($("#"+parID+"-textarea").val());
	var lastCluster = Math.min(clusters.length, maxSentences);
	
	for(var i=0; i<lastCluster; i++) {
		if (currentPos === 0) {
			if (clusters[i].cOpeningSentences.length === 0) {
				lastCluster = Math.min(clusters.length, lastCluster+1);
				continue;
			}
			
			var toAdd = "<p class=\"" + parID + "-sentence\" id=\"" + parID + "." + i + ".0op\">";
			toAdd += clusters[i].cOpeningSentences[0].sValue;
			toAdd += "<\p>";
			
			sentenceDiv.append(toAdd);
		} else {
			var s1, s2, toAdd;
			
			if (clusters[i].cOpeningSentences.length === 0) {
				s1 = null;
			} else {
				s1 = clusters[i].cOpeningSentences[0];
			}
			
			if (clusters[i].cOtherSentences.length === 0) {
				s2 = null;
			} else {
				s2 = clusters[i].cOtherSentences[0];
			}
			
			if (s1 === null && s2 === null) {
				lastCluster = Math.min(clusters.length, lastCluster+1);
				continue;
			} else if (s1 === null) {
				toAdd = "<p class=\"" + parID + "-sentence\" id=\"" + parID + "." + i + ".0ot\">";
				toAdd += s2.sValue;
				toAdd += "<\p>";
				sentenceDiv.append(toAdd);
			} else if (s2 === null) {
				toAdd = "<p class=\"" + parID + "-sentence\" id=\"" + parID + "." + i + ".0op\">";
				toAdd += s1.sValue;
				toAdd += "<\p>";	
				sentenceDiv.append(toAdd);			
			} else if (s1.sDistance < s2.sDistance) {
				toAdd = "<p class=\"" + parID + "-sentence\" id=\"" + parID + "." + i + ".0op\">";
				toAdd += s1.sValue;
				toAdd += "<\p>";	
				sentenceDiv.append(toAdd);				
			} else {
				toAdd = "<p class=\"" + parID + "-sentence\" id=\"" + parID + "." + i + ".0ot\">";
				toAdd += s2.sValue;
				toAdd += "<\p>";		
				sentenceDiv.append(toAdd);		
			}
		}
	}
}

$(document).ready(function() {
	
	#foreach($paragraph in $paragraphs) 
	recommend("$paragraph.getId()", clusters${paragraph.getId()}, TO_RECOMMEND);
	
	$(".$paragraph.getId()-sentence").live('contextmenu', function(event){		
		event.preventDefault();
		var sentID = $(this).attr('id');
		var value = $(this).text();
		
		$("#$paragraph.getId()-row").append(dialogToAlter($(this).attr('id'), clusters${paragraph.getId()}));
		$(".dialog-alter-$paragraph.getId()").dialog({
			modal : true,
			closeOnEscape : true,
			width : 700,
			buttons : {
				"OK" : function(){
					$(".alterRadio").each(function(){
						var newPar = "";
						if ($(this).prop("checked")) {
							var newID = $(this).attr('id');
							var clusterID = newID.split(".")[1];
							var newType = newID.split(".")[2];
							var newPos = parseInt(newID.split(".")[2].split("o")[0]);
							var newVal;
							if (newType.endsWith('p')) {
								newVal = clusters${paragraph.getId()}[clusterID].cOpeningSentences[newPos].sValue;
							} else {
								newVal = clusters${paragraph.getId()}[clusterID].cOtherSentences[newPos].sValue;								
							}
							
							
							newPar = "<p class=\"$paragraph.getId()-sentence\" id=\"" + newID + "\">";
							newPar += newVal;
							newPar += "</p>";
						}
						$("#$paragraph.getId()-choice").append(newPar);
					});
					$(".dialog-alter-$paragraph.getId()").remove();
				},
				"Cancel" : function(){
					$(".dialog-alter-$paragraph.getId()").dialog('close');
				}
			},
			close : function() {
				var toAdd = "<p class=\"$paragraph.getId()-sentence\" id=\"" + sentID + "\">";
				toAdd += value + "</p>";
				$("#$paragraph.getId()-choice").append(toAdd);
				$(".dialog-alter-$paragraph.getId()").remove();			
			}
		});
		
		$(this).remove();
	});
	#end
	
});