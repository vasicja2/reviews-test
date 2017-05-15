var TO_RECOMMEND = 5;

var countSentences = function(text){
	if (text.length === 0) return 0;
	var result = 0;
	
	for (var i=0; i<text.length; i++) {
		if ((text[i] === '.') && 
				(i < text.length-2) &&
				(text[i+1] === ' ') &&
				(((text[i+2] <= 'Z') &&
				(text[i+2] >= 'A')) ||
				((text[i+2] <= '9') && 
				(text[i+2] >= '0')))) {
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
	for (var i=0; i<cluster.cSentences.length; i++) {
		var newID = parID + "." + clusterID + "." + cluster.cSentences[i].sID;
		
		option = "<div class=\"form-check\">";
		option += "<label class=\"form-check-label\"><input class=\"alterRadio\" type=\"checkbox\" name=\"" + newID + "\" id=\"" + newID + "\"";
		if (newID === senID) {
			option +=" checked";
		}
		option += "/>" + cluster.cSentences[i].sValue;
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
		/*The cluster is active - skip it*/
		if (clusters[i].cActive) {
			lastCluster = Math.min(clusters.length, lastCluster+1);
			continue;
		}
		
		var s = null, toAdd = "";
		
		/*Find the closest sentence to be recommended*/
		for (var j=0; j<clusters[i].cSentences.length; j++) {
			if (currentPos === 0) {
				if(!clusters[i].cSentences[j].sChosen && clusters[i].cSentences[j].sPosition === 1) {
					s = clusters[i].cSentences[j];
					break;
				}
			} else {
				if(!clusters[i].cSentences[j].sChosen) {
					s = clusters[i].cSentences[j];
					break;
				}
			}
		}
		
		/*If no sentence is available, skip the cluster*/
		if (s === null) {
			lastCluster = Math.min(clusters.length, lastCluster+1);
			continue;
		}
		
		toAdd = "<p class=\"" + parID + "-sentence nohelp\" id=\"" + parID + "." + clusters[i].cID + "." + s.sID + "\" " +
				"style=cursor:pointer>";
		toAdd += s.sValue;
		toAdd += "<\p>";
		
		sentenceDiv.append(toAdd);
		s.sRecommended = true;
		clusters[i].cActive = true;
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
							var newPos = parseInt(newID.split(".")[2]);
							var cluster = getClusterByID(clusters${paragraph.getId()}, clusterID);
							var newSentence = getSentenceByID(cluster, newPos);
							var newVal = newSentence.sValue;
							newSentence.sRecommended = true;							
							
							var newPar = "<p class=\"$paragraph.getId()-sentence nohelp\" id=\"" + newID + "\" " +
									"style=cursor:pointer>";
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
							var newSentence = getSentenceByID(cluster, newPos);
							newSentence.sRecommended = false;
						}
					});
					
					if (remove) {
						var clusterID = parseInt(sentID.split(".")[1]);
						var cluster = getClusterByID(clusters${paragraph.getId()}, clusterID);
						
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
					
					$(".dialog-alter-$paragraph.getId()").remove();
				},
				"Cancel" : function(){
					$(".dialog-alter-$paragraph.getId()").dialog('close');
				},
				"Remove" : function(){
					var clusterID = parseInt(sentID.split(".")[1]);
					var cluster = getClusterByID(clusters${paragraph.getId()}, clusterID);
					
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
					
					$(".dialog-alter-$paragraph.getId()").remove();
				}
			},
			close : function() {
				var toAdd = "<p class=\"$paragraph.getId()-sentence nohelp\" id=\"" + sentID + "\" " +
						"style=cursor:pointer>";
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
			var sID = parseInt(thisID.split(".")[2].split("o")[0]);
			var cluster = getClusterByID(clusters${paragraph.getId()}, clusterID);
			var sentence = getSentenceByID(cluster, sID);			
			
			/*Mark the sentence as chosen*/
			sentence.sChosen = true;
			sentence.sRecommended = false;
			
			/*Check if the cluster is still active*/
			var active = false;
			for (var i=0; i<cluster.cSentences.length; i++) {
				if (cluster.cSentences[i].sRecommended) {
					active = true;
					break;
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
	
	$("#$paragraph.getId()-alterBtn").on('click', function(event) {
		event.preventDefault();
		
		/*For each suggested sentence*/
		$(".$paragraph.getId()-sentence").each(function(){
			var thisID = $(this).attr('id');
			
			var clusterID = parseInt(thisID.split(".")[1]);
			var sID = parseInt(thisID.split(".")[2].split("o")[0]);
			var cluster = getClusterByID(clusters${paragraph.getId()}, clusterID);
			var sentence = getSentenceByID(cluster, sID);			
			
			/*Mark the sentence as chosen*/
			sentence.sChosen = true;
			sentence.sRecommended = false;
			
			/*Mark the cluster as inactive and increase its distance*/
			cluster.cActive = false;
			cluster.cDistance += 10;
			
			/*Remember not to suggest this sentence again if evaluation is changed*/
			$("#$paragraph.getId()-hidden").val($("#$paragraph.getId()-hidden").val() + thisID + ' ');
			
			/*Remove the sentence from the choice*/
			$(this).remove();
		});
		
		/*Recommend new sentences*/
		recommend("$paragraph.getId()", clusters${paragraph.getId()}, TO_RECOMMEND);
	});
	#end
	
});