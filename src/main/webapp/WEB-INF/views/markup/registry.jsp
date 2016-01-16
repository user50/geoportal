<%@page trimDirectiveWhitespaces="true"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:eval var="editor" expression="T(org.w2fc.conf.Constants).isEditor()"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8">
<title>Реестр метаданных</title>



<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css"
	type="text/css" />
<link rel="stylesheet" href="../css/registry.css" type="text/css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>

<script src="../js/jsrender.js"></script>
<script src="../js/registry.js"></script>
<script src="../js/jquery.form.min.js"></script>
<script src="../js/user.js"></script>

<script type="text/javascript">
	$(function() {
		registry.init('list');
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
<div id="{{:id}}" class="title">{{:name}}</div>
<div class="abstract" style="overflow: auto;">{{:descSpatialData}}</div>
</span>
<span>
<div class="links">
<a href="{{:id}}" onclick="" target="_blank"><img src="../css/images/metadata.png"></a>
<c:if test="${editor}">
<a href="#" onclick="registry.remove({{:id}})" target="_self"><img src="../css/images/metadata_del.png"></a>
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
<div id="top-caption"><span><label>Геопортал Ярославской области - Реестр метаданных</label></span>
		<c:import url="/admin/userProfileLite"/>
</div>
</header>
 <h1 style="white-space: nowrap;">Реестр метаданных</h1>
 
	<div id ="list" class="popup0">
	
		<ul>
    		<li><a href="#layers">Слои</a></li>
    		<li><a href="#services">Сервисы</a></li>
  		</ul>
		<div class="popup1" id = "layers">
					<div style="height: 65px; border-bottom: #E7E7E7 1px solid;display: table; width: 100%;font-size: 12px;">
						<span style="height:100%; display: table-cell;vertical-align: middle; padding-left: 20px;">
						<!--  <c:if test="${editor}">
						 <button style="width:160px; height: 37px;" id="layers-add" class="add_object ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" onclick="registry.openEmptyForm()"><span class="ui-button-text">Добавить</span></button>
						</c:if> -->
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
						<!-- <c:if test="${editor}">
						 <button style="width:160px; height: 37px;" id="services-add" class="add_object ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only" onclick="registry.openEmptyForm()"><span class="ui-button-text">Добавить</span></button>
						</c:if> -->
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