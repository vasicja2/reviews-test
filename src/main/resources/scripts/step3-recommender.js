var TO_RECOMMEND = 4;
var MAX_DISTANCE = 50;

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

var findFirstAvailable = function(cluster) {
	
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
		
		if (clusters[i].cDistance > MAX_DISTANCE) {
			break;
		}
		
		var s1 = null, s2 = null, toAdd = "";
		
		for (var j=0; j<clusters[i].cOpeningSentences.length; j++) {
			if (!clusters[i].cOpeningSentences[j].sChosen) {
				s1 = clusters[i].cOpeningSentences[j];
				break;
			}
		}
		
		for (var j=0; j<clusters[i].cOtherSentences.length; j++) {
			if (!clusters[i].cOtherSentences[j].sChosen) {
				s2 = clusters[i].cOtherSentences[j];
				break;
			}
		}
		
		if (currentPos === 0) {
			if (s1 === null) {
				lastCluster = Math.min(clusters.length, lastCluster+1);
				continue;
			}
			
			
			toAdd = "<p class=\"" + parID + "-sentence\" id=\"" + parID + "." + clusters[i].cID + "." + s1.sID + "op\" " +
					"style=cursor:pointer " +
					"title=\"Click to append to the text, right-click to remove or see alternatives.\">";
			toAdd += s1.sValue;
			toAdd += "<\p>";
			
			sentenceDiv.append(toAdd);
			clusters[i].cOpeningSentences[0].sRecommended = true;
			clusters[i].cActive = true;
		} else {			
			if (s1 === null && s2 === null) {
				lastCluster = Math.min(clusters.length, lastCluster+1);
				continue;
			} else if (s1 === null) {
				toAdd = "<p class=\"" + parID + "-sentence\" id=\"" + parID + "." + clusters[i].cID + "." + s2.sID + "ot\" " +
						"style=cursor:pointer " +
						"title=\"Click to append to the text, right-click to remove or see alternatives.\">";
				toAdd += s2.sValue;
				toAdd += "<\p>";
				sentenceDiv.append(toAdd);
				s2.sRecommended = true;
			} else if (s2 === null) {
				toAdd = "<p class=\"" + parID + "-sentence\" id=\"" + parID + "." + clusters[i].cID + "." + s1.sID + "op\" " +
						"style=cursor:pointer " +
						"title=\"Click to append to the text, right-click to remove or see alternatives.\">";
				toAdd += s1.sValue;
				toAdd += "<\p>";	
				sentenceDiv.append(toAdd);	
				s1.sRecommended = true;					
			} else if (s1.sDistance < s2.sDistance) {
				toAdd = "<p class=\"" + parID + "-sentence\" id=\"" + parID + "." + clusters[i].cID + "." + s1.sID + "op\" " +
						"style=cursor:pointer " +
						"title=\"Click to append to the text, right-click to remove or see alternatives.\">";
				toAdd += s1.sValue;
				toAdd += "<\p>";	
				sentenceDiv.append(toAdd);	
				s1.sRecommended = true;			
			} else {
				toAdd = "<p class=\"" + parID + "-sentence\" id=\"" + parID + "." + clusters[i].cID + "." + s2.sID + "ot\" " +
						"style=cursor:pointer " +
						"title=\"Click to append to the text, right-click to remove or see alternatives.\">";
				toAdd += s2.sValue;
				toAdd += "<\p>";		
				sentenceDiv.append(toAdd);	
				s2.sRecommended = true;	
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
					var remove = true;
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
								cluster.cOpeningSentences[newPos].sRecommended = true;
							} else {
								newVal = cluster.cOtherSentences[newPos].sValue;
								cluster.cOtherSentences[newPos].sRecommended = true;
							}
							
							
							var newPar = "<p class=\"$paragraph.getId()-sentence\" id=\"" + newID + "\" " +
									"style=cursor:pointer " +
									"title=\"Click to append to the text, right-click to remove or see alternatives.\">";
							newPar += newVal;
							newPar += "</p>";
							$("#$paragraph.getId()-choice").append(newPar);
							remove = false;
						} else {
							var newID = $(this).attr('id');
							var clusterID = parseInt(newID.split(".")[1]);
							var newType = newID.split(".")[2];
							var newPos = parseInt(newID.split(".")[2].split("o")[0]);
							var cluster = getClusterByID(clusters${paragraph.getId()}, clusterID);
							
							if (newType.endsWith('p')) {
								cluster.cOpeningSentences[newPos].sRecommended = false;
							} else {
								cluster.cOtherSentences[newPos].sRecommended = false;								
							}
						}
					});
					
					if (remove) {
						var clusterID = parseInt(sentID.split(".")[1]);
						var cluster = getClusterByID(clusters${paragraph.getId()}, clusterID);
						
						/*Change the distance and re-sort the clusters*/
						cluster.cDistance += MAX_DISTANCE+1;		
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
					
					$(".dialog-alter-$paragraph.getId()").remove();
				},
				"Cancel" : function(){
					$(".dialog-alter-$paragraph.getId()").dialog('close');
				},
				"Remove" : function(){
					var clusterID = parseInt(sentID.split(".")[1]);
					var cluster = getClusterByID(clusters${paragraph.getId()}, clusterID);
					
					/*Change the distance and re-sort the clusters*/
					cluster.cDistance += MAX_DISTANCE+1;			
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
					
					$(".dialog-alter-$paragraph.getId()").remove();
				}
			},
			close : function() {
				var toAdd = "<p class=\"$paragraph.getId()-sentence\" id=\"" + sentID + "\" " +
						"style=cursor:pointer " +
						"title=\"Click to append to the text, right-click to remove or see alternatives.\">";
				toAdd += value + "</p>";
				$("#$paragraph.getId()-choice").append(toAdd);
				$(".dialog-alter-$paragraph.getId()").remove();			
			},
		});
		
		$(this).remove();
	});
	
	$(".$paragraph.getId()-sentence").live('click', function(){
		var thisID = $(this).attr('id');
		
		if (thisID != 'x') {
			
			var clusterID = parseInt(thisID.split(".")[1]);
			var type = thisID.split(".")[2];
			var pos = parseInt(thisID.split(".")[2].split("o")[0]);
			var cluster = getClusterByID(clusters${paragraph.getId()}, clusterID);
			
			/*Mark the sentence as already chosen*/
			if (type.endsWith('p')) {
				cluster.cOpeningSentences[pos].sChosen = true;
			} else {
				cluster.cOtherSentences[pos].sChosen = true;	
			}
			
			/*Check if the cluster is still active*/
			var active = false;
			for (var i=0; i<cluster.cOpeningSentences.length; i++) {
				if (cluster.cOpeningSentences[i].sRecommended) {
					active = true;
					break;
				}
			}
			if (!active) {
				for (var i=0; i<cluster.cOtherSentences.length; i++) {
					if (cluster.cOtherSentences[i].sRecommended) {
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