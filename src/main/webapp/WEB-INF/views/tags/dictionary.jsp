<%@ page language="java" contentType="text/html; charset=utf8" pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@page trimDirectiveWhitespaces="true"%>
<% pageContext.setAttribute("newLineChar", "\n"); %>

<spring:eval var="editor" expression="T(org.w2fc.conf.Constants).isEditor()"/>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8">
<script src="http://code.jquery.com/jquery-1.10.2.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script src="../js/jquery-migrate-1.2.1.min.js"></script>
<script src="../js/jquery.form.min.js"></script>
<script src="../js/jsrender.js"></script>
<script src="../js/jquery.autocomplete.js"></script>
<script src="../js/jquery.numeric.js"></script>
<script src="../js/mask.js"></script>
<script src="../js/user.js"></script>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" type="text/css" />
<link rel="stylesheet" href="../css/style.css" type="text/css" />
<link rel="stylesheet" href="../css/tags-dict.css" type="text/css"/>

<title>Редактирование справочника тегов</title>

<style type="text/css">

#loadmask {
    z-index: 10100;
    background: url("../css/images/loadmask.png") repeat;
    position: absolute;
    left: 0px;
    top: 0px;
    text-align: center;
    vertical-align: middle;
}

#loadmask img {
    position: absolute;
}

</style>

<!--[if IE]>
    <script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
    <style type="text/css">

        .clear {
            zoom: 1;
            display: block;
        }

    </style>

<![endif]-->
    
<script id="dictionaryLine" type="text/x-jsrender">
{{for data}}
    <tr id="tagEntry{{:id}}" class="tagEntry" onclick="dictionary.onSelectingRow({{:id}}, arguments[0] || window.event)">
        <td>
			<input type="hidden" name="id" value="{{:id}}"/>
			<input  class="tagKey" type="text" name="key" value="{{:key}}"/></td>
        <td><input type="text" name="alias" value="{{:alias}}"/></td>
        <td>
            <select name="type">
                <option {{:type=="string"?"selected":""}} value="string">Строка</option>
                <option {{:type=="number"?"selected":""}} value="number">Число</option>
				<option {{:type=="url"?"selected":""}} value="url">Ссылка</option>
				<option {{:type=="image"?"selected":""}} value="image">Изображение</option>
            </select>
        </td>
        <td class="values1">
			<div class="tagEntryValuesClass" style="display: none;">
            <table>
				<tbody id="tagEntryValues">
					{{for values ~type=type tmpl="#dictionaryLineValue" /}}
				</tbody>
            </table>
			<span class="addValueButton" onclick="dictionary.addValue({{:id}})">+</span>
			</div>
		</td>
		<td class="tagEntryValuesClass" style="display: none;">
			<button onclick="dictionary.save({{:id}})" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"><span class="ui-button-text"> Сохранить</span></button>
            <button onclick="dictionary.remove({{:id}})" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"><span class="ui-button-text"> Удалить</span></button>
		</td>
    </tr>
{{/for}}
</script>
<script id="dictionaryLineValue" type="text/x-jsrender">
	<tr><td><input class="tagEntryValue {{:~type}}Class" type="text" value="{{:value}}"/></td><td class="deleteValueButton" onclick="dictionary.removeValue(this)">x</td></tr>
</script>

