var k;
var l = 0;

var getClusterByID = function(clusters, ID) {
	for(var i=0; i<clusters.length; i++) {
		if (clusters[i].cID === ID) {
			return clusters[i];
		}
	}
	
	return null;
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
		clusters${entry.getKey().getId()}[k].cOpeningSentences.push({
			sID : l,
			sChosen : false,
			sDistance : $sentence.getDistance(),
			sValue : "$sentence.getValue()"
		});
		l += 1;
		#end
		
		#foreach($sentence in $cluster.getOtherSentences())
		clusters${entry.getKey().getId()}[k].cOtherSentences.push({
			sID : l,
			sChosen : false,
			sDistance : $sentence.getDistance(),
			sValue : "$sentence.getValue()"
		});
		l += 1;
		#end
	k += 1;	
	#end
	
#end

