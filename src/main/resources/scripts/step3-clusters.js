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

var getSentencePosition = function(cluster, senID, type) {
	if (type.endsWith('p')) {
		for (var i=0; i<cluster.cOpeningSentences.length; i++) {
			if (cluster.cOpeningSentences[i].sID == senID)
				return i;
		}
	} else {
		for (var i=0; i<cluster.cOtherSentences.length; i++) {
			if (cluster.cOtherSentences[i].sID == senID)
				return i;
		}		
	}
	
	return -1;
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
		cPosition : $cluster.getCentroidPosition(),
		cDistance : $cluster.getCurrentDistance(),
		cActive : false,
		cOpeningSentences : [],
		cOtherSentences : []
	});
	
		#foreach($sentence in $cluster.getOpeningSentences())
		var value = "$sentence.getValue()";
		if (value.indexOf('$') != -1) {
			value = completeGrade(value, $finalGrade);
		}
		clusters${entry.getKey().getId()}[k].cOpeningSentences.push({
			sID : $sentence.getSentenceID(),
			sRecommended : false,
			sChosen : false,
			sDistance : $sentence.getDistance(),
			sValue : value
		});
		#end
		
		#foreach($sentence in $cluster.getOtherSentences())
		var value = "$sentence.getValue()";
		if (value.indexOf('$') != -1) {
			value = completeGrade(value, $finalGrade);
		}
		clusters${entry.getKey().getId()}[k].cOtherSentences.push({
			sID : $sentence.getSentenceID(),
			sRecommended : false,
			sChosen : false,
			sDistance : $sentence.getDistance(),
			sValue : value
		});
		#end
	k++;	
	#end
	
#end

