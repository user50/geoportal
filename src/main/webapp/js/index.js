function onResize() {
	var messageBoxHeight = 160;
	var h = (('innerHeight' in window) ? window.innerHeight : document.documentElement.offsetHeight);
	var w = (('innerWidth' in window) ? window.innerWidth : document.documentElement.offsetWidth);
	document.getElementById('map').style.height = (h-48) + 'px';
	document.getElementById('main-tool').style.height = (h-50) + 'px';
	layers.onResize(w,h);
	helper.onResize(w,h);
}

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

function alertMessage(message) {
	window.alertDiv = window.alertDiv || $("<div id='drop_down_bar'/>").appendTo("body");
	window.alertDiv.text(message).slideDown(350, function() {
			$(this).delay(4000).slideUp("slow");
	});
}

function confirmMessage(title, message, callback) {
var confirmDiv = $("<div title='"+title+"'>"+
		"<p><span class='ui-icon ui-icon-alert' style='float: left; margin: 0 7px 20px 0;'></span>"+message+"</p>"+
"</div>").appendTo("body");
confirmDiv.dialog({
	resizable : true,
	//height : 180,
	modal : true,
	dialogClass: "gis-confirm",
	buttons : {
		"Да" : function() {
			$(this).dialog("close");
			callback();
			confirmDiv.remove();
		},
		"Нет" : function() {
			$(this).dialog("close");
			confirmDiv.remove();
		}
	}
});
}


function promptMessage(title, message, callback, large){
var pDiv = $('<div title="'+title+'">'+
	'<p class="prompt_message">'+message+'</p>'+
	((large)?'<textarea name="ptype" id="ptype" class="prompt_text ui-widget-content ui-corner-all" />':
	'<input type="text" name="ptype" id="ptype" class="prompt_text ui-widget-content ui-corner-all" />')+
	'</div>').appendTo("body");
var pDialog = pDiv.dialog({
      modal: true,
      dialogClass: "gis-prompt",
      buttons: {
        "ОК": function() {
        	callback(ptype.value);
        	$( this ).dialog( "close" );
        	pDiv.remove();
          
        },
        "Отменить": function() {
          $( this ).dialog( "close" );
          pDiv.remove();
        }
      },
      close: function() {
    	  pDiv.remove();
      }
    });
  if(!large){
	$('#ptype', pDiv).on('keypress', function(e){
	 if(e.keyCode == 13){
		callback(ptype.value);
		pDiv.dialog( "close" );
     	pDiv.remove();
	 }
	});
  }
  setTimeout(function(){
	  $('#ptype', pDiv).focus();
  },1);
}

(function($) {
    $.extend({
        showPageDialog: function (title, content, buttons, options) {
            if (!buttons){
                buttons = { "Закрыть": function () { $(this).dialog("close"); } };
            }else{
            	buttons["Закрыть"] = function () { $(this).dialog("close");};
            }
            var defOptions = {
                autoOpen: false,
                modal: true,
                // show: "blind",
                // hide: "explode",
                title: title,
                width: 450,
                //height: $(document).height(),
                dialogClass: "gis-card",
                buttons: buttons
            };
            if (options)
                defOptions = $.extend(defOptions, options);
            var pd = $("#pageDialog");
            if (pd.length < 1){
                pd = $("<div/>").attr("id", "pageDialog")
                                .appendTo("body");
                //pd.width(options.width);
                //pd.height(options.height);
            }else{
                pd.dialog('destroy');
            }
            	pd.html(content).dialog(defOptions).dialog("open");
        }
    });
})(jQuery);

$.minicolors = {
    defaults: {
        animationSpeed: 50,
        animationEasing: 'swing',
        change: null,
        changeDelay: 0,
        control: 'hue',
        defaultValue: '',
        hide: null,
        hideSpeed: 100,
        inline: false,
        letterCase: 'lowercase',
        opacity: false,
        position: 'bottom left',
        show: null,
        showSpeed: 100,
        theme: 'default'
    }
};

