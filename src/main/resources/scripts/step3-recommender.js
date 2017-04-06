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
	var clusterID = parseInt(senID.split(".")[1]);
	var cluster = getClusterByID(clusters, clusterID);
	
	var result = "<div class=\"dialog-alter-" + parID + "\" title=\"Alternative formulaions:\">" +
	"<form>";
	result += "<fieldset>";
	var option = "";
	for (var i=0; i<cluster.cOpeningSentences.length; i++) {
		var newID = parID + "." + clusterID + "." + i + "op";
		
		option = "<div class=\"form-check\">";
		option += "<label class=\"form-check-label\"><input class=\"alterRadio\" type=\"checkbox\" name=\"" + newID + "\" id=\"" + newID + "\"";
		if (newID === senID) {
			option +=" checked";
		}
		option += "/>" + cluster.cOpeningSentences[i].sValue;
		option += "</label>";
		option += "</div>";
		
		result += option;
	}
	
	for (var i=0; i<cluster.cOtherSentences.length; i++) {
		var newID = parID + "." + clusterID + "." + i + "ot";
		
		option = "<div class=\"form-check\">";
		option += "<label class=\"form-check-label\"><input class=\"alterRadio\" type=\"checkbox\" name=\"" + newID + "\" id=\"" + newID + "\"";
		if (newID === senID) {
			option +=" checked";
		}
		option += "/>" + cluster.cOtherSentences[i].sValue;
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
		if (clusters[i].cActive) {
			lastCluster = Math.min(clusters.length, lastCluster+1);
			continue;
		}
		
		if (currentPos === 0) {
			if (clusters[i].cOpeningSentences.length === 0) {
				lastCluster = Math.min(clusters.length, lastCluster+1);
				continue;
			}
			
			var toAdd = "<p class=\"" + parID + "-sentence\" id=\"" + parID + "." + clusters[i].cID + ".0op\">";
			toAdd += clusters[i].cOpeningSentences[0].sValue;
			toAdd += "<\p>";
			
			sentenceDiv.append(toAdd);
			clusters[i].cOpeningSentences[0].sChosen = true;
			clusters[i].cActive = true;
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
				toAdd = "<p class=\"" + parID + "-sentence\" id=\"" + parID + "." + clusters[i].cID + ".0ot\">";
				toAdd += s2.sValue;
				toAdd += "<\p>";
				sentenceDiv.append(toAdd);
				s2.sChosen = true;
			} else if (s2 === null) {
				toAdd = "<p class=\"" + parID + "-sentence\" id=\"" + parID + "." + clusters[i].cID + ".0op\">";
				toAdd += s1.sValue;
				toAdd += "<\p>";	
				sentenceDiv.append(toAdd);	
				s1.sChosen = true;					
			} else if (s1.sDistance < s2.sDistance) {
				toAdd = "<p class=\"" + parID + "-sentence\" id=\"" + parID + "." + clusters[i].cID + ".0op\">";
				toAdd += s1.sValue;
				toAdd += "<\p>";	
				sentenceDiv.append(toAdd);	
				s1.sChosen = true;			
			} else {
				toAdd = "<p class=\"" + parID + "-sentence\" id=\"" + parID + "." + clusters[i].cID + ".0ot\">";
				toAdd += s2.sValue;
				toAdd += "<\p>";		
				sentenceDiv.append(toAdd);	
				s2.sChosen = true;	
			}
			
			clusters[i].cActive = true;
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
			open : function() {
				$(".alertRadio").live('change', function() {
					var thisID = $(this).attr('id');
					alert("hey");
					
					$(".alertRadio").each(function(){
						if($(this).attr('id') !== thisID) {
							$(this).prop("checked", false);
						}
					});
				});
			},
			buttons : {
				"OK" : function(){
					$(".alterRadio").each(function(){
						if ($(this).prop("checked")) {
							var newID = $(this).attr('id');
							var clusterID = parseInt(newID.split(".")[1]);
							var newType = newID.split(".")[2];
							var newPos = parseInt(newID.split(".")[2].split("o")[0]);
							var cluster = getClusterByID(clusters${paragraph.getId()}, clusterID);
							var newVal;
							if (newType.endsWith('p')) {
								newVal = cluster.cOpeningSentences[newPos].sValue;
								cluster.cOpeningSentences[newPos].sChosen = true;
							} else {
								newVal = cluster.cOtherSentences[newPos].sValue;
								cluster.cOtherSentences[newPos].sChosen = true;
							}
							
							
							var newPar = "<p class=\"$paragraph.getId()-sentence\" id=\"" + newID + "\">";
							newPar += newVal;
							newPar += "</p>";
							$("#$paragraph.getId()-choice").append(newPar);
						} else {
							var newID = $(this).attr('id');
							var clusterID = parseInt(newID.split(".")[1]);
							var newType = newID.split(".")[2];
							var newPos = parseInt(newID.split(".")[2].split("o")[0]);
							var cluster = getClusterByID(clusters${paragraph.getId()}, clusterID);
							
							if (newType.endsWith('p')) {
								cluster.cOpeningSentences[newPos].sChosen = false;
							} else {
								cluster.cOtherSentences[newPos].sChosen = false;								
							}
						}
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
	
	$(".$paragraph.getId()-sentence").live('click', function(){
		var thisID = $(this).attr('id');
		
		if (thisID != 'x') {
			$("#hidden").val($("#hidden").val() + thisID + ' ');
			
			var clusterID = parseInt(thisID.split(".")[1]);
			var type = thisID.split(".")[2];
			var pos = parseInt(thisID.split(".")[2].split("o")[0]);
			var cluster = getClusterByID(clusters${paragraph.getId()}, clusterID);
			
			/*Remove the sentence from the cluster*/
			if (type.endsWith('p')) {
				cluster.cOpeningSentences.splice(pos, 1);
			} else {
				cluster.cOtherSentences.splice(pos, 1);			
			}
			
			/*Check if the cluster is still active*/
			var active = false;
			for (var i=0; i<cluster.cOpeningSentences.length; i++) {
				if (cluster.cOpeningSentences[i].sChosen) {
					active = true;
					break;
				}
			}
			if (!active) {
				for (var i=0; i<cluster.cOtherSentences.length; i++) {
					if (cluster.cOtherSentences[i].sChosen) {
						active = true;
						break;
					}
				}
			}
			cluster.cActive = active;
			
			/*Change the distance and re-sort the clusters*/
			cluster.cDistance += 10;		
			clusters${paragraph.getId()}.sort(function(a,b){
				return a.cDistance - b.cDistance;
			});
			
			/*see how many sentences are recommended*/
			var currentlyRecommended = 0;
			$(".$paragraph.getId()-sentence").each(function(){
				currentlyRecommended++;
			});		
			var toRecommend = TO_RECOMMEND - currentlyRecommended;
			
			/*fill the missing spaces*/
			recommend("$paragraph.getId()", clusters${paragraph.getId()}, toRecommend);
		}
	});
	#end
	
});