var headIsActive = function(id) {
	if ($(id).attr('class') === "headActive")
		return true;
	return false;
}

$(document).ready(function(){
	$('#where').hide();
	
	var completed = $completed;
	var where = ${where.charAt(4)};
	where += 1;
	
	var topReached = Math.max(completed, where);
	
	switch(topReached) {
	case -1:
		break;
	case 0:
		$("#head1").attr('class', "headActive");
		break;
	case 1:
		$("#head1").attr('class', "headActive");
		$("#head2").attr('class', "headActive");
		break;
	case 2:
		$("#head1").attr('class', "headActive");
		$("#head2").attr('class', "headActive");
		$("#head3").attr('class', "headActive");
		break;
	case 3:
		$("#head1").attr('class', "headActive");
		$("#head2").attr('class', "headActive");
		$("#head3").attr('class', "headActive");
		$("#head4").attr('class', "headActive");
		break;		
	default:
		$("#head1").attr('class', "headActive");
		$("#head2").attr('class', "headActive");
		$("#head3").attr('class', "headActive");
		$("#head4").attr('class', "headActive");
		$("#head5").attr('class', "headActive");
	}
	
	if (completed >= 0) 
		$("#head"+where.toString()).attr('class', "headCurrent");
	else {
		$(".headInactive").each(function(){
			$(this).remove();
		});
		$("#where").remove();
	}
	
	var form = $("#mainForm");
	
	if (headIsActive("#head1")) {
		$("#head1").click(function(){
			form.attr("action", '/');
			form.attr("method", 'POST');
			submitForm();
		});
	}
	
	if (headIsActive("#head2")) {
		$("#head2").click(function(){
			form.attr("action", '/paragraphs');
			form.attr("method", 'POST');
			submitForm();
		});
	}
	
	if (headIsActive("#head3")) {
		$("#head3").click(function(){
			form.attr("action", '/questions');
			form.attr("method", 'POST');
			submitForm();
		});
	}
	
	if (headIsActive("#head4")) {
		$("#head4").click(function(){
			form.attr("action", '/review');
			form.attr("method", 'POST');
			submitForm();
		});
	}
	
	if (headIsActive("#head5")) {
		$("#head5").click(function(){
			form.attr("action", '/save');
			form.attr("method", 'POST');
			submitForm();
		});
	}	
});