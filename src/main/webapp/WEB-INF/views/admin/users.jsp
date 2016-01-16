<%@page trimDirectiveWhitespaces="true"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:eval var="editor" expression="T(org.w2fc.conf.Constants).isEditor()"/>
<spring:eval var="admin" expression="T(org.w2fc.conf.Constants).isAdministrator()"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8">
<title>Управление пользователями и ролями</title>



<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css"
	type="text/css" />
<link rel="stylesheet" href="../css/registry.css" type="text/css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>

<script src="../js/jsrender.js"></script>
<script src="../js/jquery.form.min.js"></script>
<script src="../js/user.js"></script>
<script src="../js/mask.js"></script>

<style type="text/css">
.ui-tabs .ui-tabs-nav li {
	width: inherit;
}
</style>

<script type="text/javascript">

var userManagment = (function() {
        // private
        var embdObj = null;
        
        var userManagmentData = [];
        var curPage = {};
        
        
        var showEntryList = function(id){
            $('#'+id+'-browse-results-header-summaryText').html(userManagmentData.length + ' найдено');
            var _filter = $('#'+id+'-browse-results-header-filterText').val();
            var arg= {data:userManagmentData};
            if(_filter != ''){
                var d = [];
                var exp = new RegExp('.*' + _filter + '.*', 'i');
                for(var inx in userManagmentData){
                    if(exp.test(userManagmentData[inx].login) ||
                    	exp.test(userManagmentData[inx].fullName)	
                    )d.push(userManagmentData[inx]);
                }
                arg= {data:d};
            }
            
            var pages = Math.floor(arg.data.length / 10) + 1;
            var paging = '';
            for(var i  = 1 ; i < pages+1 ; i++){
                var c = '';
                if(!curPage[id])curPage[id] = 1;
                if(i == curPage[id])c = 'class="current"';
                paging += '<a href="#" onclick="userManagment.setPage(\''+id+'\', '+i+')" '+c+'>'+i+'</a>'; 
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
            var prefix = "";
            if("services" === id){
                prefix = "role/";
            }
            
            showMask();
            $.ajax({
                type : "GET",
                url : prefix + "rest/list"   //id
            }).done(function(data) {
                userManagmentData = data;
                for(var i = 0; i < data.length; i++){
                    if(!data[i].login){
                        data[i].isRole = true;
                    }
                }
                showEntryList(id);
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
                    showEntryList('layers');
                });
                $('#layers-browse-results-header-filterText').on('keypress', function(e){
    				if(e.keyCode == 13){
    					showEntryList('layers');
    				}
    			});
                $('#services-browse-results-header-clearFilter').click(function(){
                    $('#services-browse-results-header-filterText').val('');
                    showEntryList('services');
                });
                $('#layers-browse-results-header-filter').click(function(){
                    showEntryList('layers');
                });
                $('#services-browse-results-header-filter').click(function(){
                    showEntryList('services');
                });
                $('#add').click(function(){
                    //TODO add
                });
            },
            setPage : function(obj, page){
                curPage[obj] = page;
                 showEntryList(obj);
            },
            remove : function(id,  isRole){
                if(confirm("Подтвердите пожалуйста удаление...")){
                    
                    showMask();
                    $.ajax({
                        type : "GET",
                        url : (isRole?"role/":"user/")+"remove/" + id 
                    }).done(function(data) {
    //                    hideMask();
                        window.location.href="";
                    }).fail(function(jqXHR, textStatus) {
                        hideMask();
                        alert("Request failed: " + textStatus);
                    });
                }
            },
            openEmptyForm : function(type){
                try{
                    window.location = type+"/new";
                }catch(e){
                    alert("Для создания новой записи необходимо сначала выбрать тип.");
                }
            },
            refresh : function(type){
                if("user" == type)
                    selectElements('layers');
                else
                    selectElements('services');
            }
        };
    })();
    
	$(function() {
		userManagment.init('list');
		userManager.init();
		userManager.exit = function(){
			window.location = '../logout';
		};
		userManager.openAdminConsole = function(){
			window.open('../admin/user',  '_blank');
		};
	});
	
	
</script>

