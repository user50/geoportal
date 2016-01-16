/**
 * 
 */
var templates = (function() {
	// private
	var area = null;
	var embdObj = null;
	
	var _openTemplate = function(id){
		showMask();
		$.ajax({
			type : "GET",
			url : "get/" + id
		}).done(function(data) {
			hideMask();
			$('#name').val(data.name);
			$('#id').val(data.id);
			if (area) {
				CKEDITOR.instances.template.setData(data.template);
				//area.instanceById('template').setContent(data.template);
			} else {
				$('#template').val(data.template);
			}
		}).fail(function(jqXHR, textStatus) {
			hideMask();
			alert("Request failed: " + textStatus);
		});
	};
	
	var selectElements = function(selectableContainer, elementToSelectId){
		var elementToSelect = $('#'+elementToSelectId);
	    $(".ui-selected", selectableContainer).not(elementToSelect).removeClass("ui-selected").addClass("ui-unselecting");
	    elementToSelect.not(".ui-selected").addClass("ui-selected");
	    loadData(elementToSelectId);
	};
	
	return {
		// public
		init : function(embdId) {
			embdObj = $('#' + embdId);
			embdObj.selectable({
				stop : function() {
					$(".ui-selected", this).each(function() {
						var selected = $(this);
						var id = selected.attr('id');
						_openTemplate(id);
					});
				}
			});
		},
		setEditor : function(tmplId) {
			area =  CKEDITOR.replace( 'template' ); 
			if(tmplId){
				_openTemplate(tmplId);
				selectElements(embdObj, tmplId);
			}
		},
		removeEditor : function() {
			area.removeInstance('template');
			area = null;
		},
		clearFields : function() {
			$('#name').val('');
			$('#id').val('');
			if (area) {
				CKEDITOR.instances.template.setData('');
			} else {
				$('#template').val('');
			}
		},
		deleteTemplate : function() {
			var id = $('#id').val();
			if (id == '')
				return;
			var name = $('#name').val();
			if (confirm("Вы действительно делаете удалить шаблон '" + name + "'")) {
				showMask();
				$.ajax({
					type : "GET",
					url : "remove/" + id
				}).done(function(data) {
					hideMask();
					$('#name').val('');
					$('#id').val('');
					if (area) {
						CKEDITOR.instances.template.setData('');
					} else {
						$('#template').val('');
					}
					$('li#' + id, embdObj).remove();
				}).fail(function(jqXHR, textStatus) {
					hideMask();
					alert("Request failed: " + textStatus);
				});
			}
		}
	};
})();

function showMask() {
	var mask = document.getElementById('loadmask');
	var img = document.getElementById('loadimg');
	mask.style.display = 'block';
	wWidth = (('innerWidth' in window) ? window.innerWidth : document.documentElement.clientWidth);
	wHeight = (('innerHeight' in window) ? window.innerHeight
			: document.documentElement.clientHeight);
	mask.style.height = wHeight + 'px';
	mask.style.width = wWidth + 'px';
	img.style.left = wWidth / 2 + 'px';
	img.style.top = wHeight / 2 + 'px';
}

function hideMask() {
	var mask = document.getElementById('loadmask');
	mask.style.display = 'none';
}
