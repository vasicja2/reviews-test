$(document).ready(function(){
	#foreach ($paragraph in $paragraphs)
		$(".$paragraph.getId()-slider").slider();
	#end
});