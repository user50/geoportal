(function(){
 
    var special = jQuery.event.special,
        uid1 = 'D' + (+new Date()),
        uid2 = 'D' + (+new Date() + 1);
 
    special.scrollstart = {
        setup: function() {
 
            var timer,
                handler =  function(evt) {
 
                    var _self = this,
                        _args = arguments;
 
                    if (timer) {
                        clearTimeout(timer);
                    } else {
                        evt.type = 'scrollstart';
                        jQuery.event.handle.apply(_self, _args);
                    }
 
                    timer = setTimeout( function(){
                        timer = null;
                    }, special.scrollstop.latency);
 
                };
 
            jQuery(this).bind('scroll', handler).data(uid1, handler);
 
        },
        teardown: function(){
            jQuery(this).unbind( 'scroll', jQuery(this).data(uid1) );
        }
    };
 
    special.scrollstop = {
        latency: 300,
        setup: function() {
 
            var timer,
                    handler = function(evt) {
 
                    var _self = this,
                        _args = arguments;
 
                    if (timer) {
                        clearTimeout(timer);
                    }
 
                    timer = setTimeout( function(){
 
                        timer = null;
                        evt.type = 'scrollstop';
                        jQuery.event.handle.apply(_self, _args);
 
                    }, special.scrollstop.latency);
 
                };
 
            jQuery(this).bind('scroll', handler).data(uid2, handler);
 
        },
        teardown: function() {
            jQuery(this).unbind( 'scroll', jQuery(this).data(uid2) );
        }
    };
 
})();

