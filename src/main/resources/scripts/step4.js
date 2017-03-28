$(document).ready(function(){
	$('div.row').css("border-top", "1px solid");
	//$('div.row:first-child').css("border-top", "0px");
	var form = $('form');
	$('#btn-save').click(function(){
		form.attr("action", "/review");
		form.attr("method", "POST");
		form.submit();
	});
	$('#btn-next').click(function(){
		form.attr("action", '/save');
		form.submit();
	});
	$('#btn-back').click(function(){
		form.attr("action", '/questions');
		form.attr("method", "GET");
		form.submit();
	});
	
	#foreach ($paragraph in $paragraphs)
	#if($paragraph.getText().isEmpty())
	#else
		$('#$paragraph.getId()-textarea').val('$paragraph.getText()');		
	#end
	
	$('#$paragraph.getId()-hidden').hide();
	
	$('.$paragraph.getId()-sentence').click(function(){
		if ($('#$paragraph.getId()-textarea').val()) {
			$('#$paragraph.getId()-textarea').val($('#$paragraph.getId()-textarea').val() + " " + $(this).text());
		} else {
			$('#$paragraph.getId()-textarea').val($(this).text());
		}
		if($('#$paragraph.getId()-hidden').val) {
			$('#$paragraph.getId()-hidden').val($('#$paragraph.getId()-hidden').val() + " " + $(this).attr('id'));
		} else {
			$('#$paragraph.getId()-hidden').val($(this).attr('id'));
		}
		$(this).remove();
	});
	#end
});