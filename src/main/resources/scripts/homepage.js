var submitForm = function() {
	$("#mainForm").submit();
}

var dialogToReset = function(){
	var text = "<div id=\"dialog-reset\" title=\"Delete the existing review?\">";
	text += "<p><span class=\"ui-icon ui-icon-alert\" style=\"float:left; margin:12px 12px 20px 0;\"></span>" +
			"Your review will be deleted and you will start from scratch. Are you sure?</p>";
	text += "</div>";
	return text;
}

$(document).ready(function(){
	$("#chbox_reset").hide();
	
	$("#btn_reset").click(function(event){
		event.preventDefault();
		$("#mainForm").append(dialogToReset());
		
	    $( "#dialog-reset" ).dialog({
	        resizable: false,
	        height: "auto",
	        width: 400,
	        modal: true,
	        buttons: {
	          "Reset": function() {
	            $("#chbox_reset").prop("checked", true);
	            $("#mainForm").attr("action", '/');
	            $("#mainForm").attr("method", 'POST');
	            submitForm();
	          },
	          Cancel: function() {
	            $( this ).remove();
	          }
	        }
	      });
	});
});