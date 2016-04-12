L.Polyline.prototype.length_in_meters = function () {
	var metros_totales_ruta = 0;
	var coordenadas_iniciales = null;
    var array_coordenadas_polilinea = this._latlngs;
    for (var i = 0; i < array_coordenadas_polilinea.length - 1; i++) {
       coordenadas_iniciales = array_coordenadas_polilinea[i];
       metros_totales_ruta  += coordenadas_iniciales.distanceTo(array_coordenadas_polilinea[i + 1]);
    }
   metros_totales_ruta = metros_totales_ruta.toFixed();
   return metros_totales_ruta;
};

L.Tooltip = L.Class.extend({
	initialize: function (map) {
		this._map = map;
		this._popupPane = map._panes.popupPane;

		this._container = map.options.drawControlTooltips ? L.DomUtil.create('div', 'leaflet-draw-tooltip', this._popupPane) : null;
		this._singleLineLabel = false;
	},

	dispose: function () {
		if (this._container) {
			this._popupPane.removeChild(this._container);
			this._container = null;
		}
	},

	updateContent: function (labelText) {
		if (!this._container) {
			return this;
		}
		labelText.subtext = labelText.subtext || '';

		// update the vertical position (only if changed)
		if (labelText.subtext.length === 0 && !this._singleLineLabel) {
			L.DomUtil.addClass(this._container, 'leaflet-draw-tooltip-single');
			this._singleLineLabel = true;
		}
		else if (labelText.subtext.length > 0 && this._singleLineLabel) {
			L.DomUtil.removeClass(this._container, 'leaflet-draw-tooltip-single');
			this._singleLineLabel = false;
		}

		this._container.innerHTML =
			(labelText.subtext.length > 0 ? '<span class="leaflet-draw-tooltip-subtext">' + labelText.subtext + '</span>' + '<br />' : '') +
			'<span>' + labelText.text + '</span>';

		return this;
	},

	updatePosition: function (latlng) {
		var pos = this._map.latLngToLayerPoint(latlng),
			tooltipContainer = this._container;

		if (this._container) {
			tooltipContainer.style.visibility = 'inherit';
			L.DomUtil.setPosition(tooltipContainer, pos);
		}

		return this;
	},

	showAsError: function () {
		if (this._container) {
			L.DomUtil.addClass(this._container, 'leaflet-error-draw-tooltip');
		}
		return this;
	},

	removeError: function () {
		if (this._container) {
			L.DomUtil.removeClass(this._container, 'leaflet-error-draw-tooltip');
		}
		return this;
	}
});

var CloseEditorControl = L.Control.extend({
    options: {
        position: 'topleft'
    },

    onAdd: function (_map) {
        // create the control container with a particular class name
    	this._container = L.DomUtil.create('div', 'leaflet-bar');

        var link = L.DomUtil.create('a', 'closeeditor-control', this._container);
        link.href = '#';
        link.title = 'Закрыть редактор';
        
        L.DomEvent
        .addListener(link, 'click', L.DomEvent.stopPropagation)
        .addListener(link, 'click', L.DomEvent.preventDefault)
        .addListener(link, 'click', function(){
        	map.offEditor();
        }, this);

        return this._container;
    }
});