$(document).ready(function() {
	if(browser() == 'Internet Explorer'){
		alert('Браузер не оптимизирован для приложения. Установите Chrome или FireFox.');
		window.location.href = 'https://www.google.com/intl/ru/chrome/browser';
		return;
	}
	onResize();
	$(document).ajaxError(function(event, request) {
		if (request.status == 403)
			window.location.reload();
	});
	$('#close-left-tool').on('click', function() {
		$('#main-tool').toggle("slide", {
			complete : function() {
				$('#open-left-tool').css('display', 'block');
				var $ss = $.stylesheet('.leaflet-left .leaflet-control');
				$ss.css('margin-left', '40px');
				//remember that
				$.cookie("main-menu", 'closed', { expires : 7 });
			}
		});
	});
	$('#open-left-tool').on('click', function() {
		$('#open-left-tool').css('display', 'none');
		$('#main-tool').toggle("slide");
		var $ss = $.stylesheet('.leaflet-left .leaflet-control');
		$ss.css('margin-left', '360px');
		$.cookie("main-menu", 'open', { expires : 7 });
		onResize();
	});
	if($.cookie("main-menu") == 'closed'){
		var $ss = $.stylesheet('.leaflet-left .leaflet-control');
		$ss.css('margin-left', '40px');
		$('#open-left-tool').css('display', 'block');
		$('#main-tool').css('display', 'none');
	}
	$('#close-right-tool').on('click', function() {
		$('#helper-tool').toggle("slide", {
			complete : function() {
				$('#open-right-tool').css('display', 'block');	
				var $ss = $.stylesheet('.leaflet-right .leaflet-control');
				$ss.css('margin-right', '40px');
				//remember that
				$.cookie("helper", 'closed', { expires : 7 });
			},
			direction : "right"
		});
	});
	$('#open-right-tool').on('click', function() {
		$('#open-right-tool').css('display', 'none');
		$('#helper-tool').toggle("slide", {
			direction : "right"
		});	
		var $ss = $.stylesheet('.leaflet-right .leaflet-control');
		$ss.css('margin-right', '360px');
		$.cookie("helper", 'open', { expires : 7 });
	});
	map.initMap('map');
	$("#tabs").tabs({
		  activate: function( event, ui ) {
			  onResize();
		  }
	});
	layers.init('layers');
	layers.on('select', function(e, data){
		map.addEnvLayer(data);
	});
	layers.on('deselect', function(e, layer){
		map.removeEnvLayer(layer.id);
		helper.removeData(layer.id);
	});
	layers.on('edit', function(e, data){
		map.onEnvLayer(data);
	});
	layers.on('esri_legend', function(e, data){
		map.showEsriLegend(e.metadata.id, data);
	});
	map.on('objects_loaded', function(e, layer){
		helper.addData(layer.id, layer.data);
	});
	map.on('objects_removed', function(e, layer){
		helper.removeDataByIds(layer.id, layer.data);
	});
	map.on('found_hidden', function(e, data){
		/*if(data.layers.length > 0){
			confirmMessage("Загрузка объекта", "Объект находтся на слое, который сейчас не выбран. Выбрать?", function(){
				layers.selectLayer(data.layers[0].id);
			});
		}else{
			confirmMessage("Загрузка объекта", "Объект не привязан ни к одному слою. Создать слой?", function(){
				//TODO create layer
			});
			
		}*/
	});
	$('#reset_all').button().click(function(){
		layers.reset();
		map.clear();
		helper.clear();
	});
	$('#show_report').button().click(function(){
	    window.open('report/layers.pdf',  '_blank');
    });
	$('#show_report_is').button().click(function(){
		window.open('report/integration-service',  '_blank');
	});

	$('#add_layer').button().click(function(){
		layers.openForm();
	});
	userManager.init();
	helper.init('helper', function(p){
		var h = map.getHome();
		if(h == null)return 0;
		var home = new L.LatLng(h[0], h[1]);
		if(typeof p.lat != 'undefined' && typeof p.lon != 'undefined'){
			return home.distanceTo(new L.LatLng(p.lat, p.lon));
		}else{
			if(p.geom == null) return -1;
			var geom = eval('[' + p.geom + ']')[0];
			var d = [];
			var _r = function(arr){
				for(var i in arr){
					if(typeof arr[i][0] == "object"){arr[i] = _r(arr[i]);continue;}
					d.push(home.distanceTo(new L.LatLng(arr[i][1], arr[i][0])));
				}
				return arr;
			};
			_r(geom.coordinates);
			var asc = function( a, b ) {
				return a - b;
			};
			d.sort( asc ); 
			return d[0];
		}
	});
	helper.on('click', function(e, id){
		map.fitToObject(id);
	});
	helper.on('over', function(e, id){
		map.distinguish(id);
	});
	helper.on('out', function(e, id){
		map.undistinguish(id);
	});
	helper.on('search_complete', function(e, data){
		//helper.replaceData(data);
	});
	helper.on('geolocation', function(e, data){
		map.toPoint(data.results[0]);
	});
	onResize();
});

function browser()
{
    var ua = navigator.userAgent;
    
    if (ua.search(/MSIE/) > 0) return 'Internet Explorer';
    if (ua.search(/Firefox/) > 0) return 'Firefox';
    if (ua.search(/Opera/) > 0) return 'Opera';
    if (ua.search(/Chrome/) > 0) return 'Google Chrome';
    if (ua.search(/Safari/) > 0) return 'Safari';
    if (ua.search(/Konqueror/) > 0) return 'Konqueror';
    if (ua.search(/Iceweasel/) > 0) return 'Debian Iceweasel';
    if (ua.search(/SeaMonkey/) > 0) return 'SeaMonkey';
    
    // Браузеров очень много, все вписывать смысле нет, Gecko почти везде встречается
    if (ua.search(/Gecko/) > 0) return 'Gecko';

    // а может это вообще поисковый робот
    return 'Search Bot';
}
