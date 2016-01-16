var userManager = (function(){
	//private
	return {
		//public
		init : function(){
			$('.user_section_popup').mouseout(function(event){
				var target = event.toElement || event.relatedTarget;
				if(target != null && jQuery.contains($(this)[0], target))return;
				$(this).hide();		
			});
			$('.user_section').click(function(){
				$('#login_section').hide();
				var popup = $('.user_section_popup');
				popup.toggle();
			});
			$('#loginAnchor').click(function(event){
				event.preventDefault();
				event.stopPropagation(); 
				$('#login_notify').hide();
				$('#login_section').show();
			});
			$('#login_section button').button();
			$('#login_section_close').click(function(event){
				event.preventDefault();
				event.stopPropagation() 
				$('#login_section').hide();
			});
			 var options = { 
				        //target:        '#output1',   // target element(s) to be updated with server response 
				        //beforeSubmit:  showRequest,  // pre-submit callback 
				        success:       function(resp){
				        	var r = eval(resp);
				        	if(r){
				        		window.location.reload();
				        	}else{
				        		$('#login_notify').show();
				        	}
				        }  
				 
				        // other available options: 
				        //url:       url         // override for form's 'action' attribute 
				        //type:      type        // 'get' or 'post', override for form's 'method' attribute 
				        //dataType:  null        // 'xml', 'script', or 'json' (expected server response type) 
				        //clearForm: true        // clear all form fields after successful submit 
				        //resetForm: true        // reset the form after successful submit 
				 
				        // $.ajax options can be used here too, for example: 
				        //timeout:   3000 
				    }; 
				 
				    // bind form using 'ajaxForm' 
			 $('#login_section form').ajaxForm(options); 
		},
		exit : function(){
			window.location = 'logout';
		}, 
		openAdminConsole: function(){
			window.open('admin/users',  '_blank');
		}
	};
})();