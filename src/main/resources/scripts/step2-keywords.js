var divs = [];
var types = [];



$(document).ready(function(){
	#foreach($type in $kwtypes)
	types.push("$type.getId()");
	divs.push($("#$type.getId()-div"));
	#end
	
	for(var i=0; i<types.length; i++) {
		var tags = "#" + types[i] + "-tags";
		var div = "#" + types[i] + "-div";
		var next = "#" + types[i] + "-next";
		var prev = "#" + types[i] + "-prev";
		var name = types[i] + "-tags";
		
		$(tags).tagit({
			fieldName: name
		});
		
		if (i !== 0) {
			$(div).hide();
		}
		
		if (i === 0) {
			$(prev).remove();
		}
		
		if (i === types.length-1) {
			$(next).remove();
		}
	}
	
	var pos = 0;
	#foreach($type in $kwtypes)
	$("#$type.getId()-next").click(function(){
		pos = parseInt($(this).attr('id')[0]);
		pos--;
		
		divs[pos].hide();
		divs[pos+1].show();
		
		pos = 0;
	});
	
	$("#$type.getId()-prev").click(function(){
		pos = parseInt($(this).attr('id')[0]);
		pos--;
		
		divs[pos].hide();
		divs[pos-1].show();
		
		pos = 0;
	});
	#end
});