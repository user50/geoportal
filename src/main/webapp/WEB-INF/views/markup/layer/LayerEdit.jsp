<%@page trimDirectiveWhitespaces="true"%>
<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<style>
<!--
.colors-readonly{
    width: 30px;
    height: 20px;
}

#pageDialog td {
	white-space: nowrap;
}

-->
</style>

<!-- spring:eval var="editor" expression="T(org.w2fc.conf.Constants).isEditor(id)"/-->

<script id="LayerTagsTmpl" type="text/x-jquery-tmpl">
    <tr id="LayerTagsTableRow{{:index}}">
        <td><input name="layerTags[{{:index}}].id" value="" type="hidden" /></td>
        <td><input class="tagKey" name="layerTags[{{:index}}].key" value=""/></td>
        <td><input name="layerTags[{{:index}}].value" value=""/></td>
		<td><input class="del-tag" type="button" value="X" onclick="layers.deleteTag({{:index}})"/></td>
    </tr>
</script>

<form:form id="_LayerEditForm_"  modelAttribute="portalLayer" enctype="multipart/form-data">
    <table>
        <thead>
            <tr style="height: 0;">
                <th style="width: 260px;"></th>
                <th><form:hidden path="id" /><form:hidden path="parentId" /></th>
            </tr>
        </thead>
        <tr>
            <td>Идентификатор</td>
            <td><label>${portalLayer.id}</label></td>
        </tr>
        <tr>
            <td>Название ${portalLayer.typeId == 0 ? "сервиса":"ПД"} </td>
            <td>
            <c:choose>
                <c:when test="${editor}"><form:input path="name" /></c:when>
                <c:otherwise><label>${portalLayer.name}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
        <tr ${isStandalone ? "style='display: none;'":""}>
            <td>Кол-во включений слоя</td>
            <td><label><fmt:formatNumber pattern="#,##0" value="${rating}"/></label></td>
        </tr>
         <tr>
            <td>Пользовательский рейтинг</td>
            <td><div likes="${likes}" id="stars"></div>
            </td>
        </tr>
        <tr ${portalLayer.typeId != 1 ? "style='display: none;'":""}>
            <td>Отображение по объектам</td>
            <td><c:choose>
                <c:when test="${editor}"><form:checkbox style="width: initial;" path="viewByObject" /></c:when>
                <c:otherwise><div class="dialog-cb-${portalLayer.viewByObject}"></div></c:otherwise>
            </c:choose>
            </td>
        </tr>
         <tr ${isStandalone ? "style='display: none;'":""}>
            <td>Количество объектов</td>
            <td>
                <label>${spatialDataWeight}</label>
            </td>
        </tr>
        <c:choose>
        <c:when test="${editor}">
         <tr>
            <td>Иконка в "дереве" (16x16): </td>
            <td>
            	<img id="_LayerTreeIcon_" src="layer/treeicon/${portalLayer.id}" />
                <input style="width: auto;" type="file" id="treefile" name="treefile"></input>
                <button type="button" onclick="layers.uploadTreeIcon();">Загрузить</button>
                <button type="button" onclick="layers.removeTreeIcon();">Сбросить</button>
            </td>
        </tr>
        </c:when>
        </c:choose>
  </table>
  <div ${isStandalone ? "style='display: none;'":""}>
   <!-- Tabs -->
   <table id="type_tabs">
   <tr>
   <c:if test="${editor}">
   	<td style="border-right: 1px solid #d1d1d1;padding-right: 10px;vertical-align: top;width: 150px;">
   		Тип слоя: <br/>
            <label><form:radiobutton style="width: auto;" path="typeId" value="1" /> Векторный слой (WFS)</label><br> 
   			<label><form:radiobutton style="width: auto;" path="typeId" value="2" /> WMS сервис</label><br>
   			<label><form:radiobutton style="width: auto;" path="typeId" value="3" /> Тайловый сервис</label><br>
   			<label><form:radiobutton style="width: auto;" path="typeId" value="5" /> Сервис ArcGIS</label><br>
            <form:radiobutton style="display: none;" path="typeId" value="0" /> <span style="display: none;">Сервис - реестр</span><br>
            <form:radiobutton style="display: none;" path="typeId" value="4" /> <span style="display: none;">Слой - реестр</span><br>
  	</td>
  	</c:if>
  	<td>
  	<c:choose>
        <c:when test="${portalLayer.typeId == 1}">
        	<c:set var="disp" value="block" scope="page"/>
        </c:when>
        <c:otherwise>
        	<c:set var="disp" value="none" scope="page"/>
        </c:otherwise>
    </c:choose>
  	<div type="tab" id="tabs-1" style="display: ${disp};">
   <table>
        <thead>
            <tr style="height: 0;">
                <th style="width: 160px;"></th>
                <th></th>
            </tr>
        </thead>     
        <tr>
            <td>Веб сервис</td>
            <td><a href="ws?wsdl" target="_blank">wsdl</a></td>
        </tr>
        
    </table>
  	<table>
        <thead>
            <tr style="height: 0;">
                <th></th>
                <th style="width: 100%;"></th>
            </tr>
        </thead>
        <tr>
            <td>Иконка объекта точка (25x41): </td>
            <td>
            	<img id="_LayerEditIcon_" src="geo/flagicon/${portalLayer.id}" />
                    <c:choose>
                    <c:when test="${editor}">
                        <input style="width: auto;" type="file" id="file" name="file"></input>
                        <button type="button" onclick="layers.uploadIcon();">Загрузить</button>
                    </c:when>
                </c:choose>
            </td>
        </tr>
        <tr>
            <td>Цвет границ: </td>
            <td>
            <c:choose>
                <c:when test="${editor}"><form:input path="lineColor" class="LayerLineColor__"/></c:when>
                <c:otherwise><div class="colors-readonly" style="background-color: ${portalLayer.lineColor};"></div></c:otherwise>
            </c:choose>
            </td>
        </tr>
        <tr>
            <td>Толщина линии (px): </td>
            <td>
            <c:choose>
                <c:when test="${editor}"><form:input onkeypress='return ((47 < event.charCode && event.charCode<58 && event.shiftKey==false) || (95<event.keyCode && event.keyCode<106)|| (event.keyCode==8) || (event.keyCode==9)|| (event.keyCode>34 && event.keyCode<40)|| (event.keyCode==46) )' path="lineWeight" /></c:when>
                <c:otherwise><label>${portalLayer.lineWeight}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
        <tr>
            <td>Цвет заливки: </td>
            <td>
            <c:choose>
                <c:when test="${editor}">
                	<form:input path="fillColor" class="LayerLineColor__"/>
                </c:when>
                <c:otherwise><div class="colors-readonly" style="background-color: ${portalLayer.fillColor};"></div></c:otherwise>
            </c:choose>
            </td>
        </tr>
         <tr>
            <td>Прозрачность заливки (от 0% до 100%): </td>
            <td>
            <c:choose>
                <c:when test="${editor}"><form:input onkeypress='return ((47 < event.charCode && event.charCode<58 && event.shiftKey==false) || (95<event.keyCode && event.keyCode<106)|| (event.keyCode==8) || (event.keyCode==9)|| (event.keyCode>34 && event.keyCode<40)|| (event.keyCode==46) )' path="fillOpacity" /></c:when>
                <c:otherwise><label>${portalLayer.fillOpacity}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
         <c:choose>
        <c:when test="${editor}">
        <tr>
        	<td>
        		<label for="tmpl">Шаблон для "облака": </label>
        	</td>
        	<td>	
        		<select name="tmplId" id="tmplId">
        		<option value="">не выбран</option>
        		<c:forEach items="${templates}" var="tmpl" varStatus="status">
        		<c:choose>
        			<c:when test="${portalLayer.tmplId == tmpl.id}">
        				<option selected="selected" value="${tmpl.id}">${tmpl.name}</option>
        			</c:when>
        			<c:otherwise>
        				<option value="${tmpl.id}">${tmpl.name}</option>	
        			</c:otherwise>
        		</c:choose>
        		</c:forEach>
        		</select>
        		<c:if test="${tmplEditor}">
        			<a class="ui-button-text" id="tmpl_editor" href="#">Редактор шаблонов</a>
        		</c:if>
        	</td>
        </tr>
        </c:when>
        </c:choose>
    </table>
	</div>
	<c:choose>
        <c:when test="${portalLayer.typeId == 2}">
        	<c:set var="disp" value="block" scope="page"/>
        </c:when>
        <c:otherwise>
        	<c:set var="disp" value="none" scope="page"/>
        </c:otherwise>
    </c:choose>
	<div type="tab" id="tabs-2" style="display: ${disp};">
	Адрес: <a href="http://www.opengeospatial.org/standards/wfs" target="_blank">WMS</a> сервиса: 
			<c:choose>
                <c:when test="${editor}"><br/><form:input path="wmsUrl" /></c:when>
                <c:otherwise><label>${portalLayer.url}</label></c:otherwise>
            </c:choose>
    </div>
    <c:choose>
        <c:when test="${portalLayer.typeId == 3}">
        	<c:set var="disp" value="block" scope="page"/>
        </c:when>
        <c:otherwise>
        	<c:set var="disp" value="none" scope="page"/>
        </c:otherwise>
    </c:choose>
  <div type="tab" id="tabs-3" style="display: ${disp};">
    Адрес тайлового* сервиса: 
			<c:choose>
                <c:when test="${editor}"><br/><form:input path="url" /></c:when>
                <c:otherwise><label>${portalLayer.url}</label></c:otherwise>
            </c:choose>
            <div style="white-space: normal;font-size: 10px;color: gray;">