</head>
<body>
    <section>
        <header>
            <nav>
                <div id="top-caption"><span><label>Геопортал Ярославской области - Редактирование справочника тегов</label></span>
                        <c:import url="/admin/userProfileLite"/>
                </div>
            </nav>
        </header>

        <h1 style="white-space: nowrap;">Редактирование справочника тегов</h1>
        
        <div class="popup1" onclick="dictionary.onSelectingRow()">
            <div class="popup2">
                <button id="addNew" onclick="dictionary.create()" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"><span class="ui-button-text">Добавить</span></button>
            </div>    
            <div class="line"></div>
            <div class="popup2">
                <input id="exTableFilter"><button id="exTableFilterButton" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"><span class="ui-button-text">Фильтр</span></button>
            </div>
            <div class="line"></div>
            <div class="popup2">
                <table>
                    <tbody id="__dictionaryData__">
                        <tr>
                            <th class="column1">Ключ</th>
                            <th class="column1">Алиас</th>
                            <th class="column1">Тип</th>
                            <th class="column2">Значения</th>
                            <th class="column3"></th>
                       </tr>
                   </tbody>
                </table>
            </div>
            <div style="display: none" id="loadmask">
                <img id="loadimg" src="../css/images/ajax-loader.gif">
            </div>
            <footer>
            </footer>
        </div>
    </section>

    <script type="text/javascript">
        
        $(document).ready(function() {
            
            var data = [];

            var _setAutocomplete = function(field){
            	$(field).autocomplete("../layer/tag/name", {
        		    delay:10,
        		    minChars:1,
        		    matchSubset:1,
        		    autoFill:true,
        		    matchContains:1,
        		    cacheLength:10,
        		    selectFirst:true,
        		    maxItemsToShow:100
        		  });
            };

            showMask();
    	    $.ajax({
    			url : 'rest/list',
    			type : 'GET',
    			contentType : 'application/json',
    			async: true
    		}).done(function (_data){
    		    // order data by key
    		    var key = "key";
    		    data = _data.sort(function(a, b){
    		        var x = a[key]; var y = b[key];
    		        return ((x < y) ? -1 : ((x > y) ? 1 : 0));
    		    });
    		    
    		    // render recieved data
    		    $('#__dictionaryData__').append($.templates('#dictionaryLine').render({
                    data: data}
                ));
				
				// disable types of "live" objects
				$("#__dictionaryData__ select[name='type']").prop('disabled', true);
				// initialize number fields
				$(".numberClass").numeric({decimal: "," });

				$(".tagKey").each(function(i, obj){
		           	$(obj).removeClass('tagKey');
		           	_setAutocomplete(obj);
	            });
    		    
				try{
					dictionary.onSelectingRow(data[0].id);
				}catch(e){}
				
				userManager.init();
				userManager.exit = function(){
					window.location = '../logout';
				};
				userManager.openAdminConsole = function(){
					window.open('../admin/user',  '_blank');
				};
				
    		    hideMask();
    		    
    		}).fail(function(jqXHR, textStatus) {
    		    hideMask();
    			alert( "Request failed: " + textStatus );
    		});
            

            window.dictionary = window.dictionary || (function(){
                
                var containerId = "__dictionaryData__"; 
                
                var ids4NewRow = -1000;
                
                var selectedRowId = null;
                
                var getTagEntriesList = function(){
                    return $("#"+containerId+" tr[id^='tagEntry']");
                };
                
                var afterRemove = function(nId, id){
                    $("#"+containerId+" #tagEntry"+ nId).remove();
                    if(!!id){
                        for(var i = 0; i < data.length; i++){
                	        if(data[i].id == id){
                	            data.splice(i, 1);
                	            break;
                	        }
                	    }
                    }
                };
                
                var beforeSave = function(nId, obj){
                    for(var i = 0; i < data.length; i++){
            	        if(data[i].key == obj.key && data[i].id != obj.id){
            	            alert("Тег с ключом '" + obj.key +"' уже существует, \n измените имя ключа");
            	            return false;
            	        }
            	    }
					return true;
                };

				var afterSave = function(nId, obj){
					if(0 > nId){
                        $("#"+containerId+" #tagEntry"+ nId + "  input[name='id']").val(obj.id);
                    }
					
                    for(var i = 0; i < data.length; i++){
            	        if(data[i].id == obj.id){
            	            data[i] = obj;
            	            return;
            	        }
            	    }
                    // if didn't find
                    data.push(obj);
                };
                
                // FILTER ---------------------------------------------------
                $('#exTableFilter').keypress(function (e) {
                    if (e.which == 13) {
                        $('#exTableFilterButton').trigger("click");
                    }
                });
            
            	$('#exTableFilterButton').click(function(){
            	    var d = [];
            	    var substr = $('#exTableFilter').val().toUpperCase();
            	    
            	    for(var i = 0; i < data.length; i++){
            	        if(data[i].key.toUpperCase().search(substr) > -1){
            	            $("#"+containerId+" #tagEntry"+ data[i].id).show();
            	        }else{
            	            $("#"+containerId+" #tagEntry"+ data[i].id).hide();
            	        }
            	    }
            	});
            	// FILTER ---------------------------------------------------
                
                return {
                	size : function(){
                	    return getTagEntriesList().length;
                	},
                	get : function(nId){
                	    var qPrefix = "#"+containerId+" #tagEntry"+ nId;
                	    
                	    var arr = $(qPrefix + " input[class^='tagEntryValue']");
                	    var valuesArr = [];
                	    for(var i = 0; i < arr.length; i++){
                	        valuesArr.push({value: arr[i].value})
                	    }
                	    
                	    var _id = parseInt($(qPrefix + " input[name='id']").val());
                	    if(0 > _id) _id = null;
                	    
                	    return {
                	        id : _id,
                	        key : $(qPrefix + " input[name='key']").val(),
                	        alias : $(qPrefix + " input[name='alias']").val(),
                	        type : $(qPrefix + " select[name='type']").val(),
                	        values : valuesArr
                	    };
                	},
                    /*
                    *  SAVE OBJECT
                    */
                	save : function(nId){
						var o = this.get(nId);
						if(! beforeSave(nId, o)){
							return;
						}
                	    showMask();
                	    $.ajax({
                			url : 'saveOrUpdate',
                			data : JSON.stringify(o),
                			type : 'POST',
                			contentType : 'application/json',
                			async: true
                		}).done(function (data){
                		    afterSave(nId, data);
                		    hideMask();
                		}).fail(function(jqXHR, textStatus) {
                		    hideMask();
                			alert( "Request failed: " + textStatus );
                		});
            	    },
                    /*
                    *  REMOVE OBJECT
                    */
                	remove : function(nId){
                	    var o = this.get(nId);

                	    if(0 > o.id || null == o.id){
                	        afterRemove(nId);
                	        return;
                	    }
                	    
                	    showMask();

                	    $.ajax({
                			url : 'rest/remove/' + o.id,
                			type : 'GET',
                			contentType : 'application/json',
                			async: true
                		}).done(function (data){
                		    afterRemove(nId, o.id);
                		    hideMask();
                		}).fail(function(jqXHR, textStatus) {
                		    hideMask();
                			alert( "Request failed: " + textStatus );
                		});
                	},
                    /*
                    *  CREATE NEW ROW IN THE FORM
                    */
                	create : function(){
                	    var nId = ids4NewRow--;
                	    $('#'+containerId).append($.templates('#dictionaryLine').render({
                            data:[{id: nId, values:[]}]}
                        ));
                	    $(".tagKey").each(function(i, obj){
        		           	$(obj).removeClass('tagKey');
        		           	_setAutocomplete(obj);
        	            });
                	},
                    /*
                    *  CREATE NEW VALUE
                    */
                	addValue : function(nId){
						var t = $("#"+containerId+ " #tagEntry"+ nId + " select[name='type']").val();
                	    $("#"+containerId+ " #tagEntry"+ nId + " #tagEntryValues").append(
							$.templates('#dictionaryLineValue').render({data:[{}]}, {type: t})
						);
						$("#"+containerId+ " #tagEntry"+ nId + " .numberClass").numeric({decimal: "," });
						$("#"+containerId+ " #tagEntry"+ nId + " select[name='type']").prop('disabled', true);
                	},
                    /*
                    *  REMOVE VALUE
                    */
                	removeValue : function(node){
                	    $(node.parentNode).remove();
                	},
                    /*
                    *  ON SELECTING ROW
                    */
                	onSelectingRow : function(nId, event){
                		if(!!event){
                		    if (event.stopPropagation){
                	        event.stopPropagation();
                    	    }else{
                    	       event.cancelBubble=true;
                    	    }
                		}
                	    $(".tagEntryValuesClass").hide();
                	    $("#"+containerId+ " #tagEntry"+ nId + " .tagEntryValuesClass").show();
                	    selectedRowId = nId;
                	}
                };
            })();
        });
    </script>
</body>
</html>