<script id="reestr_section" type="text/x-jquery-tmpl">
{{for data}}
{{if ~paging(#index)}}
<div class="snippet">
<span style="width: 25px;padding-left: 20px;">
{{if typeId == 1}}
  <img src="../css/themes/classic/vector.png" alt="Векторный слой" title="Векторный слой">
{{else typeId == 3}}
  <img src="../css/themes/classic/tile.png" alt="Тайловый слой" title="Тайловый слой">
{{/if}}
</span>
<span>
<a href="{{if isRole}}role/{{else}}user/{{/if}}{{:id}}" onclick=""><div id="{{:id}}" class="title">{{:login}}{{:name}}</div></a>
<div class="abstract" style="overflow: auto;">{{:fullName}}</div>
</span>
<span>
<div class="links">
<a href="{{if isRole}}role/{{else}}user/{{/if}}{{:id}}" onclick=""><img src="../css/images/metadata.png"></a>
<c:if test="${admin}">
{{if 10 < id}}
<a href="#" onclick="userManagment.remove({{:id}}, {{if isRole}}true{{else}}false{{/if}})" target="_self"><img src="../css/images/metadata_del.png"></a>
{{/if}}
</c:if>
</div>
</span>
</div>
{{/if}}
{{/for}}
</script>
</head>
<body>
<section>
 <header>
<div id="top-caption"><span><label>Геопортал Ярославской области - Управление пользователями и ролями</label></span>
		<c:import url="/admin/userProfileLite"/>
</div>
</header>
 <h1 style="white-space: nowrap;">Управление пользователями и ролями</h1>
 
	<div id ="list" class="popup0">
	
		<ul>
    		<li><a href="#layers">Пользователи</a></li>
    		<li><a href="#services">Роли</a></li>
  		</ul>
		<div class="popup1" id = "layers">
					<div style="height: 65px; border-bottom: #E7E7E7 1px solid;display: table; width: 100%;font-size: 12px;">
						<span style="height:100%; display: table-cell;vertical-align: middle; padding-left: 20px;">
						<c:if test="${admin}">
						 <button style="width:160px; height: 37px;" id="layers-add" class="add_object ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" onclick="userManagment.openEmptyForm('user')"><span class="ui-button-text">Добавить</span></button>
                         <button style="width:160px; height: 37px;" id="layers-add" class="add_object ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" onclick="userManagment.refresh('user')"><span class="ui-button-text">Обновить</span></button>
						</c:if>
						</span>
						<span id="layers-browse-results-header" style="height:100%; display: table-cell;vertical-align: middle; padding-right: 20px; text-align: right;">
							<span id="layers-browse-results-header-summaryText" class="result">0 найдено</span>
							<span id="layers-paging">
						</span>
					</div>
					<div style="height: 65px; border-bottom: #E7E7E7 1px solid;display: table; width: 100%;font-size: 12px;">
							<span style="height:100%; display: table-cell;vertical-align: middle; padding-left: 20px;">
							<input style="width: 540px;border: #E9E9E9 solid 1px;border-radius: 3px;height: 25px;" id="layers-browse-results-header-filterText" type="text" size="20">
								<button id="layers-browse-results-header-filter" type="submit" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"><span class="ui-button-text">Фильтр</span></button>
								<button id="layers-browse-results-header-clearFilter" type="submit" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"><span class="ui-button-text">Очистить</span></button>
							</span>
					</div>
					<div id="layers-obj_list">
					</div>
				</div>
		<div class="popup1" id = "services">
					<div style="height: 65px; border-bottom: #E7E7E7 1px solid;display: table; width: 100%;font-size: 12px;">
						<span style="height:100%; display: table-cell;vertical-align: middle; padding-left: 20px;">
						<c:if test="${admin}">
						 <button style="width:160px; height: 37px;" id="services-add" class="add_object ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" onclick="userManagment.openEmptyForm('role')"><span class="ui-button-text">Добавить</span></button>
						 <button style="width:160px; height: 37px;" id="layers-add" class="add_object ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" onclick="userManagment.refresh('role')"><span class="ui-button-text">Обновить</span></button>
                        </c:if>
						</span>
						<span id="services-browse-results-header" style="height:100%; display: table-cell;vertical-align: middle; padding-right: 20px; text-align: right;">
							<span id="services-browse-results-header-summaryText" class="result">0 найдено</span>
							<span id="services-paging">
						</span>
					</div>
					<div style="height: 65px; border-bottom: #E7E7E7 1px solid;display: table; width: 100%;font-size: 12px;">
							<span style="height:100%; display: table-cell;vertical-align: middle; padding-left: 20px;">
							<input style="width: 540px;border: #E9E9E9 solid 1px;border-radius: 3px;height: 25px;" id="services-browse-results-header-filterText" type="text" size="20">
								<button id="services-browse-results-header-filter" type="submit" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"><span class="ui-button-text">Фильтр</span></button>
								<button id="services-browse-results-header-clearFilter" type="submit" class="ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only"><span class="ui-button-text">Очистить</span></button>
							</span>
					</div>
					<div id="services-obj_list">
					</div>
		</div>
		
		
		<div style="display: none" id="loadmask">
		<img id="loadimg" src="../css/images/ajax-loader.gif">
	</div>	
	</div>
	</section>
</body>
</html>