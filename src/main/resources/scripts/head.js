var headIsActive = function(id) {
	if ($(id).attr('class').includes("headActive"))
		return true;
	return false;
}

$(document).ready(function(){
	$("[title]").tooltip();
	$('#where').hide();
	
	var completed = $completed;
	var where = ${where.substring(4, ${where.length()})};
	where += 1;
	
	var topReached = Math.max(completed, where);
	
	
	$("#btn_help").on('click', function(event){
		event.preventDefault();
		
		var tooltips = [];
		$("[title]").each(function(){
			if (!$(this).attr('class') || !$(this).attr('class').includes("nohelp")) {
				tooltips.push($(this));
			}
		});
		
		for (var i=0; i<tooltips.length; i++) {
			tooltips[i].tooltip("open");
		}
	});
	
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

	$("#head1").addClass("nohelp");
	$("#head2").addClass("nohelp");
	$("#head3").addClass("nohelp");
	$("#head4").addClass("nohelp");
	$("#head5").addClass("nohelp");
	
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