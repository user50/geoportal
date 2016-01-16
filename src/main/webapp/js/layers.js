var layers = (function(){
    //private
    
    var el = null;
    
    var _noevents_ = false;
    
    var eventListeners = {
            'select' : [],
            'deselect' : [],
            'edit' : [],
            'esri_legend' : []
        };
    var _startuplayers = [];
    
    var _indx = 0;
  
    var _generateContextItems = function(obj){
    	var node = this.get_json ( obj )[0];
    	var _this = this;
    	var context = {};
    	context.rename = false;
    	if(isEditor() || node.metadata.permissions == 1){
    		context.create = {
    				"label" : "Создать слой",
                	"action" : function(obj){
                		_openForm(null, node.metadata.id);
                		return true;
                	}	
    		};
    	}else{
    		context.create = false;
    	}
    	context.open = {
    			 "label"				: "Свойства",
                 "action"			: function () {
                     _openForm(node.metadata);
                 }	
    	};
    	if((isEditor() || node.metadata.permissions == 1) && node.metadata.typeId == 1){
    		context.edit = {
    				"label"				: "Редактировать на карте",
                    "action"			: function(){
                        _noevents_ = true;
                        this.check_node(obj);
                        _noevents_ = false;
                        _fireEvent('edit', node, node.metadata);
                    }
    		};
    	}else{
    		context.edit = false;
    	}
    	if((isEditor() || node.metadata.permissions == 1) && node.metadata.typeId == 1){
    		context.massedit = {
    				"label"				: "Редактировать в таблице",
                    "action"			: function(){
                        window.open("mass/edit/"+ node.metadata.id,  '_blank');
                    },
                    "_disabled"			: (node.metadata.haveObjects == 0)
    		};
    	}else{
    		context.massedit = false;
    	}
    	if(isEditor() || node.metadata.permissions == 1){
    		context.remove = {
    				"label"				: "Удалить слой без объектов",
                    "action"			: function(){
                        //var _this = this;
                        setTimeout(function(){
                            confirmMessage("Снять регистрацию", 'Вы действительно желаете удалить слой '+_this.get_text(obj)+'?', function(){
                                if(_this.is_selected(obj)) { _this.remove(); } else { _this.remove(obj); }
                            });
                        }, 1);
                    }	
    		};
    	}else{
    		context.remove = false;
    	}
    	if(isAdmin() || node.metadata.permissions == 1){
    		context.rigths = {
    				 "label"             : "Права доступа",
    	             "action"            : function(obj){
    	                 _editPermissions(node.metadata.id);
    	             }	
    		};
    	}else{
    		context.rigths = false;
    	}
    	context.ccp = false;
    	if((isEditor() || node.metadata.permissions == 1 || isAdmin()) && node.metadata.typeId == 1){
    		context['clear'] = {
   				 "label" : "Удалить все объекты",
   				 "action"			: function(){
                    setTimeout(function(){
                        confirmMessage("Очистить слой", 'Вы действительно желаете удалить все объекты из '+_this.get_text(obj)+'?', function(){
                        	showMask();
                            $.post( 
                                'geo/gis_objects_delete_by_layer', 
                                {layerId:node.metadata.id}
                            ).done(function(res){
                                hideMask();
                                try{
                                    alertMessage("Объекты удалены.");
                                    _fireEvent('deselect', res, node.metadata);
                                }catch(e){
                                    alertMessage("Ошибка удаления.");
                                }
                                
                            }).fail(function(jqXHR, textStatus) {
                    			hideMask();
                    			alertMessage( "Ошибка при удалении: " + textStatus );
                    		});	;
                        });
                    }, 1);
   				 },
                 "_disabled"			: (node.metadata.haveObjects == 0)	
    		};
    	}
    	
    	if((isEditor() || node.metadata.permissions == 1 || isAdmin()) && node.metadata.typeId == 1){
    		context['export'] = {
    				 "label" : "Экспорт",
    				 "action"            : function () {
                     	_openExportForm(node.metadata);
                     },
                     "_disabled"			: (node.metadata.haveObjects == 0)
    	                /*"submenu":{
    	                    "exportMifMid" : {
    	                        "action" : function(){
    	                            location.href="export/layer/?id="+ node.metadata.id +"&format=MIFMID";
    	                        },
    	                        "label" : "Экспорт в .MIF/.MID"
    	                    },
    	                    "exportCsv" : {
    	                        "action" : function(obj){
    	                            location.href="export/layer/?id="+ node.metadata.id +"&format=CSV";
    	                        },
    	                        "label" : "Экспорт в .CSV"
    	                    },
    	                    "exportTxt" : {
    	                        "action" : function(obj){
    	                            location.href="export/layer/?id="+ node.metadata.id +"&format=TXT";
    	                        },
    	                        "label" : "Экспорт в .TXT"
    	                    }
    	                }*/
    		};
    	}else{
    		context['export'] = false;
    	}
    	if((isEditor() || node.metadata.permissions == 1 || isAdmin()) && node.metadata.typeId == 1){
    		context['import'] = {
    				"label" : "Импорт",
                    "action" : false,
                    "submenu":{
                        "fileImport" : {
                            // The item label
                            "label"             : "Импорт из файлов",
                            "action"            : function () {
                            	_openImportForm(node.metadata);
                            }
                        },
                        "inforImport" : {
                            // The item label
                            "label"             : "Импорт объектов из старой СУПД",
                            "action"            : function (obj) {
                                window.open("infor/?layerId="+ node.metadata.id,  '_blank');
                            }
                        }
                    }	
    		};
    	}else{
    		context['import'] = false;
    	}
    	if(node.metadata.typeId == 5){
    		context['legend'] = {
    				"label" : "Показать легенду",
      				 "action"			: function(){
      					$.ajax({
      					    type: 'GET',
      					    url: node.metadata.esriUrl + '/legend',
      					    dataType: 'jsonp',
      					    data: {f:'pjson'},
      					    success: function(result){
      					    	console.log(result);
      					    	if(result.layers){
      					    		_fireEvent('esri_legend', node, result.layers);
      					    	}else{
      					    		 alertMessage("Невозможно отобразить легенду!");
      					    	}
      					    }
      					});
      				 }
    		};
    	}else{
    		context['legend'] = false;
    	}
    	return context;
    };
    
    var _init = function(obj){
        if(obj.attr('layers') != 'null'){
            _startuplayers = obj.attr('layers').split(',');
        }
        obj.jstree({  
            "core": {"html_titles": true},
            "plugins": ["themes", "json_data", "checkbox", "ui", "sort", "contextmenu", "crrm", "cookies"],
            "json_data": {
              "ajax": {
                "url": "layer/",
                "data": function (n) {
                  return {"id": n.attr ? n.attr("id") : ''};
                },
                "success": function (d){
                    var root = {attr:{id:null}};
                    var createNodesList = function(inx, n, parentNode){
                        var node = {"data" : {'title': '<span>'+n.name+'</span>'}, 
                                "attr" : { "id": n.id},
                                "metadata" : n
                        };
                        node.data.icon = 'layer/treeicon/' + n.id;
                        /*if(n.typeId == 1)node.data.icon = 'css/themes/classic/vector.png';
                        if(n.typeId == 3)node.data.icon = 'css/themes/classic/tile.png';
                        if(n.typeId == 2)node.data.icon = 'css/themes/classic/wms.png';
                        if(n.typeId == 5)node.data.icon = 'css/themes/classic/esri.png';*/
                        if(n.typeId == 1 && n.haveObjects == 0){
                        	node.attr['class'] = 'no-checkbox';
                        }
                        if(parentNode.attr.id == n.parentId){
                        	if(!parentNode.children)parentNode.children = [];
                        	$.each(d, function(i, _n){
                        		createNodesList(i, _n, node);
                        	});
                        	parentNode.children.push(node);
                        	if(_startuplayers.indexOf(''+n.id) > -1){
                                node.attr['class'] = "jstree-checked";
                                _fireEvent('select', {}, n);
                            }
                        }
                    };
                    
                    $.each(d, function(inx, n){
                    	createNodesList(inx, n, root);
                    });
                    return root.children;
                }
              }
            },
            "themes": {
              "theme": "classic",
              "dots": true,
              "icons": true
            },
            "checkbox": {
                "real_checkboxes": true,
                "two_state" : true
            },
            "contextmenu": {
                "items" : _generateContextItems
            }
          }).bind("change_state.jstree", function (event, data) {
        	  var node = data.inst.get_json(data.args[0])[0];
              if(data.args[1]){
                  _fireEvent('deselect', event, node.metadata);                  
              }else{
                  _fireEvent('select', event, node.metadata);
              }
          }).bind("remove.jstree", function(event, data){
              var node = data.inst.get_json(data.rslt.obj[0])[0];
                $.ajax({
                    url : 'layer/remove/' + node.metadata.id,
                    type : 'GET'
                }).done(function(data) {
                    alertMessage( "Регистрация снята");
                }).fail(function(jqXHR, textStatus) {
                    alertMessage("Произошла ошибка" );
                });
          }).bind("dblclick.jstree", function(data){
        	  var treeObj = $(this);
        	  if(treeObj.jstree('is_open',  data[0])){
        		  treeObj.jstree('close_node', data[0]); 
        	  }else
        	  if(treeObj.jstree('is_close',  data[0]) && !treeObj.jstree('is_leaf', data[0])){
        		  $(this).jstree('open_node', data[0]); 
        	  }
          }).bind("loaded.jstree", function (event, data) {
        	  var _this = data.inst;
        	  for(var inx in _startuplayers){
        		  var coll = _this._get_settings().checkbox.two_state ? _this.get_container_ul().find("li") : _this.get_container_ul().children("li");
                  coll.each(function (i, li) {
                      var node = _this.get_json ( li )[0];
                      if('' + node.metadata.id == _startuplayers[inx]){
                    	  var parent = _this._get_parent ( li );
                    	  if(parent[0] != null)_this.open_node ( parent[0] , false , false );
                      }
                  });
        	  }
          });
    };
    
    
    var _fireEvent = function(eventName, event, dataObj){
        if(_noevents_)return;
        var listeners = eventListeners[eventName];
        for(var l in listeners){
            setTimeout((function(listener){
                return function(){
                    listener(event, dataObj);
                };
            })(listeners[l]),1);
        }
    };
    
    var _openExportForm = (function(){
    	
    	var buttons = { "Экспорт": function(){
    		
    		var ref = $('#export_ref').val();
    		var format = $('#export_type').val();
    		var id = $('#export_id').val();
    		if(!ref || !format){
    			$('#pageDialog #exportErrorMesg').html("Ошибка. Требуется выбрать параметры.");
    			return;
    		}
    		
    		location.href="export/layer/?id="+ id +"&format=" + format + "&srs=" + ref;
    	}};
    	
    	 var draw = function(layer){
             var content = $("<div/>");
             content.html($.templates(tmpl).render(layer));
             $.showPageDialog("Экспорт", content, buttons);
         }; 
    	
    	 var tmpl = null;

         return function(layer){
             if(! tmpl){
                 showMask();
                 $.get('export/dialog', function(phtml) {
                     hideMask();
                     tmpl = phtml;
                     draw(layer);
                 });
             }else{
                 draw(layer);
             }
         };
    })();
    
    
    var _openImportForm = (function(){

        var buttons = { "Импорт": function(){
            showMask();
            var formData = new FormData($('#__ImportToLayer__')[0]);
            var progressView = function(data){
            	if(data.loaded < 0){
            		hideMask();
            		$('#pageDialog #importErrorMesg').html("Ошибка импорта. Внутренняя ошибка сервера.");
                    alertMessage("Ошибка импорта.");
            		return;
            	}
            	$('progress').attr({value:data.loaded,max:data.total});
            	if(data.loaded == data.total){
            		hideMask();
            		return;
            	}
            	$.ajax({
            		url: 'import/layerstatus/',
            		type: 'GET',
            		success: function(ret){
            			setTimeout(function(){progressView(ret)}, 1000);
            		},
            		error: function(ret){
            			$('#pageDialog #importErrorMesg').html("Ошибка импорта. Внутренняя ошибка сервера. <a id='importErrorMesgDetails'>Подробнее...</a>");
                        $('#pageDialog #importErrorMesg #importErrorMesgDetails').click(function(){alert(ret.responseText);});
                        alertMessage("Ошибка импорта.");
            		},
            		cache: false            	
            	});
            	
            };
            
            $.ajax({
                url: 'import/layer/',
                type: 'POST',
                /*xhr: function() {  // Custom XMLHttpRequest
                    var myXhr = $.ajaxSettings.xhr();
                    if(myXhr.upload){ 
                        myXhr.upload.addEventListener(
                                'progress', 
                                function(e){if(e.lengthComputable){$('progress').attr({value:e.loaded,max:e.total});}}, 
                                false); // For handling the progress of the upload
                    }
                    return myXhr;
                },*/
                beforeSend: false,
                success: function(data){
                	setTimeout(function(){progressView(data)}, 1000);
                },
                error: function(data){
                    hideMask();
                    $('#pageDialog #importErrorMesg').html("Ошибка импорта. Внутренняя ошибка сервера. <a id='importErrorMesgDetails'>Подробнее...</a>");
                    $('#pageDialog #importErrorMesg #importErrorMesgDetails').click(function(){alert(data.responseText);});
                    alertMessage("Ошибка импорта.");
                },
                // Form data
                data: formData,
                //Options to tell jQuery not to process data or worry about content-type.
                cache: false,
                contentType: false,
                processData: false
            });
        }};

        var draw = function(layer){
            var content = $("<div/>");
            //$.tmpl(tmpl, layer, {}).appendTo(content);
            content.html($.templates(tmpl).render(layer));
            $.showPageDialog("Импорт", content, buttons);
        }; 

        var tmpl = null;

        return function(layer){
            if(! tmpl){
                showMask();
                $.get('import/dialog', function(phtml) {
                    hideMask();
                    tmpl = phtml;
                    draw(layer);
                });
            }else{
                draw(layer);
            }
        };
    })();
    
    var _openForm = (function(){
        
        var buttons = { "Сохранить": function(){
            showMask();
            $.post( 
                'layer/createOrUpdate', 
                $('#_LayerEditForm_').serialize(),
                'json' // I expect a JSON response
            ).done(function(res){
                hideMask();
                try{
                    id = parseInt(res);
                    alertMessage("Данные сохранены успешно.");
                    $('#pageDialog').dialog("close");
                    $.jstree._reference(el).uncheck_all();
                    $.jstree._reference(el).refresh ();
                }catch(e){
                    alertMessage("Ошибка сохранения.");
                }
                
            });
        }};
        
        return function(layer, parentId){
            showMask();
            var id = -1;
            if(!! layer){
                id = layer.id;
            }
            $.get('layer/get/' + id,{parentId:parentId}, function(phtml) {
                hideMask();
                $.showPageDialog("Слой", phtml, (id == -1 || layer.permissions == 1  || isEditor())?buttons:false, {height: $(document).height()-20, width: 900});
                //if(parentId)$('input[name=parentId]').val(parentId);
                $('input[name=typeId]').click(function() {
                        var val = $(this).val();
                        $("div[type=tab]").each(function(i, tab){
                        	if($(tab).attr('id') == 'tabs-'+val){
                        		$(tab).show();
                        	}else{
                        		$(tab).hide();
                        	}
                        });
                });
                /*$(".tagKey").each(function(i2, obj){
                    $(obj).removeClass('tagKey');
                    var s = $(obj).attr('name').substring("layerTags[".length);           
                    var indx = s.substring(0, s.length - 5);
                    var value = $(obj).val();
                    
                    autocompleteLib.setAutocompleteKey(obj);
                    if(autocompleteLib.isFiasField(value)){
                        autocompleteLib.setAutocompleteFias($('input[name="layerTags['+ indx +'].value"]'), obj);
                    }else{
                        autocompleteLib.setAutocompleteValue($('input[name="layerTags['+ indx +'].value"]'), obj);
                    }
                });
                */
                // initialize number fields
               // $(".numberClass").numeric({decimal: "," });
                
                $(".LayerLineColor__").minicolors({
                    control: $(this).attr('data-control') || 'hue',
                    defaultValue: $(this).attr('data-defaultValue') || '',
                    inline: $(this).attr('data-inline') === 'true',
                    letterCase: $(this).attr('data-letterCase') || 'lowercase',
                    opacity: $(this).attr('data-opacity'),
                    position: $(this).attr('data-position') || 'bottom left',
                    change: function(hex, opacity) {
                    },
                    theme: 'default'
                });
                $('#tmpl_editor').click(function(){
                	var tId = $('#tmplId').val();
                	window.open('tmpl/'+tId, '_blank');
                });
                $('#tmpl_editor1').click(function(){
                	var tId = $('#esriTmplId').val();
                	window.open('tmpl/'+tId, '_blank');
                });
                var rO = false;
                if($.cookie("layer" + id + "like"))rO = true;
                $('#stars').raty({readOnly: rO, score: $('#stars').attr('likes'),  path: 'css/images', 
                	click: function(score, evt) {
                			$.cookie("layer" + id + "like", true);
                    		 $.post( 
                    				 'layer/setLike',
                    				 {id:id, likes:score}
                    	     ).done(function(res){
                    	    	 alertMessage("Ваш голос учтен!");      
                    	     });
                    	
                	}
                });
            });
        };
    })();
    
    var _editPermissions = (function(){
        
        var buttons = {"Сохранить" : function(){
            showMask();
            $.post( 
                'layer/permissions/update', 
                $('#_PermissionsForm_').serialize(),
                'json' // I expect a JSON response
            ).done(function(res){
                hideMask();
                try{
                    alertMessage("Данные сохранены успешно.");
                    $('#pageDialog').dialog("close");
                }catch(e){
                    alertMessage("Ошибка сохранения.");
                }
                
            });
        }};
        
        return function(layerId){
            showMask();
            $.get('layer/permissions/' + layerId, {}, function(phtml) {
                hideMask();
                $.showPageDialog("Права доступа", phtml, buttons, {maxHeight: $(document).height()});
            });
        };
    })();
    
    return {
        //public
        init: function(embdId){
            $.jstree._themes = "css/themes/";
            el = $('#' + embdId);
            _init(el);			
        },
        on : function(eventName, callback){
            eventListeners[eventName].push(callback);
        },
        onResize : function(w, h){
            if(el != null){
            	if(isEditor()){
            		el.css('height', h-207);
            	}else{
            		el.css('height', h-155);
            	}
            }
        },
        openForm : function(id){
            _openForm(id);
        },
        selectLayer: function(id){
            var _this = $.jstree._reference(el); 
            var coll = _this._get_settings().checkbox.two_state ? _this.get_container_ul().find("li") : _this.get_container_ul().children("li");
            coll.each(function (inx, li) {
                var node = _this.get_json ( li )[0];
                if(node.metadata.id == id)_this.check_node(li);
            });
        },
        getSelected: function(){
            var _this = $.jstree._reference(el); 
            var coll = _this._get_settings().checkbox.two_state ? _this.get_container_ul().find("li") : _this.get_container_ul().children("li");
            var c = [];
            coll.each(function (inx, li) {
                if(_this.is_checked(li))c.push(li.id);
            });
            return c.join();
        },
        getSelectedWithData: function(){
            var _this = $.jstree._reference(el); 
            var coll = _this._get_settings().checkbox.two_state ? _this.get_container_ul().find("li") : _this.get_container_ul().children("li");
            var c = [];
            coll.each(function (inx, li) {
                if(_this.is_checked(li)){
                	var node = _this.get_json ( li )[0];
                	c.push(node);
                }
            });
            return c;
        },
        reset : function(){
            $.jstree._reference(el).uncheck_all();			
        },
        reloadTree : function(){
            $.jstree._reference(el).uncheck_all();
            $.jstree._reference(el).refresh ();  
       },
       setTagInx : function(indx){
           _indx = indx;
       },
       addTag: function(){
           //$('#LayerTagsTmpl').tmpl([{index: _indx}]).appendTo('#LayerTagsTable');
           $('#LayerTagsTable').append($.templates('#LayerTagsTmpl').render([{index: _indx}]));
           $(".tagKey").each(function(i, obj){
               $(obj).removeClass('tagKey');
               var s = $(obj).attr('name').substring("layerTags[".length);           
               var indx = s.substring(0, s.length - 5);
               autocompleteLib.setAutocompleteKey(obj, false);
               autocompleteLib.setAutocompleteValue($('input[name="layerTags['+ indx +'].value"]'), obj);
           });
          _indx++;
       },
       deleteTag: function(index1){
           $('#LayerTagsTableRow' + index1).remove();
       },
       uploadIcon: function(){
           showMask();
           var formData = new FormData($('#_LayerEditForm_')[0]);
            $.ajax({
                url: 'layer/uploadIcon',
                type: 'POST',
                beforeSend: false,
                success: function(layerId){
                   $('#_LayerEditIcon_').attr("src", "geo/flagicon/"  + layerId + "?" + Math.random()); // FF anticache
                   $('#_LayerEditForm_ #id').val(layerId);
                   
                   layers.reloadTree();
                   
                   hideMask();
                   alertMessage("Слой сохранен. Иконка загружена успешно.");
               },
                error: function(data){
                    hideMask();
                    alertMessage("Ошибка загрузки.");
                },
                // Form data
                data: formData,
                //Options to tell jQuery not to process data or worry about content-type.
                cache: false,
                contentType: false,
                processData: false
            });
       },
       uploadTreeIcon: function(){
           showMask();
           var formData = new FormData($('#_LayerEditForm_')[0]);
            $.ajax({
                url: 'layer/uploadTreeIcon',
                type: 'POST',
                beforeSend: false,
                success: function(layerId){
                   $('#_LayerTreeIcon_').attr("src", "layer/treeicon/"  + layerId + "?" + Math.random()); // FF anticache
                   $('#_LayerEditForm_ #id').val(layerId);
                   
                   layers.reloadTree();
                   
                   hideMask();
                   alertMessage("Слой сохранен. Иконка загружена успешно.");
               },
                error: function(data){
                    hideMask();
                    alertMessage("Ошибка загрузки.");
                },
                // Form data
                data: formData,
                //Options to tell jQuery not to process data or worry about content-type.
                cache: false,
                contentType: false,
                processData: false
            });
       },
       removeTreeIcon: function(){
           showMask();
           var formData = new FormData($('#_LayerEditForm_')[0]);
            $.ajax({
                url: 'layer/removeTreeIcon',
                type: 'POST',
                beforeSend: false,
                success: function(layerId){
                   $('#_LayerTreeIcon_').attr("src", "layer/treeicon/"  + layerId + "?" + Math.random()); // FF anticache
                   //$('#_LayerEditForm_ #id').val(layerId);
                   
                   layers.reloadTree();
                   
                   hideMask();
                   alertMessage("Слой сохранен. Иконка успешно удалена.");
               },
                error: function(data){
                    hideMask();
                    alertMessage("Ошибка загрузки.");
                },
                // Form data
                data: formData,
                //Options to tell jQuery not to process data or worry about content-type.
                cache: false,
                contentType: false,
                processData: false
            });
       }
    };
})();