var helper = (function() {
	//private
	var _id = '';
	var _lt = false;
	var _filter = '';
	var eventListeners = {
		'click' : [],
		'over' : [],
		'out' : [],
		'search_complete' : [],
		'geolocation' : []
	};
	var _data = {};
	var _home = null;
	
	var history = [];
	var _search_mode = 1;
	var _search_paging = null;
	var _saveHistory = function(){
		if(_data != null)history.push(jQuery.extend(true, {}, _data));
		if(history.length > 10)history.shift();
	};
	
	var _resoreFromHistory = function(){
		if(history.length > 0){
			_data = history.pop();
			return true;
		}
		return false;
	};
	
	var _replaceData = function(layerId, data){
		_data = {};
		_data[layerId] = data;
		_refresh();
	};
	
	var _appendData = function(layerId, data){
		if(_data[layerId]){
			_data[layerId] = _data[layerId].concat(data);
		}else{
			_data[layerId] = data;
		}
		_refresh();
	};
	
	var _sliceData = function(layerId){
		delete _data[layerId];
		_refresh();
	};
	
	var _sliceDataByLayer = function(layerId, ids){
		if(_data[layerId]){
			var objs = _data[layerId];
			for(var m = 0; m < objs.length; m++){
	            if(ids.indexOf(objs[m].id) >= 0){
	            	objs.splice(m, 1);
	                m--;/*т.к. длина массива изменяется методом splice на 1*/
	            }
			}
			_data[layerId] = objs;
		}
		_refresh();
	};
	
	var _refresh = function(modyfier){
		var data = [];
		var uniqInx = [];
		for(var i in _data){
			for(var j in _data[i]){
				if(uniqInx.indexOf(_data[i][j].id) < 0){
					data.push(_data[i][j]);
					uniqInx.push(_data[i][j].id);
				}
			}
		}
		var amount = data.length;
		if(modyfier)modyfier(data);
		if(_filter != ''){
			var criteria = new RegExp(_filter + '.*', 'i');
			 for(var m = 0; m < data.length; m++){
		            if(!criteria.test(data[m].name)){
		                data.splice(m, 1);
		                m--;/*т.к. длина массива изменяется методом splice на 1*/
		            }
		     }
		}
		if(data.length > 1000){
			$('#helper_err_mess').show();
			$('#helper_obj_list').html("");
			$('#helper-status').html('0 из '+amount+' объектов');
			return;
		}else{
			$('#helper_err_mess').hide();
		}
		$('#helper_obj_list').html($.templates('#helper_section').render({data:data}/*,
				{ filter: function(arg) {
                    return _filter == '' || new RegExp(_filter + '.*', 'i').test(arg);
                }
       }*/));
		//$('#helper_section').tmpl({data:data, filter:_filter}).appendTo('#helper_obj_list');
		$('.helper_section').off('click').off('mouseover').off('mouseout');
		$('.helper_section').each(function(e){
			$(this).on('click', function(e){
				e.stopPropagation();
				var id = this.id.substr(5);
				_fireEvent('click', e, id);
			}).on('mouseover', function(e){
				e.stopPropagation();
				var id = this.id.substr(5);
				_fireEvent('over', e, id);
			}).on('mouseout', function(e){
				e.stopPropagation();
				var id = this.id.substr(5);
				_fireEvent('out', e, id);
			});
			
		});
		if(data.length == amount){
			$('#helper-status').html(data.length + ' объектов');
		}else{
			$('#helper-status').html(data.length + ' из '+amount+' объектов');
		}
	};
	
	var _sortByName = function(){
		_refresh(function(data){
			function asc( a, b ) {
				if(a.name == b.name)return 0;
				return (a.name > b.name)?1:-1;
			}
			data.sort( asc ); 
		});
	};
	
	var _sortByFar = function(){
		_refresh(function(data){
			if(_home != null){
				function asc( a, b ) {
					var f1 = _home(a);
					var f2 = _home(b);
					return f1 - f2;
				};
				data.sort( asc ); 
			}			
		});
	};
	
	var _fireEvent = function(eventName, event, dataObj){
		var listeners = eventListeners[eventName];
		for(var l in listeners){
			setTimeout((function(listener){
				return function(){
					listener(event, dataObj);
				}
			})(listeners[l]),1);
		}
	};
	
	var _ajaxSearch = function(search, _search_mode, _search_paging, fias, latlng){
		showMask();
		$.ajax({
	    	url : 'helper/search',
			type : 'GET',
			data: {q:search, m:_search_mode, p : _search_paging, fias: fias, lat: latlng.lat, lng:latlng.lon},
			contentType : 'application/json'
		}).done(function(data) {
			hideMask();
			_fireEvent('search_complete', {}, data);
			if(data.length == 0){
				alertMessage( "Объектов не найдено");
				_search_paging = null;
				_saveHistory();
				_sliceData(-1);
				return;
			}
			_saveHistory();
			_replaceData(-1,data);
		}).fail(function(jqXHR, textStatus) {
			  hideMask();
			  alertMessage( "Request failed: " + textStatus );
		});
	};
	
	var _search = function(search, fias){
		_search_paging = (_search_paging == null)?0:_search_paging+1;
		//geocoder search
		if(_search_mode == 2){
			$.ajax({
			    type: 'GET',
			    url: 'http://search.maps.sputnik.ru/search',
			    dataType: 'jsonp',
			    data: {q:search, limit:1},
			    success: function(result){
			    	console.log(result);
					_ajaxSearch(search, _search_mode, _search_paging, fias, result.result[0].position);
					_fireEvent('geolocation', {}, {results : [{latlng :{lat:result.result[0].position.lat, lng:result.result[0].position.lon}}]});
			    }
			});
			
			/*L.esri.Geocoding.Tasks.geocode().text(search).run(function(err, results, response){
					console.log(results);
					_ajaxSearch(search, _search_mode, _search_paging, fias, results.results[0].latlng);
					_fireEvent('geolocation', {}, results);
			});*/
		}else{
			_ajaxSearch(search, _search_mode, _search_paging, null, {});
		}
		//-------------
		
	};
	
	return {
		//public
		init : function(id, homeFunc){
			_home = homeFunc;
			_id = id;
			$("<div/>").appendTo("body").load( "helper/", function() {
				_lt = true;
				$('#'+_id).html($.templates('#helper_body').render());
				//$('#helper_body').tmpl().appendTo('#'+_id);
				$( "#helper_filter" ).keyup(function() {
					_filter = $(this).val();
					_refresh();
				});
				$( "#helper_search" ).on('keypress', function(e){
					if(e.keyCode == 13){
						_search($(this).val(), $('#helper_fias_search').val());
					}
				});
				$( "#search" ).on('click', function(e) {
					_search($("#helper_search").val(), $('#helper_fias_search').val());					
				});
				$('#by_name').click(function(){
					_sortByName();
				});
				$('#by_far').click(function(){
					_sortByFar();
				});
				$('#history_back').click(function(){
					if(_resoreFromHistory())_refresh();
				});
				$('#by_tags').click(function(){
					_search_mode = 1;
                    autocompleteLib.removeAutocomplete($('#helper_search'));
					$('#by_tags').css('border-bottom','1px dotted #858383');
					$('#by_addr').css('border-bottom','none');
				});
				$('#by_addr').click(function(){
					_search_mode = 2;
					autocompleteLib.setAutocompleteFiasFull($('#helper_search'), $('#helper_fias_search'), null);
					$('#by_addr').css('border-bottom','1px dotted #858383');
					$('#by_tags').css('border-bottom','none');
				});
				/*$('#helper_obj_list').bind('scrollstop', function(e){
					if($(this).scrollTop() == $(this).context.scrollHeight - $(this).height() && _search_paging != null)
						_search($("#helper_search").val(), $('#helper_fias_search').val());
				});*/
			});
		},
		/*replaceData : function(data){
			if(_lt == false){
				setTimeout(function(){arguments.callee(data)}, 100);
			}else{
				_search_paging = null;
				_saveHistory();
				_replaceData(data);
			}
			onResize();
		},*/
		addData : function(layerId, data){
			if(_lt == false){
				setTimeout(function(){arguments.callee(layerId, data)}, 100);
			}else{
				_search_paging = null;
				//_saveHistory();
				_sliceData(-1);
				history = [];
				_appendData(layerId, data);
			}
			onResize();
		},
		removeData : function(layerId){
			if(_lt == false){
				setTimeout(function(){arguments.callee(layerId)}, 100);
			}else{
				_search_paging = null;
				//_saveHistory();
				_sliceData(layerId);
			}
			onResize();
		},
		removeDataByIds : function(layerId, ids){
			if(_lt == false){
				setTimeout(function(){arguments.callee(layerId, ids)}, 100);
			}else{
				_search_paging = null;
				//_saveHistory();
				_sliceDataByLayer(layerId, ids);
			}
			onResize();
		},
		on : function(eventName, callback){
    		eventListeners[eventName].push(callback);
    	},
    	onResize : function(w,h){
    		if(_id == null)return;
    		$('#'+_id).height(h-105);
    		$('#helper_obj_list').height(h-350);
    	},
    	clear : function(){
    		_data = {};
    		_refresh();
    	}
	};	
})();