var map = (function() {  
  
    //Private variables  
    var startPoint = [57.62619561811418, 39.891357421875];
    var startZoom = 13;
	var _map = null;
	var _isEditing = null;
	var _ipPopupAllowed = true;
	var _home = null;
	var _homeMarker = null;
	var _registry = {};
	var eventListeners = {
		'click_object' : [], //нажали мышкой на попап объекта
		'objects_loaded' : [], //догрузили объекты на карту
		'select_object' : [], //нажали мышкой на объект
		'found_hidden' : [], //нашли объект на сервере, но показать не можем - нет на карте
		'objects_removed' : [] //удаляем объекты в редакторе
	};
	var _drawControl = null;
	var _closeDrawControl = null;
	var envLayers = {};
	//var _ajaxQueue = $.ajaxMultiQueue(5);
	var _loadingThreads = {};
	var _searchLayer = '__search__';
	var _routeLayer = '__route__';
	var _routeDirections = {'Left':'←', 'Right':'→','Straight':'↑ ','Roundabout':'↓ ','DestinationReached':'', 'SlightRight':'→', 'SlightLeft':'←'};
	var _makeRoute = function(route){
		var _routeline = L.polyline(route.coordinates, {color: 'blue'}).addTo(envLayers[_routeLayer].layer);
		var aux_meters = route.summary.totalDistance;
		var popupString = '<b>' + route.name + '</b><br/>';
		popupString += 'общее расстояние: ' + (aux_meters/1000).toFixed(2) + ' км.<br/>';
		for(var inx in route.instructions){
			var instr = route.instructions[inx];
			popupString+=_routeDirections[instr.type] + ' ' + instr.distance + 'м ';
			if(instr.road != '')popupString+='(' +instr.road + ')';
			popupString+='<br/>';
		}
		popupString+='<a onclick="map.hideRoute()" href="#">скрыть</a>';
		_routeline.bindPopup(popupString);
		_map.fitBounds(_routeline.getBounds());
	};
	var _wmsLayer = null;
	var _wmsPermLayer = null;
	var _geoLocation = function(){
		if($.cookie("home") != undefined)
			_home = eval('['+$.cookie("home")+']');
		if(_home != null && _home.length == 2){
			//_map.setView(_home, startZoom); 
			_createHomeMarker(_home);
		}
		$.getScript("js/gears_init.js").fail(function(jqxhr, settings, exception) {
			alertMessage("Fail request js/gears_init.js.");
		}).done(function(script, textStatus) {
			$.getScript("js/geo.js").fail(function(jqxhr, settings, exception) {
				alertMessage("Fail request js/geo.js.");
			}).done(function(script, textStatus) {
				if (geo_position_js.init()) {
					  geo_position_js.getCurrentPosition(function (p) {
						/*if(_home == null){
							_home =  [p.coords.latitude, p.coords.longitude]; 
							//_map.setView(_home, startZoom); 
							_createHomeMarker(_home);
						}else{
							if(_home[0] != p.coords.latitude || _home[1] != p.coords.longitude){
								confirmMessage('Мое местоположение', 'Определено новое месторасположение. Установить?', function(){*/
									_home =  [p.coords.latitude, p.coords.longitude]; 
									//_map.setView(_home, startZoom); 
									_createHomeMarker(_home);
				    	/*		});
							}
						}*/
		  			   }, function () {
		  					alertMessage('Местоположение не определено.');
		  			   });
		    	}
			});
		});
	};
	
	var _createHomeMarker = function(coordinates){
		$.cookie("home", coordinates, { expires : 7 });
		if(_homeMarker == null){
			_homeMarker = L.marker(_home, {icon: L.icon({
				    iconUrl: 'css/images/home-icon.png',
				    iconSize: [25, 41],
				    iconAnchor: [13, 41],
				    popupAnchor: [-3, -76]
				})}).addTo(_map).on('click', function(){
					confirmMessage('Мое местоположение', 'Желаете определить новое местоположение?', function(){
	    				_setHome();
	    			});
				});
		}else{
			_homeMarker.setLatLng( coordinates );
		}
	};
	
	var _setHome = function(){
		_map._container.style.cursor = 'crosshair';
		var _tooltip = new L.Tooltip(_map);
		_tooltip.updateContent({ text: 'Нажмите на карту для определения вашего местоположения' });
		var movefunc = function(e){
			_tooltip.updatePosition(e.latlng);
		};
		_map.on('mousemove', movefunc);
		var set = function(e){
			var latlng = e.latlng;
			_home = [latlng.lat, latlng.lng];
			_map._container.style.cursor = '';
			_map.off('mousemove', movefunc);
			_tooltip.dispose();
			_map.off('click', set);
			_createHomeMarker(_home);
		};
		_map.on('click', set);
	};
	
	var _initEditorEvents = function(){
		_map.on('draw:created', function (e) {
		    var type = e.layerType,
		        layer = e.layer;
		    promptMessage("Создание объекта", "Введите название объекта", function(title) {
				if (title != '' && title != null) {
					var gj = layer.toGeoJSON();
					gj.properties.name = title;
					if(type == "circle")gj.properties.radius = layer._mRadius;
					showMask();
		    		$.ajax({
		    	    	url : 'geo/gis_objects_create',
		    			type : 'POST',
		    			data : {type: type, obj:JSON.stringify(gj), layerId:_isEditing, name:title}
		    		}).done(function(data) {
		    			hideMask();
		    			if(type == "marker"){
		    				_createPoint(_isEditing, data);
		    			}
		    			else{
		    				_createShape(_isEditing, data);
		    			}
		    			alertMessage( "Объект сохранен");
		    			_fireEvent('objects_loaded', {}, {id:_isEditing, data:[data]});
		    		}).fail(function(jqXHR, textStatus) {
		    			hideMask();
		    			alertMessage("Произошла ошибка во время создания" );
		    		});
				}
			});
		});
		_map.on('draw:deleted', function (e) {
			var layers = e.layers;
			var toupdate = [];
		    layers.eachLayer(function (layer) {
		    	var dataObj = layer.dataObj;
		    	if(dataObj != null)toupdate.push(dataObj.id);
		    });
		    showMask();
    		$.ajax({
    	    	url : 'geo/gis_objects_delete',
    			type : 'POST',
    			data : JSON.stringify({layerId: _isEditing, objIds:toupdate}),
    			contentType : 'application/json'
    		}).done(function(data) {
    			hideMask();
    			alertMessage( "Объекты успешно удалены");
    			_fireEvent('objects_removed', {}, {id:_isEditing, data:toupdate});
    		}).fail(function(jqXHR, textStatus) {
    			hideMask();
    			alertMessage("Произошла ошибка во время удаления" );
    		});
		});
		_map.on('draw:edited', function (e) {
			var layers = e.layers;
			var toupdate = [];
		    layers.eachLayer(function (layer) {
		    	var gj = layer.toGeoJSON();
				if(layer._mRadius)gj.properties.radius = layer._mRadius;
		    	var dataObj = layer.dataObj;
		    	toupdate.push({id: dataObj.id, geojson : JSON.stringify(gj)});
		    });
		    showMask();
    		$.ajax({
    	    	url : 'geo/gis_objects_update',
    			type : 'POST',
    			data : JSON.stringify(toupdate),
    			contentType : 'application/json'
    		}).done(function(data) {
    			hideMask();
    			alertMessage( "Объекты успешно сохранены");
    		}).fail(function(jqXHR, textStatus) {
    			hideMask();
    			alertMessage("Произошла ошибка во время сохранения" );
    		});
		});
		_map.on('draw:deletestart', function(){
			_ipPopupAllowed = false;
		});
		_map.on('draw:deletestop', function(){
			_ipPopupAllowed = true;
		});
	};
	
	var _removeEditor = function (){
		if(_drawControl == null)return;
		_map.removeControl(_drawControl);
		_map.removeControl(_closeDrawControl);
		_closeDrawControl = null;
		_drawControl = null;
		_isEditing = null;
	};
	
	var _setupEditor = function (_featureGroup){
		// Initialize the draw control and pass it the FeatureGroup of editable layers
		_drawControl = new L.Control.Draw({
			position: 'topleft',
			draw: {
		        circle: false,
		        marker: {
		        	repeatMode: true
		        }
		    },
			edit: {
		        featureGroup: _featureGroup
		    }
		});
		_closeDrawControl = new CloseEditorControl();
		_map.addControl(_drawControl);
		_map.addControl(_closeDrawControl);
	};
	
	
	
    //Private method  
	var _clearMap = function(){
		if(_featureGroup == null){
			for(var i = 0; i < nodes.length; i++){
				try{_map.removeLayer(nodes[i]);}catch(e){}
			}
		}else{
			_map.removeLayer(_featureGroup);
			_featureGroup = null;
		}
		nodes = [];
	};
	
	var _generateIcon = function(id){
		return L.icon({
		    iconUrl: 'geo/flagicon/'+ id,
		    iconSize: [25, 41],
		    iconAnchor: [13, 41],
		    popupAnchor: [-3, -76]
		});
	};
	
	var _generateIconByObject = function(id){
		return L.icon({
		    iconUrl: 'geo/object/flagicon/'+ id,
		    iconSize: [25, 41],
		    iconAnchor: [13, 41],
		    popupAnchor: [-3, -76]
		});
	};
	
	var _createLayer = function(layerId, objects, metadata){
		var fGroup = null;
		if(metadata.permissions == 1 || isEditor() || objects.length < parseInt(getSettings()['CLUSTERING_AMOUNT'])){
			fGroup = new L.FeatureGroup();
		}else{
			fGroup = new L.MarkerClusterGroup({ disableClusteringAtZoom: 17 });
		}
		_map.addLayer(fGroup);
		envLayers[layerId] = {layer:fGroup,metadata:metadata};
		for(var inx in objects){
			if (objects[inx].objectGeomType == 'Point'){
				_createPoint(layerId, objects[inx], metadata);
			}else{
				_createShape(layerId, objects[inx]);
			}
		}
		return fGroup;
	};
    
	_rotateCoordinates = function(arr){
		for(var i in arr){
			if(typeof arr[i][0] == "object"){arr[i] = _rotateCoordinates(arr[i]);continue;}
			arr[i] = [arr[i][1], arr[i][0]];
		}
		return arr;
	};
	
	var _createShape = function(layerId, shapeObj, fit){
		if(_registry[shapeObj.id]){
			_map.removeLayer(_registry[shapeObj.id]);
		}
		var metadata = (layerId != _searchLayer)?envLayers[layerId].metadata:{lineColor:'red', lineWeight:3, fillColor:'red', fillOpacity:20};
		if(metadata.viewByObject){
			metadata = {lineColor: (shapeObj.lineColor)?shapeObj.lineColor:metadata.lineColor, 
						lineWeight:(shapeObj.lineWeight)?shapeObj.lineWeight:metadata.lineWeight, 
						fillColor:(shapeObj.fillColor)?shapeObj.fillColor:metadata.fillColor,
						fillOpacity:(shapeObj.fillOpacity)?shapeObj.fillOpacity:metadata.fillOpacity};
		}
		var drawShape = function(geom){
			var shape = eval('[' + geom + ']')[0];
			var pText = shapeObj.name;
			//var tmpl = metadata.tmpl;
			var tmpl = (layerId != _searchLayer)?envLayers[layerId].metadata.tmpl:null;
			if(tmpl){
				pText = tmpl;
			}
			shapeObj.tmpl = pText;
			var popup = L.popup();//.setContent(pText);
			if(!envLayers[layerId])return;
			shapeObj.ownerLayerId = layerId;
			if(shape.type == 'LineString'){
				var p = L.polyline(_rotateCoordinates(shape.coordinates), {color: metadata.lineColor, weight: metadata.lineWeight}).addTo(envLayers[layerId].layer).on({
					'click' : bindPopupClick(popup, shapeObj, envLayers[layerId])
				});
				/*p.dataObj = shapeObj;
				_registry[shapeObj.id] = p;
				p.on({'remove': function(){delete _registry[shapeObj.id]}});
				if(fit)_map.fitBounds(p.getBounds());
				return;*/
			}else if(shape.type == 'Polygon'){
				var p = L.polygon(_rotateCoordinates(shape.coordinates), {color: metadata.lineColor, weight: metadata.lineWeight, fillColor: metadata.fillColor,  fillOpacity: metadata.fillOpacity/100}).addTo(envLayers[layerId].layer).on({
					'click' : bindPopupClick(popup, shapeObj, envLayers[layerId])
				});
				/*p.dataObj = shapeObj;
				_registry[shapeObj.id] = p;
				p.on({'remove': function(){delete _registry[shapeObj.id]}});
				if(fit)_map.fitBounds(p.getBounds());
				return;*/
			}else {
				var p = L.geoJson(shape, {color: metadata.lineColor, weight: metadata.lineWeight, fillColor: metadata.fillColor,  fillOpacity: metadata.fillOpacity/100}).addTo(envLayers[layerId].layer).on({
					'click' : bindPopupClick(popup, shapeObj, envLayers[layerId])
				});	
			}
			p.dataObj = shapeObj;
			_registry[shapeObj.id] = p;
			p.on({'remove': function(){delete _registry[shapeObj.id];}});
			if(parseInt(getSettings()['SHOW_LABEL'])){
				p.bindLabel(shapeObj.name);
			}
			if(fit)_map.fitBounds(p.getBounds());
		};
		if(shapeObj.geoJSON){
			drawShape(shapeObj.geoJSON);
		}else{
			if(!_loadingThreads[layerId])_loadingThreads[layerId] = $.ajaxMultiQueue(3);
			_loadingThreads[layerId].queue({
				url : 'geo/getshapebyid',
				type : 'POST',
				data : {id:shapeObj.id}
			}).done(function(object) {
				if(object == '')return;
				drawShape(object);
			}).fail(function(jqXHR, textStatus) {
				alertMessage( "Request failed: " + textStatus );
			});
		}
	};
	
	var _stopLoadingShapes = function(layerId){
		if(!_loadingThreads[layerId])return;
		_loadingThreads[layerId].dequeue();
		delete _loadingThreads[layerId];
	};
	
	var _showFeedback = function(dataObj){
		var buttons = {
				"Отправить": function(){
		            showMask();
		            $.post( 
		                'geo/feedback_send', 
		                $('#_FeedbackForm_').serialize(),
		                'json' // I expect a JSON response
		            ).done(function(res){
		                hideMask();
		                if("Success" == res){
		                    alertMessage("Замечание отправлено.");
		                    $('#pageDialog').dialog("close");
		                }else{
		                    alertMessage("Ошибка.");
		                }
		                
		            });
		        }
		};
		showMask();
		$.get('geo/feedback/' + dataObj.id, function(phtml) {
			hideMask();
            $.showPageDialog("Замечания", phtml, buttons);
        });
	};
	
	var bindPopupClick = function(pp, dataObj, layer){
		var buttons = {
				"Отправить замечание": function(){
					$('#pageDialog').dialog("close");
					_showFeedback(dataObj);
				}
		};
		
		if((layer.metadata && layer.metadata.permissions == 1)  || isEditor())buttons["Сохранить"] = function(){
            showMask();
            $.post( 
                'geo/update', 
                $('#_ObjectEditForm_').serialize(),
                'json' // I expect a JSON response
            ).done(function(res){
                hideMask();
                if(res.objectGeomType == 'Point'){
                    _createPoint(dataObj.ownerLayerId, res);
                }else{
                    _createShape(dataObj.ownerLayerId, res);
                }
                $('#pageDialog').dialog("close");
                alertMessage("Данные сохранены успешно.");
            }).fail(function(){
            	hideMask();
            	 alertMessage("Ошибка сохранения.");
            });
        };
        
        //var geom = eval('[' + dataObj.geom + ']')[0];
        //if(geom!= null && geom.type == 'Polygon' && isAdmin())buttons["Использовать в контроле прав"] = function(){
        if((dataObj.objectGeomType == 'Polygon' || dataObj.objectGeomType == 'MultiPolygon') && isAdmin())buttons["Использовать в контроле прав"] = function(){
            showMask();
            $.get( 
                'admin/addPermissionArea/'+dataObj.id
            ).done(function(res){
                hideMask();
                if(true == res){
                    alertMessage("Объект успешно добавлен.");
                }else{
                    alertMessage("Объект уже добавлен");
                }
                
            }).fail(function(){
            	hideMask();
           	 	alertMessage("Объект уже добавлен");
           });
        };
		
		return function(e) {
			_fireEvent('select_object', e, dataObj);
			if(!_ipPopupAllowed)return;
			if(pp._isOpen){
				_map.closePopup(pp);
			}else{
				if(pp.postInit){
					pp.setLatLng(e.latlng);		
					_map.openPopup(pp);
				}else{
				var createContent = function(content){
					pp.postInit = true;
					if((layer.metadata && layer.metadata.permissions == 1) || isEditor()){
						pp.setContent( content + "<div><a class='opengis' href='#'>Открыть</a></div>");
					}else{
						pp.setContent( content + "<div><img title='Отправить замечание' class='image_feedback' src='css/images/feedback_icon.png'/></div>" );
					}
					pp.setLatLng(e.latlng);		
					_map.openPopup(pp);
					$(pp._container).delegate('a.opengis', 'click', function(){
						showMask();
			            $.get('geo/getdialog/' + dataObj.id, function(phtml) {
			                hideMask();
			                $.showPageDialog("Объект", phtml, buttons, {width: 600, maxHeight: $(document).height()});
			                $('#tobuffer').button().click(function(){
			                	var popupWin = window.open('about:blank', '_blank', "location,width=400,height=300,top=0");
			                	var win_doc = popupWin.document;
			                	win_doc.open();
			        			win_doc.write($('.obj_geom')[0].innerHTML);
			        			win_doc.close();
			                	popupWin.focus(); // передаём фокус новому окну
			                });
			            });
						_map.closePopup(pp);
					});
					$(pp._container).delegate('img.image_feedback', 'click', function(){
						_showFeedback(dataObj);
					});
				};
				$.post( 
		                'geo/getpropertiesbyid', 
		                {id:dataObj.id},
		                'json' // I expect a JSON response
				).done(function(props) {
					var args = {};
					for(var p in props){
						args[props[p].key] = props[p].value; 
					}
					args['name'] = dataObj.name;
					args['lat'] = dataObj.lat;
					args['lon'] = dataObj.lon;
					args['oid'] = dataObj.id;
			        //var content = $.tmpl(pp.getContent(), args, {}).html();
					var content = $.templates({markup: dataObj.tmpl, allowCode: true }).render(args);
			        createContent(content);
				}).fail(function(jqXHR, textStatus) {
					createContent(dataObj.name);
				});
				}
			}
			
		};
	};
	
	var _createPoint = function(layerId, pointObj){
		if(_registry[pointObj.id]){
			_map.removeLayer(_registry[pointObj.id]);
		}
		var pText = pointObj.name;
		var tmpl = (layerId != _searchLayer && layerId != _routeLayer)?envLayers[layerId].metadata.tmpl:null;
		if(tmpl){
			pText = tmpl;
		}
		pointObj.tmpl = pText;
		var popup = L.popup({
			offset : new L.Point(0,-33),
			maxWidth: 700
		}).setLatLng([pointObj.lat, pointObj.lon]);//.setContent(pText);
		var config = {};
		if(layerId != _searchLayer && layerId != _routeLayer){
			if(arguments.length == 3){
				var metadata = arguments[2];
				if(metadata.viewByObject && pointObj.icon){
					config.icon = _generateIconByObject(pointObj.id);
				}else{
					config.icon = _generateIcon(layerId);
				}
			}else{
				config.icon = _generateIcon(layerId);
			}
		}
		if(layerId == _searchLayer){
			config.icon = _generateIconByObject(pointObj.id);
		}
		var p = L.marker([pointObj.lat, pointObj.lon], config).addTo(envLayers[layerId].layer);
		pointObj.ownerLayerId = layerId;
		if(layerId != _routeLayer){p.on({'click' : bindPopupClick(popup, pointObj, envLayers[layerId])});}
		else{p.bindPopup(pointObj.name);}
		p.dataObj = pointObj;
		_registry[pointObj.id] = p;
		if(parseInt(getSettings()['SHOW_LABEL'])){
			p.bindLabel(pointObj.name);
		}
		p.on({'remove': function(){
			delete _registry[pointObj.id];
		}});
	};
	
	var _fireEvent = function(eventName, event, dataObj){
		var listeners = eventListeners[eventName];
		for(var l in listeners){
			setTimeout((function(listener){
				return function(){
					listener(event, dataObj);
				};
			})(listeners[l]),1);
		}
	};
	
	var _getDataObjects = function (layerId, callback){
		showMask();
		$.ajax({
	    	url : 'geo/list_by_layer_id',
			type : 'GET',
			data : {layerId:layerId}, 
			contentType : 'application/json'
		}).done(function(objects) {
			hideMask();
			_fireEvent('objects_loaded', {}, {id:layerId, data:objects});
			callback(objects);
		}).fail(function(jqXHR, textStatus) {
			hideMask();
			alertMessage( "Request failed: " + textStatus );
		});	
		
	};
	
	var _findById = function(id, recursion, callback){
		/*if(_registry[id]){
			callback(_registry[id]);
			return;
		}
		if(!recursion){
			callback(null);
			return;
		}*/
		var found = false;
		_map.eachLayer(function (layer) {
		    if(layer.dataObj && layer.dataObj.id == id){found = true; callback(layer);}
		    if(recursion){
		    	if(layer.getLayers)
		    		var ls = layer.getLayers();
		    		for(var inx in ls){
		    			var m = ls[inx];
		    			if(m.dataObj && m.dataObj.id == id){
		    					found = true;
		    					try{
		    						if(layer.zoomToShowLayer)layer.zoomToShowLayer(m, function(){callback(m);});
		    					}catch(e){
		    						console.log('zoomToShowLayer failed. Possibly is not marker!');
		    					}
		    					break;
		    			}
		    	}
		    }
		});
		if(!found)callback(null);
	};
	
	var _on = function(eventName, callback){
		eventListeners[eventName].push(callback);
	};
	
	var _off = function(eventName, callback){
		if(eventListeners[eventName].indexOf(callback) >= 0){
			eventListeners[eventName].splice(eventListeners[eventName].indexOf(callback),1);			
		}
	};	
	
	var _getMarkerPosition = function(title, callback){
		_map._container.style.cursor = 'crosshair';
		var _tooltip = new L.Tooltip(_map);
		_tooltip.updateContent({ text: title });
		var movefunc = function(e){
			_tooltip.updatePosition(e.latlng);
		};
		var listener = function(e, obj){
			_map._container.style.cursor = '';
			_map.off('mousemove', movefunc);
			_off('select_object', listener);
			_map.off('click', arguments.callee);
			_tooltip.dispose();
			_ipPopupAllowed = true;
			callback(e, obj);
		};
		_ipPopupAllowed = false;
		_map.on('mousemove', movefunc);
		_map.on('click', listener);
		_on('select_object', listener);
	};
	
	var _buildLinkUrl = function(){
		var url = window.location.origin + window.location.pathname + '?';
		var c = _map.getCenter();
    	url += 'c=' + [c.lat, c.lng].join();
    	url += '&z=' + _map.getZoom();
		var l = layers.getSelected();
		if(l!='')url += '&layers=' + layers.getSelected();
		return url;
	};
	
	var stdTiles = L.tileLayer('http://maps.yarcloud.ru:8088/enk/{z}/{x}/{y}.png',
			{
				attribution : 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a>',
				maxZoom : 18
			});
	
	var _esriLegends =  null; //хранение включенного контрола
	
	var _EsriLegendControl = L.Control.extend({
	    options: {
	        position: 'bottomright'
	    },

	    onAdd: function (map) {
	        // create the control container with a particular class name
	        var container = L.DomUtil.create('div', 'esri-legend-control');
	        var lc = L.DomUtil.create('div', 'esri-legend-top', container );
	        var legendClose = L.DomUtil.create('div', 'esri-legend-close', lc );
	        var legendContainer = L.DomUtil.create('div', 'esri-legend-list', container ); 
	        $(legendContainer).html($.templates('#esri_legend_template').render({data:this.options.list}));
	        var _this = this;
	        $(legendClose).on('click', function(){
	        	_map.removeControl(_this);
	        	_esriLegends = null;
	        });
	        return container;
	    }
	});
	
	return {  
  
        //Public variables and methods  
    	initMap : function(embdId){
    		
    		//var rucadastre = new L.RuCadastre();
    		
    		 var ggl = new L.Google("ROADMAP");
             var ggl2 = new L.Google('SATELLITE');
    		_map = L.map('map', {layers: [stdTiles], measureControl: true, permissionControl: getUserPermissionObjectId(), loadingControl: true});
            L.control.mousePosition().addTo(_map);
            L.control.scale({imperial:false}).addTo(_map);
           // _map.addLayer(stdTiles);
            //stdTiles.bringToBack();
           
             //_map.addLayer(ggl);
            L.control.layers( /* option buttons */ 
                    {"Ярославская область" : stdTiles,
                     "Google": ggl,
                     "Google Earth":ggl2}/*,{
                    	 "Публичная кадастровая карта": rucadastre                    	 
                     }*/).addTo(_map); /* checkboxs */
            // Create the print provider, subscribing to print events
            if(!isAnonymous())$.getScript(getSettings()['PRINT_SERVER'] + "/pdf/info.json?var=printConfig").fail(function(jqxhr, settings, exception) {
    			alertMessage("Fail request print server settings!");
    		}).done(function(script, textStatus) {
    			var method = 'GET';
				if(printConfig.createURL.indexOf(window.location.origin) > -1)method = 'POST';
    			var printProvider = L.print.provider({
    				capabilities: printConfig,
    				method: method,
    				dpi: parseInt(getSettings()['PRINT_DPI']),
    				outputFormat: 'pdf',
    				outputFilename: 'geoportal',
    				customParams: {
    					mapTitle: 'Геопортал ЯО',
    					comment: 'Геопортал ЯО'
    				}
    			});
    			var printControl = L.control.print({
    				provider: printProvider
    			});
    			printProvider.on('printexception', function(provider, response){
    				alertMessage( "Request failed: " + response.responseText);
    			});
    			printProvider.on('printarea', function(opts){
    				_ipPopupAllowed = !opts.show;
    			});
    			_map.addControl(printControl);
    		});
            /*
    		 *    http://map.trans-monitor.ru/
    		 * 
                addLayer('enk', 'http://maps.yarcloud.ru:8088/enk/{z}/{x}/{y}.png', 'Map data &copy; 2011 Softcom and OpenStreetMap contributors', 25);
                addLayer('enk2', 'http://maps.yarcloud.ru:8088/enk2/{z}/{x}/{y}.png', 'Map data &copy; 2011 Softcom and OpenStreetMap contributors', 25);
                addLayer('ddh', 'http://maps.yarcloud.ru:8088/ddh1/{z}/{x}/{y}.png', 'Map data &copy; 2011 Softcom and OpenStreetMap contributors', 25);
                addLayer('Борщевик', 'http://maps.yarcloud.ru:8088/brs/{z}/{x}/{y}.png', 'Map data &copy; 2011 Softcom and OpenStreetMap contributors', 25);
                addLayer('Дорожные знаки', 'http://maps.yarcloud.ru:8088/road_signs/{z}/{x}/{y}.png', 'Map data &copy; 2011 Softcom and OpenStreetMap contributors', 25);
                addLayer('Openstreet map', 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', 'Map data &copy; 2011 OpenStreetMap contributors', 18);
                addLayer('raster', 'http://maps.yarcloud.ru:8088/enki/{z}/{x}/{y}.png', 'Map data &copy; 2011 Softcom and OpenStreetMap contributors', 25);
                addLayer('Navipoint', 'http://maps.yarcloud.ru:8088/navipoint/{z}/{x}/{y}.png', 'Map data &copy; 2011 Softcom and OpenStreetMap contributors', 25);
                addLayer('rtk', 'http://maps.yarcloud.ru:8088/rtk/{z}/{x}/{y}.png', 'Map data &copy; 2011 Softcom and OpenStreetMap contributors', 25);
                addLayer('speed', 'http://maps.yarcloud.ru:8088/speed/{z}/{x}/{y}.png', 'Map data &copy; 2011 Softcom and OpenStreetMap contributors', 25);
                addLayer('district', 'http://maps.yarcloud.ru:8088/district/{z}/{x}/{y}.png', 'Map data &copy; 2011 Softcom and OpenStreetMap contributors', 25);
    		 */
            //L.Control.measureControl().addTo(_map);
            var mobj = $('#'+embdId);
            var startupCenter = startPoint;
            var startupZoom = startZoom;
            if(mobj.attr('center') != 'null'){
            	startupCenter = eval('[' + mobj.attr('center') + ']');
    		}else{
    			if($.cookie("center") != undefined){
    				startupCenter = eval('[' + $.cookie("center") + ']');    				
                }
    		}
            if(mobj.attr('zoom') != 'null'){
            	startupZoom = eval(mobj.attr('zoom'));
    		}else{
    			if($.cookie("zoom") != undefined){
    				startupZoom = eval($.cookie("zoom"));
                }
    		}
            _map.setView(startupCenter, startupZoom);
            _map.on('move', function(){
            	var c = _map.getCenter();
            	$.cookie("center", [c.lat, c.lng], { expires : 7 });            	
            });
            _map.on('zoomend', function(){
            	$.cookie("zoom", _map.getZoom(), { expires : 7 });            	
            });            
    		_geoLocation();
    		var searchLayer = new L.FeatureGroup();
    		_map.addLayer(searchLayer);
    		envLayers[_searchLayer] = {layer:searchLayer};
    		var routeLayer = new L.FeatureGroup();
    		_map.addLayer(routeLayer);
    		envLayers[_routeLayer] = {layer:routeLayer};
    		_initEditorEvents();
    		$('#link_section button').button();
    		$('#link_section').click(function(){
    			$('input',this).select();
    		});
    		$('#link_section_close').click(function(event){
    			event.preventDefault();
    			event.stopPropagation();
    			$('#link_section').hide();
    		});
    		$('#link_section_rebuild').click(function(event){
    			event.preventDefault();
    			event.stopPropagation();
    			$('#link_section input').val(_buildLinkUrl()).select();
    		});
    		$('#link_section_open').click(function(event){
    			event.preventDefault();
    			event.stopPropagation();
    			window.open($('#link_section input').val(), '_blank');
    		});
    		//Weather
    		 L.control.weather({
    			// cnt : 5,
           	  lang: "ru",
           	  units: "metric",
           	  position: "bottomright",
           	  template: "<div><div class='weatherIcon'><img src=':iconurl'></div><div>Температура: :temperature°C</div><div>Влажность: :humidity%</div><div>Ветер: :winddirection :windspeed м/с</div></div>",
           	  translateWindDirection: function (text){
           		  switch(text){
           		  	case 'S':
           		  		return 'юж.';
           		  	case 'W':
        		  		return 'зап.';
           		  	case 'N':
        		  		return 'сев.';
           		  	case 'E':
        		  		return 'вост.';
           		  	case 'SW':
        		  		return 'юго-зап.';
           		  	case 'NE':
        		  		return 'сев.-вост.';
           		  	case 'SE':
           		  		return 'юг.-вост.';
        		  	default :
        		  		return text;
           		  }
           	  } 
           	  
           }).addTo(_map);    
    	},
    	onEnvLayer : function (metadata){
    		if(_isEditing != null){
    			alertMessage( "Редактируется другой слой" );
    			return;
    		}
    		_isEditing = metadata.id;
    		// Initialize the FeatureGroup to store editable layers
    		var _featureGroup = envLayers[_isEditing];
    		if(_featureGroup == null){
    			_getDataObjects(_isEditing, function(objects){
    				_featureGroup = _createLayer(_isEditing, objects, metadata);
    				//_map.addLayer(_featureGroup);
            		//envLayers[layerId] = _featureGroup;
            		_setupEditor(_featureGroup);
    			});
    		}else{
    			_setupEditor(_featureGroup.layer);
    		}			
    	},
    	offEditor : function(){
    		_removeEditor();
    	},
    	on : function(eventName, callback){
    		_on(eventName, callback);
    	},
    	off : function(eventName, callback){
    		_off(eventName, callback);
    	},
    	isEditing : function(){
    		return _isEditing != null;
    	},
    	/*moveTo : function(id){
    		for(i in nodes){
    			var node = nodes[i];
    			if(node.dataObj.id == id){
    				map.setView([node.dataObj.lat, node.dataObj.lon], 17); 
    			}
    		}
    	},*/
    	toHome : function(){
    		if(_home != null){
    			_map.setView(_home, startZoom); 
    		}else{
    			confirmMessage('Мое местоположение', 'Желаете определить местоположение?', function(){
    				_setHome();
    			});
    		}
    	},
    	toPoint : function(point){
    		_map.setView(point.latlng);
    		L.marker([point.latlng.lat, point.latlng.lng])
    		.bindPopup(point.text)
    		.addTo(_map);
    	},
    	getHome : function(){
    		return _home;
    	},
    	getRoute : function(){
    		_getMarkerPosition('Начало движения', function(e, obj){
    			var start = e.latlng;
    			if(!obj)_createPoint(_routeLayer, {id:'start', name:'Начало движения', lat:start.lat, lon:start.lng});
    			_getMarkerPosition('Конец движения', function(e, obj){
        			var end = e.latlng;
        			if(!obj)_createPoint(_routeLayer, {id:'end',name:'Конец движения', lat:end.lat, lon:end.lng});
        			var router = L.Routing.osrm();
        			var waypoints = [];
        			waypoints.push({latLng: L.latLng(start.lat,start.lng)});
        			waypoints.push({latLng: L.latLng(end.lat,end.lng)});
        			router.route(waypoints, function(err, routes) {
        	            if (err) {
        	            	alertMessage('Сервис не доступен: ' + err);
        	            } else {
        	            	_makeRoute(routes[0]);
        	            }
        	        });
        		});
    		});    	
    	},
    	hideRoute : function(){
    		envLayers[_routeLayer].layer.clearLayers();
    	},
    	addEnvLayer : function(metadata){
    		envLayers[_searchLayer].layer.clearLayers();
    		if(envLayers[metadata.id])envLayers[metadata.id].layer.clearLayers();
    		var id = metadata.id;
    		var type = metadata.typeId;
    		if(type == 1){
    			_getDataObjects(id, function(objects) {
    				if(objects.length > 5000)alertMessage('Количество объектов > 5000! Возможно загрузка займет больше времени.');
    				_createLayer(id, objects, metadata);
    				//	map.addLayer(fGroup);
    				//	envLayers[id] = fGroup;
    			});
    		}
    		if(type == 3){
    			var l = L.tileLayer(metadata.url, {}).addTo(_map).bringToFront();;
    			envLayers[id] = {layer:l,metadata:metadata};
    		}
    		if(type == 5){
    			var esriLayer = L.esri.dynamicMapLayer(metadata.url, {
    				transparent : true,
    			    useCors: false,
    			    format: 'image/png',
    			    returnGeometry: false
    			}).addTo(_map);
    			envLayers[id] = {layer:esriLayer,metadata:metadata};
    			esriLayer.bindPopup(function (error, featureCollection, results) {
    			    if(!_ipPopupAllowed)return;
    				if(error || featureCollection.features.length === 0) {
    			      return false;
    			    } else {
    			    	if(metadata.tmpl){
    			    		console.log(results);
    			    		return $.templates({markup:metadata.tmpl, allowCode: true }).render({features:results.results});
    			    	}else{
    			    		return featureCollection.features[0].properties.name;
    			    	}
    			    }
    			  });
    		}
    		if(type == 2){
    			//TODO make parameters
    			var wmsUrl = metadata.wmsUrl.replace(/([^?]*)\?(.*)/,'$1');
    			var wmsLayer =  url('?LAYERS', metadata.wmsUrl);
    			var wmsSrs =  url('?SRS', metadata.wmsUrl);
    			var wms = L.tileLayer.wms(wmsUrl + '?f=image', {
        		    format: 'image/png',
        		    transparent: true,
        		    attribution: "wms",
        		    //crs: (null == wmsSrs)?L.CRS.Simple:wmsSrs,
        		    layers: (null == wmsLayer)?'':wmsLayer        		    
        		}).addTo(_map).bringToFront();
    			envLayers[id] = {layer:wms,metadata:metadata};
    		}
			if(type == 6){
				var options = {'transparent': true, crs: L.CRS.EPSG4326};
				var wmsLayer = L.WMS.source("wms/", options);
				var buttons = {};
				
				if((metadata && metadata.permissions == 1)  || isEditor())buttons["Сохранить"] = function(){
					showMask();
					$.post( 
						'geo/update', 
						$('#_ObjectEditForm_').serialize(),
						'json' // I expect a JSON response
					).done(function(res){
						hideMask();
						$('#pageDialog').dialog("close");
						alertMessage("Данные сохранены успешно.");
					}).fail(function(){
						hideMask();
						 alertMessage("Ошибка сохранения.");
					});
				};
				var createContent = function(id, content, latlng){
					if((metadata && metadata.permissions == 1) || isEditor()){
						content += "<div><a class='opengis' href='#'>Открыть</a></div>";
					}
					var pp = L.popup().setLatLng(latlng).setContent(content).openOn(_map);
					
					$(pp._container).delegate('a.opengis', 'click', function(){
						showMask();
						$.get('geo/getdialog/' + id, function(phtml) {
							hideMask();
							$.showPageDialog("Объект", phtml, buttons, {width: 600, maxHeight: $(document).height()-70});
							$('#tobuffer').button().click(function(){
								var popupWin = window.open('about:blank', '_blank', "location,width=400,height=300,top=0");
								var win_doc = popupWin.document;
								win_doc.open();
								win_doc.write($('.obj_geom')[0].innerHTML);
								win_doc.close();
								popupWin.focus(); // передаём фокус новому окну
							});
						});
						_map.closePopup(pp);
					});
				};
				wmsLayer.ajax = function(url, callback) {
					$.ajax(url.replace("wms/", "wms/identify/"), {
						'context': this,
						'success': function(result) {
							if(result == ''){
								callback.call(this, null);
								return;
							}
							callback.call(this, result);
						 }
					});
				}; 
				wmsLayer.showFeatureInfo = function(latlng, info){
					var content = info.name;
					if(metadata.tmpl){
						console.log(info);
						for(var p in info.tags){
							info[info.tags[p].key] = info.tags[p].value; 
						}
						content = $.templates({markup:metadata.tmpl, allowCode: true }).render(info);
					}
					createContent(info.id, content, latlng);
				};
				wmsLayer.addSubLayer(id);
				wmsLayer.addTo(_map);
				envLayers[id] = {layer:wmsLayer,metadata:metadata};
				
			}
    	},
    	removeEnvLayer : function(id){
    		if(_isEditing == id)_removeEditor();
    		if(envLayers[id]){
    			_map.removeLayer(envLayers[id].layer);
    			_stopLoadingShapes(id);
    			delete envLayers[id];
    		}
    	}, 
    	fitToObject : function(id){
    		_findById(id, true, function(obj){
    		if(obj != null){
    			var ll = obj.getLatLng;
    			var b = obj.getBounds;
    			if(ll){_map.panTo(obj.getLatLng());obj.fire('click', {latlng:obj.getLatLng()});}
    			if(b){_map.fitBounds(obj.getBounds());}
    			return;
    		}
    		$.ajax({
    			type: "GET",
    			url: "geo/get/"+id, 
    			contentType: "application/json"
    		}).done(function(data) {
    			if (data.objectGeomType == 'Point'){
    				_createPoint(_searchLayer, data);
    				_findById(id, false, function(newObj){
    					_map.panTo(newObj.getLatLng());
    					newObj.fire('click', {latlng:newObj.getLatLng()});
    				});
    			}else{
    				_createShape(_searchLayer, data, true);
    			}
    			_fireEvent('found_hidden', {id:id}, data);
    		}).fail(function(jqXHR, textStatus) {
    			alertMessage( "Request failed: " + textStatus );
    		});
    		});
    	},
    	distinguish : function(id){
    		_findById(id, false, function(obj){
    		if(obj != null){
    			//obj.fire('click', {latlng:obj._latlng});
    			try{
    				obj.setStyle({'weight': obj.options.weight*2});
    			}catch(e){}
    		}
    		});
    	},
    	undistinguish : function(id){
    		_findById(id, false, function(obj){
    		if(obj != null){
    			//obj.fire('click', {latlng:obj._latlng});
    			try{
    				obj.setStyle({'weight': obj.options.weight/2});
    			}catch(e){}
    		}});
    	},
    	getLink : function(){
    		$('#link_section').show();
    		$('#link_section input').val(_buildLinkUrl()).select();
    	},
    	getWidget : function(){
    		var url = window.location.origin + window.location.pathname + 'widget/?';
    		var c = _map.getCenter();
        	url += 'lat=' + c.lat + '&lon=' + c.lng;
        	url += '&z=' + _map.getZoom();
    		var l = layers.getSelectedWithData();
    		var lids = [];
    		for(var inx in l){
    			//if(l[inx].metadata.typeId == 1)v.push(l[inx].metadata.id);
    			lids.push(l[inx].metadata.id);
    		}
    		if(lids.length > 0)url += '&layers=' + lids.join();
    		url += '&origin=' + window.location.origin + window.location.pathname;
    		//if(t.length > 0)url += '&tlayers=' + t.join();
    		var ver = 1;
			if($.cookie("wms_version") != undefined){
				ver = eval($.cookie("wms_version"));
				$.cookie("wms_version", ++ver);
            }
			url += '&version=' + ver;
    		window.open(url);
    	},
    	openWMS : function(layerIds){
    		if(_wmsLayer != null)_map.removeLayer(_wmsLayer);
    		var ver = 1;
			if($.cookie("wms_version") != undefined){
				ver = eval($.cookie("wms_version"));
            }
    		_wmsLayer = L.tileLayer.wms('wms/', {
    		    layers: layerIds,
    		    format: 'image/png',
    		    transparent: true,
    		    attribution: "wms",
    		    version: ver,
    		    crs: L.CRS.Simple
    		}).addTo(_map).bringToFront();
    	},
    	openPermissionArea : function(){
    		if(_wmsPermLayer != null)_map.removeLayer(_wmsPermLayer);
    		var ver = 1;
			if($.cookie("wms_version") != undefined){
				ver = eval($.cookie("wms_version"));
            }
    		_wmsPermLayer = L.tileLayer.wms('wms/permObj/', {
    		    format: 'image/png',
    		    transparent: true,
    		    attribution: "wms",
    		    version: ver,
    		    crs: L.CRS.Simple
    		}).addTo(_map).bringToFront();
    	},
    	closePermissionArea: function(){
    		if(_wmsPermLayer != null)_map.removeLayer(_wmsPermLayer);
    	},
    	clearWMS : function(layerIds){
    		$.ajax({
    			type: "POST",
    			url: "wms/reset",
    			data: {LAYERS:layerIds} 
    		}).done(function() {
    			alertMessage( "WMS cache cleared");
    			var ver = 1;
    			if($.cookie("wms_version") != undefined){
    				ver = eval($.cookie("wms_version"));
                }
    			$.cookie("wms_version", ++ver);
    		}).fail(function(jqXHR, textStatus) {
    			alertMessage( "Request failed: " + textStatus );
    		});
    	},
    	addToLayer : function(objId, select){
    		if(select.val() == ''){
    			alertMessage( 'Выберите слой' );
    			return;
    		}
    		var label = $('option[value='+select.val()+']', select)[0].innerHTML; 
    		$.ajax({
    			type: "POST",
    			url: "geo/add_to_layer",
    			data: {layerId:select.val(), objId:objId} 
    		}).done(function() {
    			alertMessage( "Объект успешно добавлен к слою '"+label+"'");
    		}).fail(function(jqXHR, textStatus) {
    			alertMessage( "Ошибка добавления: " + textStatus );
    		});
    	},
    	clear : function(){
    		_map.eachLayer(function (layer) {
    			if(layer == envLayers[_searchLayer].layer){
    				_map.removeLayer(layer );
    				var searchLayer = new L.FeatureGroup();
    	    		_map.addLayer(searchLayer);
    	    		envLayers[_searchLayer] = {layer:searchLayer};
    				return;
    			}
    			if(layer == envLayers[_routeLayer].layer)return;
    			if(layer == _homeMarker)return;
    			_map.removeLayer(layer );
    		});
    		stdTiles.addTo(_map);
    	},
    	showEsriLegend : function(lId, legends){
    		if(_esriLegends != null)_map.removeControl(_esriLegends);
    		_esriLegends = new _EsriLegendControl({list: legends, layerId : lId}); 
    		_map.addControl(_esriLegends);
    	}
    };  
})();  
