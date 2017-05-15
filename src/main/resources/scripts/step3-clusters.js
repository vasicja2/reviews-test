var k;
//var finalGrade = ${finalGrade};

var getClusterByID = function(clusters, ID) {
	for(var i=0; i<clusters.length; i++) {
		if (clusters[i].cID === ID) {
			return clusters[i];
		}
	}
	
	return null;
}

var getSentenceByID = function(cluster, ID) {
	for(var i=0; i<cluster.cSentences.length; i++) {
		if(cluster.cSentences[i].sID === ID) {
			return cluster.cSentences[i];
		}
	}
	
	return null;
}

var completeGrade = function(sentence, eval) {
	var mark = "";
	
	if (eval >= 90) {
		mark = "A";
	} else if (eval >= 80) {
		mark = "B";
	} else if (eval >= 70) {
		mark = "C"; 
	} else if (eval >= 60) {
		mark = "D";
	} else if (eval >= 50) {
		mark = "E";
	} else {
		mark = "F";
	}
	
	var result = sentence.replace("$g", mark);
	
	return result;
}

//				Save all clusters
#foreach($entry in $recommended.entrySet())
var clusters${entry.getKey().getId()} = [];
k = 0;	

	#foreach($cluster in $entry.getValue())
	clusters${entry.getKey().getId()}.push({
		cID : $cluster.getClusterId(),
		cDistance : $cluster.getCurrentDistance(),
		cActive : false,
		cSentences : []
	});
	
		#foreach($sentence in $cluster.getSentences())
		var value = "$sentence.getValue()";
		if (value.indexOf('$') != -1) {
			value = completeGrade(value, $finalGrade);
		}
		clusters${entry.getKey().getId()}[k].cSentences.push({
			sID : $sentence.getSentenceID(),
			sPosition : $sentence.getPosition(),
			sRecommended : false,
			sChosen : false,
			sDistance : $sentence.getDistance(),
			sValue : value
		});
		#end
		
	k++;	
	#end
	
#end

