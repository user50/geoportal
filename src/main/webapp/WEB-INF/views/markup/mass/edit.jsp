<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@page trimDirectiveWhitespaces="true"%>
<% pageContext.setAttribute("newLineChar", "\n"); %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8">
<script src="http://code.jquery.com/jquery-1.10.2.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script src="../../js/jquery-migrate-1.2.1.min.js"></script>
<script src="../../js/jquery.form.min.js"></script>
<script src="../../js/jsrender.js"></script>
<script src="../../js/jquery.numeric.js"></script>
<script src="../../js/handsontable.full.min.js"></script>
<script src="../../js/jquery.paging.js"></script>
<script src="../../js/massedit.js"></script>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" type="text/css" />
<link rel="stylesheet" href="../../css/style.css" type="text/css" />
<link rel="stylesheet" href="../../css/handsontable.full.css" type="text/css" />
<link rel="stylesheet" href="../../css/massedit.css" type="text/css" />
<title>Массовое редактирование объектов слоя "${layerName}"</title>

<style type="text/css">

#object_table{
	overflow: hidden;
	height: ${pagingSelector*24 + 30}px;
}

</style>
</head>
<body style="overflow: auto;">
	<section>
		<header>
			<div id="top-caption">
				<span><label>Геопортал Ярославской области - Редактирование объектов</label></span>
			</div>
		</header>
		<h1 style="white-space: nowrap;">Объекты слоя "${layerName}" (${totalCount} всего)</h1>
<div class="popup0">
			<div class="popup1">
<form id="massEditForm" method="post">
<div class="tags_section">
	<label>Теги для редактирования: (выбирайте интересующие теги и нажимайте "Применить")</label>
   <div id="tags_container" style="line-height: 20px;">
   <c:forEach items="${tagList}" var="key" varStatus="status">
   		<label class="tags">
   			<c:choose>
   				<c:when test="${checkedTags.contains(key)}"><input value="${key}" checked="checked" class="tag_checkbox" type="checkbox" name="tag_key"/></c:when>
   				<c:otherwise><input value="${key}" class="tag_checkbox" type="checkbox" name="tag_key"/></c:otherwise>
   			</c:choose>
   			<label>${key}</label>
   			<span class="delete_tag" title="Удалить" onclick="massedit.deleteTag(this); return false;"></span>
   		</label>
   </c:forEach>
   </div>
   <button id="addTag" onclick="return massedit.addTag()">Добавить тег</button>
</div>
   <div class="tool_pane">
   	<label>Фильтр по названию: </label>
   	<input type="text" name="nameFilter" value="${nameFilter}"/>
   	<span></span>
   	<label>Сортировать по: </label> 
   		<select id="sortSelector" name="sortSelector">
   			<option value="name">Наименованию</option>
   			<option value="id">Идентификатору</option>
   		</select>
   		<span></span>
   <label>Объектов на странице: </label> 
   	<select id="pagingSelector" name="pagingSelector">
   		<option value="10">10</option>
   		<option value="20">20</option>
   		<option value="50">50</option>
   		<option value="100">100</option>
   		<option value="300">300</option>
   </select>
   <span></span>
   <input type="submit" value="Применить"/>
   </div>
</form>
<div class="tool_pane">
	<label>Привязать/переместить на слой: </label>
	<select name="layerSelect" id="layerSelect">
       	<option value="">не выбран</option>
        <c:forEach items="${layers}" var="layer" varStatus="status">
         	<c:if test="${layer.typeId == 1}">
         	<option value="${layer.id}">${layer.name}</option>
         	</c:if>
         </c:forEach>
    </select>
    <span></span>
    <span id="tie" title="Привязать"></span>
    <span id="move" title="Переместить"></span>
    <span id="clone" title="Копировать"></span>
</div>

<div class="tool_pane">
	<div id="pagination"></div>
	<span title="Выбрать все" id="check_all"></span><span title="Сбросить все" id="uncheck_all" ></span>
	<label>Выбрано :</label><span id="checkedCount">${checkedObjCount}</span>
	<span id="delete"  title="Удалить выбранные"></span>
</div>
<div id="object_table"></div>

</div>
</div>
</section>
<script>
$(document).ready(function() {
	massedit.init(${layerId});
	<c:forEach items="${checkedTags}" var="tag" varStatus="status">
		massedit.getColHeaders().push('${tag}');
		massedit.getColMapping().push({data:'${tag}'});
	</c:forEach>
	$('#pagingSelector').val(${pagingSelector});
	$('#sortSelector').val('${sortSelector}');
	massedit.setPerPage(${pagingSelector});
	massedit.setSort('${sortSelector}');
	massedit.initTable(); 
	massedit.initPaging(${objectCount});
});
</script>
<div style="display: none" id="loadmask">
		<img id="loadimg" src="../../css/images/ajax-loader.gif">
</div>
</body>
</html>
