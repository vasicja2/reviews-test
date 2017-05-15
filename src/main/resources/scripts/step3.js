/**
 * Source: http://stackoverflow.com/questions/2897155/get-cursor-position-in-characters-within-a-text-input-field
 * */
(function($) {
    $.fn.getCursorPosition = function() {
        var input = this.get(0);
        if (!input) return; // No (input) element found
        if ('selectionStart' in input) {
            // Standard-compliant browsers
            return input.selectionStart;
        } else if (document.selection) {
            // IE
            input.focus();
            var sel = document.selection.createRange();
            var selLen = document.selection.createRange().text.length;
            sel.moveStart('character', -input.value.length);
            return sel.text.length - selLen;
        }
    }
})(jQuery);

var submitForm = function() {
	$("#mainForm").submit();
}

$(document).ready(function(){
	
	//				Define all actions on buttons
	var form = $("#mainForm");

	$('#btn-next').click(function(){
		form.attr("action", '/save');
		form.attr("method", "POST");
		submitForm();
	});
	$('#btn-back').click(function(){
		form.attr("action", '/questions');
		form.attr("method", "POST");
		submitForm();
	});
	
    $("#accordion").accordion({
	    autoHeight: false,
	    collapsible: true
	});	
    
	//					Prevent user from submitting by enter
	$(window).keydown(function(event){
	    if(event.keyCode == 13) {
	      event.preventDefault();
	      return false;
	    }
	});
});