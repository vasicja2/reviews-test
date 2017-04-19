var op, ot, k;
//var finalGrade = ${finalGrade};

var getClusterByID = function(clusters, ID) {
	for(var i=0; i<clusters.length; i++) {
		if (clusters[i].cID === ID) {
			return clusters[i];
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
	op = 0;
	ot = 0;
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
			sID : op,
			sRecommended : false,
			sChosen : false,
			sDistance : $sentence.getDistance(),
			sValue : value
		});
		op++;
		#end
		
		#foreach($sentence in $cluster.getOtherSentences())
		var value = "$sentence.getValue()";
		if (value.indexOf('$') != -1) {
			value = completeGrade(value, $finalGrade);
		}
		clusters${entry.getKey().getId()}[k].cOtherSentences.push({
			sID : ot,
			sRecommended : false,
			sChosen : false,
			sDistance : $sentence.getDistance(),
			sValue : value
		});
		ot++;
		#end
	k++;	
	#end
	
#end

