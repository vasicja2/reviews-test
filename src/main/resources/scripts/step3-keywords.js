//			Save all keywords
var keywords = [];
#foreach($type in $kwtypes)
keywords.push({ 
	ID : "$type.getId()",
	values : []
});
#end

var category = 0;
#foreach($kwEntry in $keywords.entrySet())
category = parseInt("$kwEntry.getKey().getId().substring(0,1)");
category--;
#foreach($val in $kwEntry.getValue())
keywords[category].values.push("$val.getDefaultVal()");
#end
#end

//			Show keywords for the paragraph
var addKeywords = function() {
	var div = "<div id=KWchoice align=\"center\">";
	var colour = {
			R: "0",
			G: "34",
			B: "102",
			opacity: "1"
		};
	
	for(var i=0; i<keywords.length; i++) {
		for (var j=0; j<keywords[i].values.length; j++) {
			div += "<div class=\"keyword\" style=\"display:inline-block;" +
					"height:10%;" +
					"width:8%;" +
					"color:white;" +
					"background:rgba(" + colour.R + ", " + colour.G + ", " + colour.B + ", " + colour.opacity + ");" +
					//"border-radius:30%;" +
					"padding:0.6%;" +
					"cursor:pointer;" +
					"margin-left:2%" +
					"\">";
			div += keywords[i].values[j];
			div += "</div>";
		}
	}
	div += "</div>";
	
	#foreach($paragraph in $paragraphs)
	$("#$paragraph.getId()-row").prepend(div);
	#end
}

$(document).ready(function(){
	
	addKeywords();
	
	var lastFocus;
	$("input[type=text]").live('focus', function(){
		lastFocus = $(this);
	});
	
	$(".keyword").click(function(){
		var newText = $(lastFocus).val() + $(this).text();
		lastFocus.val(newText);
	});
});