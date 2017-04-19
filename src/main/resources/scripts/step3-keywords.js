//			Save all keywords
var keywords = [];
#foreach($type in $kwtypes)
keywords.push({ 
	ID : "$type.getId()",
	values : []
});
#end

var category = 0;
#foreach($kw in $keywords)
category = parseInt("$kw.getType().getId().substring(0,1)");
category--;
keywords[category].values.push("$kw.getValue()");
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
					"\"" +
					"title=\"Click to insert into the text at your current position. You can also use this when completing or editing a sentence separately.\">";
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
	var lastPosition;
	$("input[type=text]").live('focusout', function(){
		lastFocus = $(this);
		lastPosition = $(this).getCursorPosition();
	});
	
	$("textarea").live('focusout', function(){
		lastFocus = $(this);
		lastPosition = $(this).getCursorPosition();
	});
	
	$(".keyword").click(function(){
		var oldText = $(lastFocus).val();
		var newText = oldText.substr(0, lastPosition) + $(this).text() + oldText.substr(lastPosition, oldText.length-lastPosition);
		lastFocus.val(newText);
	});
});