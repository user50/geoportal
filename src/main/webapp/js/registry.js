var registry = (function() {
	// private
	var embdObj = null;
	
	var registryData = [];
	var curPage = {};
	
	var showLayersList = function(id){
		$('#'+id+'-browse-results-header-summaryText').html(registryData.length + ' найдено');
		var _filter = $('#'+id+'-browse-results-header-filterText').val();
		var arg= {data:registryData};
		if(_filter != ''){
			var d = [];
			for(var inx in registryData){
				if(new RegExp(_filter + '.*', 'i').test(registryData[inx].name))d.push(registryData[inx]);
			}
			arg= {data:d};
		}
		
		var pages = Math.floor(arg.data.length / 10) + 1;
		var paging = '';
		for(var i  = 1 ; i < pages+1 ; i++){
			var c = '';
			if(!curPage[id])curPage[id] = 1;
			if(i == curPage[id])c = 'class="current"';
			paging += '<a href="#" onclick="registry.setPage(\''+id+'\', '+i+')" '+c+'>'+i+'</a>'; 
		}
		$('#'+id+'-paging').html(paging);
		$('#'+id+'-obj_list').html($.templates('#reestr_section').render(arg,
				{ /*filter: function(arg) {
                    return _filter == '' || new RegExp(_filter + '.*', 'i').test(arg);
                	},*/
                  paging: function(inx) {
                	  return inx < curPage[id] * 10 && inx >= (curPage[id]-1)*10;
                  }
       }));
		var start = (curPage[id]-1) * 10 + 1;
		$('#'+id+'-browse-results-header-pageControl .result').html('Отображено ' + start + '-' + ($('#'+id+'-obj_list .snippet').length +start - 1));
	}
	
	var selectElements = function(id){
		loadData(id);
	};
	
	var loadData = function(id){
		showMask();
		$.ajax({
			type : "GET",
			url : id
		}).done(function(data) {
            registryData = data;
		    showLayersList(id);
			hideMask();
		}).fail(function(jqXHR, textStatus) {
			hideMask();
			alert("Request failed: " + textStatus);
		});
	};
	
	return {
		init : function(objId) {
			embdObj = $('#' + objId);
			embdObj.tabs({
				activate : function(event, ui) {
					var id = ui.newPanel.attr('id');
					loadData(id);
					
				}
			});
			selectElements('layers');
			$('#layers-browse-results-header-clearFilter').click(function(){
				$('#layers-browse-results-header-filterText').val('');
				showLayersList('layers');
			});
			$('#layers-browse-results-header-filterText').on('keypress', function(e){
				if(e.keyCode == 13){
					showLayersList('layers');
				}
			});
			$('#services-browse-results-header-clearFilter').click(function(){
				$('#services-browse-results-header-filterText').val('');
				showLayersList('services');
			});
			$('#layers-browse-results-header-filter').click(function(){
			    showLayersList('layers');
			});
			$('#services-browse-results-header-filter').click(function(){
			    showLayersList('services');
			});
			$('#add').click(function(){
				//TODO add
			});
		},
		setPage : function(obj, page){
			curPage[obj] = page;
			 showLayersList(obj);
		},
		remove : function(id){
		    if(confirm("Подтвердите пожалуйста удаление...")){
    		    showMask();
                $.ajax({
                    type : "GET",
                    url : "rest/remove/" + id 
                }).done(function(data) {
//                    hideMask();
                    window.location.href="";
                }).fail(function(jqXHR, textStatus) {
                    hideMask();
                    alert("Request failed: " + textStatus);
                });
		    }
		},
		openEmptyForm : function(){
		    try{
	            var type = $($(".ui-selected")[0]).attr("id")
	            window.open("-1?type=" + type,"_blank");
		    }catch(e){
		        alert("Для создания новой записи реестра необходимо сначала выбрать тип");
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