*Пример строки подключения:<br/>

http://tile.host.ru/TileService.ashx?Request=GetTile&LayerName={layername}&apikey={apikey}&z={z}&x={x}&y={y}
<br/>

Обязательные параметры запроса<br/>
{layername} – цифробуквенный идентификатор слоя. Берется из значения тега {layername} в ответе на запрос Request=GetCapabilities, совпадает со служебным полем ID слоя в “личной карте пользователя”.
<br/>
{apikey} – ключ доступа
<br/>
{x}, {y}, {z} – номер тайла
            </div>
   </div>
   <c:choose>
        <c:when test="${portalLayer.typeId == 5}">
        	<c:set var="disp" value="block" scope="page"/>
        </c:when>
        <c:otherwise>
        	<c:set var="disp" value="none" scope="page"/>
        </c:otherwise>
    </c:choose>
    <div type="tab" id="tabs-5" style="display: ${disp};">
    <table>
        <thead>
            <tr style="height: 0;">
                <th></th>
                <th style="width: 100%;"></th>
            </tr>
        </thead>
        <tr>
            <td>Адрес сервиса ArcGis: </td>
			<c:choose>
                <c:when test="${editor}">
                	<td>
                	<form:input path="esriUrl" />
                	</td>
                </c:when>
                <c:otherwise><td>${portalLayer.url}</td></c:otherwise>
            </c:choose>
            </tr>
            
            
            <c:choose>
        <c:when test="${editor}">
        <tr>
        	<td>
        		<label for="tmpl">Шаблон для "облака": </label>
        	</td>
        	<td>	
        		<select name="esriTmplId" id="esriTmplId">
        		<option value="">не выбран</option>
        		<c:forEach items="${templates}" var="tmpl" varStatus="status">
        		<c:choose>
        			<c:when test="${portalLayer.tmplId == tmpl.id}">
        				<option selected="selected" value="${tmpl.id}">${tmpl.name}</option>
        			</c:when>
        			<c:otherwise>
        				<option value="${tmpl.id}">${tmpl.name}</option>	
        			</c:otherwise>
        		</c:choose>
        		</c:forEach>
        		</select>
        		<c:if test="${tmplEditor}">
        			<a class="ui-button-text" id="tmpl_editor1" href="#">Редактор шаблонов</a>
        		</c:if>
        	</td>
        </tr>
        </c:when>
        </c:choose>
        </table>
   </div>
  </td>
  </tr>
  </table>
    </div>
  <table>
        <thead>
            <tr style="height: 0;">
                <th></th>
                <th  style="width: 100%"></th>
            </tr>
        </thead>
        <tr>
            <td>Описание ${portalLayer.typeId == 0 ? "сервиса":"ПД"}: </td>
            <td>
            <c:choose>
                <c:when test="${editor}"><form:textarea  path="descSpatialData" /></c:when>
                <c:otherwise><label style="white-space: normal;">${portalLayer.descSpatialData}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
       <tr>
            <td>Документ - регламент: </td>
            <td>
            <c:choose>
                <c:when test="${editor}"><form:input path="docRegulation" /></c:when>
                <c:otherwise><label>${portalLayer.docRegulation}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
        <tr>
            <td>Уровень доступа: </td>
            <td>
            <c:choose>
                <c:when test="${editor}"><form:input path="accessLevel" /></c:when>
                <c:otherwise><label>${portalLayer.accessLevel}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
         <tr ${portalLayer.typeId == 0 ? "style='display: none;'":""}>
            <td>Источник получения ПД: </td>
            <td>
            <c:choose>
                <c:when test="${editor}"><form:input path="sourceSpatialData" /></c:when>
                <c:otherwise><label>${portalLayer.sourceSpatialData}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
        <tr ${portalLayer.typeId == 0 ? "style='display: none;'":""}>
            <td>Масштаб (точность) первоисточника данных: </td>
            <td>
            <c:choose>
                <c:when test="${editor}"><form:input path="mapAccuracy" /></c:when>
                <c:otherwise><label>${portalLayer.mapAccuracy}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
        <tr ${portalLayer.typeId == 0 ? "style='display: none;'":""}>
            <td>Объем данных: </td>
            <td>
            <c:choose>
                <c:when test="${editor}"><form:input path="dataAmount" /></c:when>
                <c:otherwise><label>${portalLayer.dataAmount}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
        <tr>
            <td>Дата последнего изменения ${portalLayer.typeId == 0 ? "сервиса или версия":"ПД"}: </td>
            <td>
            <c:choose>
                <c:when test="${editor}"><form:input id="_LayerEditForm_lastUpdateSpatialData_" path="lastUpdateSpatialData" /></c:when>
                <c:otherwise><label>${portalLayer.lastUpdateSpatialData}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
        <tr ${portalLayer.typeId == 0 ? "style='display: none;'":""}>
            <td>Периодичность актуализации ПД: </td>
            <td>
            <c:choose>
                <c:when test="${editor}">
                    <form:select path="updateFrequency">
                        <form:option value="нет"/>
                        <form:option value="постоянно"/>
                        <form:option value="раз в день"/>
                        <form:option value="раз в неделю"/>
                        <form:option value="раз в месяц"/>
                        <form:option value="по мере необходимости"/>
                        <form:option value="не известно"/>
                    </form:select>
                </c:when>
                <c:otherwise><label>${portalLayer.updateFrequency}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
        <!-- tr>
            <td>Дата последнего изменения метаданных: </td>
            <td><label><fmt:formatDate pattern="dd-MM-yyyy H:m" value="${portalLayer.lastUpdateMetadata}" /></label></td>
        </tr-->
        <tr ${portalLayer.typeId == 0 ? "style='display: none;'":""}>
            <td>Система координат первоисточника данных: </td>
            <td>
            <c:choose>
                <c:when test="${editor}">
                    <form:select path="coordinateSystem">
                        <form:option value="WGS84"/>
                        <form:option value="СК76"/>
                        <form:option value="МСК Рыбинска"/>
                        <form:option value="МСК Ярославля"/>
                    </form:select>
                </c:when>
                <c:otherwise><label>${portalLayer.coordinateSystem}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
        <tr ${portalLayer.typeId == 0 ? "style='display: none;'":""}>
            <td>Формат представления ПД: </td>
            <td>
            <c:choose>
                <c:when test="${editor}">
                    <form:select path="exportFormat">
                        <form:option value="wsdl"/>
                        <form:option value=""/>
                    </form:select>
                </c:when>
                <c:otherwise><label>${portalLayer.exportFormat}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
        <tr>
            <td>Информация об условиях доступа к ${portalLayer.typeId == 0 ? "сервису":"набору данных"}: </td>
            <td>
            <c:choose>
                <c:when test="${editor}"><form:textarea  path="accessConditions" /></c:when>
                <c:otherwise><label>${portalLayer.accessConditions}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
        <tr>
            <td>Название организации, ${portalLayer.typeId == 0 ? "поддерживающей сервис":"осуществляющей распространение данных"}: </td>
            <td>
            <c:choose>
                <c:when test="${editor}"><form:input path="ownerCompany" /></c:when>
                <c:otherwise><label>${portalLayer.ownerCompany}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
        <tr>
            <td>Ф.И.О. и должность ответственного лица: </td>
            <td>
            <c:choose>
                <c:when test="${editor}"><form:input path="ownerName" /></c:when>
                <c:otherwise><label>${portalLayer.ownerName}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
        <tr>
            <td>Телефон: </td>
            <td>
            <c:choose>
                <c:when test="${editor}"><form:input path="ownerPhone" /></c:when>
                <c:otherwise><label>${portalLayer.ownerPhone}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
        <tr>
            <td>Email: </td>
            <td>
            <c:choose>
                <c:when test="${editor}"><form:input path="ownerEmail" /></c:when>
                <c:otherwise><label>${portalLayer.ownerEmail}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
        <tr ${portalLayer.typeId == 0 ? "style='display: none;'":""}>
            <td>Территория покрытия, км<sup>2</sup>: </td>
            <td>
            <c:choose>
                <c:when test="${editor}"><form:input path="coverageArea" /></c:when>
                <c:otherwise><label>${portalLayer.coverageArea}</label></c:otherwise>
            </c:choose>
            </td>
        </tr>
   </table>
</form:form>

<script>
$(function() {
    $( "#_LayerEditForm_lastUpdateSpatialData_" ).datepicker({ dateFormat: "dd-mm-yy", firstDay: 1, changeYear: true });
});
</script>

