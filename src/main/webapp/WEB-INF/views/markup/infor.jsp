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

<title>Импорт гео-данных</title>

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

#exTableFilter {
    width: 510px;
}

#loadmask img {
    position: absolute;
}

.popup2{
    font-size: 13px;
}

.tagEntry {
    border-top-style: dashed;
    border-top-width: 1px;
}

.tagEntry1 {
    width: 30%;
}

.popup2 input{
    font-size: 13px;
    width: 200px;
}

.run input{
    width: 160px;
    height: 38px;
}

#addNew {
    width: 150px;
    height: 38px;
}

#clearTags {
    width: 90px;
    height: 38px;
}

#layerSelect {
    width: 510px;
}

#__dictionaryData__ {
    font-size: 13px;
}
.column1 {
    width: 10%;
}

.column2 {
    //width: 40%;
}

.column3 {
    //width: 40%;
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
    <tr id="tagEntry_{{:prefix}}{{:id}}" class="tagEntry" onclick="infor.onSelectingRow({{:id}}, arguments[0] || window.event)">
        <td>
            <label>{{:id}}</label>
        </td>
        <td>
            <label>{{:name}}</label>
        </td>
        <td>
			<label>{{:geomType}}</label>
		</td>
		<td>
            <table class="popup2">
                <tbody>
                    {{for tags tmpl="#dictionaryLineValue" /}}
                </tbody>
            </table>
		</td>
    </tr>
{{/for}}
</script>
<script id="dictionaryLineValue" type="text/x-jsrender">
    <tr><td class="tagEntry1">{{:key}}:</td><td>{{:value}}</td></tr>
</script>

<script id="queryTag" type="text/x-jsrender">
{{for data}}
                    <div name="tag">
                        Ключ: <input type="text" name="key"/>
                        Значение: <input type="text" name="value"/>
                        <span class="deleteValueButton" onclick="infor.removeTag(this)">x</span>
                    </div>
{{/for}}
</script>

</head>
<body>
    <section>
        <header>
            <nav>
                <div id="top-caption"><span><label>Геопортал Ярославской области - Импорт гео-данных</label></span>
                        <c:import url="/admin/userProfileLite"/>
                </div>
            </nav>
        </header>

        <h1 style="white-space: nowrap;">Импорт объектов из старой СУПД</h1>
        
        <div class="popup1">
            <div class="popup2">
                <br/><b>Слой для импорта:</b><br/>
                <select id="layerSelect">
                <c:forEach items="${layers}" var="layer" varStatus="status">
                <c:choose>
                    <c:when test="${layer.id == layerId}">
                        <option selected="selected" value="${layer.id}">${layer.name}</option>
                    </c:when>
                    <c:otherwise>
                        <option value="${layer.id}">${layer.name}</option>    
                    </c:otherwise>
                </c:choose>
                </c:forEach>
                </select>
                <button onclick="infor.runQuery()" style="width: 160px; height: 38px;" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"><span id="resultCount" class="ui-button-text">Выполнить запрос</span></button>
                <button onclick="infor.runImport()" style="width: 160px; height: 38px;" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"><span class="ui-button-text">Импортировать</span></button>
                
                <div id="queryBlock">
                    <br/><b>Параметры запроса:</b><br/><br/>
                    <div name="tag">
                        Ключ: <input type="text" name="key" value=""/>
                        Значение: <input type="text" name="value" value=""/>
                        <span class="deleteValueButton" onclick="infor.removeTag(this)">x</span>
                    </div>
                </div>
                <br/>
                <button id="addNew" onclick="infor.createTag()" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"><span class="ui-button-text">Добавить новую пару</span></button>
                <button  id="clearTags" onclick="infor.clearTags()" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"><span class="ui-button-text">Очистить</span></button>
                
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
                            <th class="column1">Идент.</th>
                            <th class="column2">Имя</th>
                            <th class="column2">Геометрия</th>
                            <th class="column3">Тэги</th>
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
            var ids = [];

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

            userManager.init();
            userManager.exit = function(){
                window.location = '../logout';
            };
            userManager.openAdminConsole = function(){
                window.open('../admin/users',  '_blank');
            };

            window.infor = window.infor || (function(){
                
                var containerId = "__dictionaryData__"; 
                
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
            	        if(!!data[i].name && data[i].name.toUpperCase().search(substr) > -1){
            	            $("#"+containerId+" #tagEntry_"+ data[i].prefix + data[i].id).show();
            	        }else{
            	            $("#"+containerId+" #tagEntry_"+ data[i].prefix + data[i].id).hide();
            	        }
            	    }
            	});
            	// FILTER ---------------------------------------------------
                
                return {
                    createTag : function(){
                        $('#queryBlock').append($.templates('#queryTag').render({
                            data:[{index: 1}]}
                        ));
                    },
                    removeTag : function(el){
                        $(el.parentNode).remove();
                    },
                    clearTags : function(el){
                        $('div[name=tag]').remove();
                        infor.createTag();
                    },
                    runQuery : function(){
                        
                        for(var i = 0; i < data.length; i++){
                            $("#"+containerId+" #tagEntry_"+ data[i].prefix + data[i].id).remove();
                        }
                        
                        var data1 = [];
                        
                        var divs1 = $('#queryBlock div[name="tag"]');
                        for(var i = 0; i < divs1.length; i++){
                            data1.push($(divs1[i]).find('input[name=key]')[0].value);
                            data1.push($(divs1[i]).find('input[name=value]')[0].value);
                        }
                        
                        showMask();
                        $.ajax({
                            url : 'search',
                            type : 'POST',
                            data: JSON.stringify(data1),
                            contentType : 'application/json',
                            async: true
                        }).done(function (_data){
                            // order data by key
                            var key = "key";
                            
                            $('#resultCount').text("Найдено " + _data.ids.length)
                            
                            ids = _data.ids;
                            
                            data = _data.listLast1000.sort(function(a, b){
                                var x = a[name]; var y = b[name];
                                return ((x < y) ? -1 : ((x > y) ? 1 : 0));
                            });
                            
                            // render recieved data
                            $('#__dictionaryData__').append($.templates('#dictionaryLine').render({
                                data: data}
                            ));
                            
                            hideMask();
                            
                        }).fail(function(jqXHR, textStatus) {
                            hideMask();
                            alert( "Request failed: " + textStatus );
                        });
                    },
                    runImport : function(){
                       
                        showMask();
                        $.ajax({
                            url : 'import/' + $('#layerSelect').val(),
                            type : 'POST',
                            data: JSON.stringify(ids),
                            contentType : 'application/json',
                            async: true
                        }).done(function (_data){
                            hideMask();
                            alert( "Завершено успешно" );
                        }).fail(function(jqXHR, textStatus) {
                            hideMask();
                            alert( "Request failed: " + textStatus );
                        });
                    }
                };
            })();
        });
    </script>
</body>
</html>
