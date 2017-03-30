//			Save all clusters
var i;
var j = 0;

#foreach($entry in $recommended.entrySet())
var clusters${entry.getKey().getId()} = [];
i = 0;
	
	#foreach($cluster in $entry.getValue())
	clusters${entry.getKey().getId()}.push({
		cPosition : $cluster.getCentroidPosition(),
		cDistance : $cluster.getCurrentDistance(),
		cOpeningSentences : [],
		cOtherSentences : []
	});
	
		#foreach($sentence in $cluster.getOpeningSentences())
		clusters${entry.getKey().getId()}[i].cOpeningSentences.push({
			sID : j,
			sChosen : false,
			sDistance : $sentence.getDistance(),
			sValue : "$sentence.getValue()"
		});
		j += 1;
		#end
		
		#foreach($sentence in $cluster.getOtherSentences())
		clusters${entry.getKey().getId()}[i].cOtherSentences.push({
			sID : j,
			sChosen : false,
			sDistance : $sentence.getDistance(),
			sValue : "$sentence.getValue()"
		});
		j += 1;
		#end
	i += 1;	
	#end
	
#end

