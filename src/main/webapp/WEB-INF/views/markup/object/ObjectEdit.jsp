<%@page trimDirectiveWhitespaces="true"%>
<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<spring:eval var="anonymous" expression="T(org.w2fc.conf.Constants).isAnonymous()"/>
<!-- spring:eval var="editor" expression="T(org.w2fc.conf.Constants).isEditor()"/-->
<script id="ObjectTagsTmpl" type="text/x-jquery-tmpl">
	<tr id="ObjectTagsTableRow{{:index}}">
            <td><input type="hidden" name="properties[{{:index}}].id" value=""/>
				<input class="tagKey" name="properties[{{:index}}].key" value=""/></td>
            <td><input name="properties[{{:index}}].value" value=""/></td>
            <td><input class="del-tag" type="button" value="X" onclick="ObjectEditDialog.deleteTag({{:index}})"/></td>
    </tr>   
</script>

<script type="text/javascript">
	window.ObjectEditDialog = (function(){
	    var indx = ${fn:length(portalObject.tags)};

	    $(".tagKey").each(function(i, obj){
           	$(obj).removeClass('tagKey');
           	var s = $(obj).attr('name').substring("properties[".length);           
			var indx = s.substring(0, s.length - 5);
			var value = $(obj).val();
           	autocompleteLib.setAutocompleteKey(obj);
           	/*if(autocompleteLib.isFiasField(value)){
           		autocompleteLib.setAutocompleteFias($('input[name="properties['+ indx +'].value"]'), obj);
           	}else{*/
           		autocompleteLib.setAutocompleteValue($('input[name="properties['+ indx +'].value"]'), obj);
           	//}
        });
	    autocompleteLib.setAutocompleteFiasFull($('#fiasCodeSearch'), $('input[name="fiasCode"]'), $('#fiasCodeText'));
	    return {
    	    addTag: function(){
                $('#ObjectTagsTable').append($.templates('#ObjectTagsTmpl').render([{index: indx}]));
                indx++;
                $(".tagKey").each(function(i, obj){
                   	$(obj).removeClass('tagKey');
                   	var s = $(obj).attr('name').substring("properties[".length);           
                    var indx = s.substring(0, s.length - 5);
                    autocompleteLib.setAutocompleteKey(obj);
                    autocompleteLib.setAutocompleteValue($('input[name="properties['+ indx +'].value"]'), obj);
                });
            },
            addAdressTag: function(){
                var _this = this;
                L.esri.Geocoding.Tasks.reverseGeocode().latlng([${portalObject.lat},  ${portalObject.lon}]).run(function(error, result, response){
                    if(!error){
                    	console.log(result);
						for(var tagName in result.address){
							if(result.address[tagName] == null || result.address[tagName] == '')continue;
                    		_this.addTag();
                    		$('input[name="properties['+(indx-1)+'].key"]').val(tagName);
                    		$('input[name="properties['+(indx-1)+'].value"]').val(result.address[tagName]);
						}
                    }else{
                    	alertMessage("Невозможно определить адрес!");
                    }
                });
            },
            deleteTag: function(index1){
                $('#ObjectTagsTableRow' + index1).remove();
            },
            uploadIcon: function(){
                showMask();
                var formData = new FormData($('#_ObjectEditForm_')[0]);
                 $.ajax({
                     url: 'geo/object/uploadIcon',
                     type: 'POST',
                     beforeSend: false,
                     success: function(id){
                        $('#_ObjectEditIcon_').attr("src", "geo/object/flagicon/"  + id + "?" + Math.random()); // FF anticache
                        $('#_ObjectEditForm_ #id').val(id);
                        
                        hideMask();
                        alertMessage("Объект сохранен. Иконка загружена успешно.");
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
	
    $(".LayerLineColor__").minicolors({
        control: $(this).attr('data-control') || 'hue',
        defaultValue: $(this).attr('data-defaultValue') || '',
        inline: $(this).attr('data-inline') === 'true',
        letterCase: $(this).attr('data-letterCase') || 'lowercase',
        opacity: $(this).attr('data-opacity'),
        position: $(this).attr('data-position') || 'bottom left',
        change: function(hex, opacity) {},
        theme: 'default'
    });
    
</script>

<form:form id="_ObjectEditForm_"  modelAttribute="portalObject"> 
    <table>
        <thead>
            <tr style="height: 0;">
                <th style="width: 170px;"></th>
                <th><form:hidden path="id" /></th>
            </tr>
        </thead>
        <c:if test="${permissionObject}">
        <tr>
            <td colspan="2"><div style="text-align: center; background-color: red; color: white;">Используется в контроле прав!</div></td>
        </tr>
        </c:if>
        <tr>
            <td>Идентификатор:</td>
            <td>
            	<a target="_blank" href="mass/history/${portalObject.id}">${portalObject.id}</a>
      		</td>            
        </tr>
        <tr>
            <td>Название:</td>
            <td>
            <c:choose>
      			<c:when test="${editor}">
      				<form:input path="name" />
      			</c:when>
   				<c:otherwise>
      			<label>${portalObject.name}</label>
      			</c:otherwise>
			</c:choose>
            </td>            
        </tr>
		<tr>
			<td>Координаты:</td>
			<td><c:choose>
					<c:when test="${editor}">
						<c:choose>
							<c:when test="${portalObject.objectGeomType == 'Point'}">
								<label> 
										Широта:<form:input path="lat" /> 
										Долгота:<form:input path="lon" />
								</label>
							</c:when>
							<c:otherwise>
								<label><form:textarea path="wkt" /></label>
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<c:catch var="err">
							<label>${portalObject.lat} x
								${portalObject.lon}</label>
						</c:catch>
						<c:if test="${err != null}">
							<label class="obj_geom">${portalObject.geom}</label>
							<div id="tobuffer">Показать</div>
						</c:if>
					</c:otherwise>
				</c:choose></td>
		</tr>
		<c:choose>
			<c:when test="${portalObject.objectGeomType == 'Polygon'}">
				<tr>
					<td>Площадь:</td>
					<td>${areaCalc} м2</td>
				</tr>
			</c:when>
		</c:choose>
		<tr>
            <td>Создан:</td>
            <td><label>${creatorName}</label><br/><label><fmt:formatDate value="${portalObject.createdDateTime}" pattern="dd/MM/yyyy" /></label></td>
        </tr>
        <tr>
            <td>Последняя правка:</td>
            <td><label>${editorName}</label><br/><label><fmt:formatDate value="${portalObject.changedDateTime}" pattern="dd/MM/yyyy" /></label></td>
        </tr>
   </table>
   <table>
         <tr>
            <td>Код ФИАС*:</td>
            <td>
            	 <c:choose>
    				<c:when test="${editor}">
    					<input id="fiasCodeSearch"><br/>
    					<label>Цифровой код:</label><span id="fiasCodeText">${portalObject.fiasCode}</span>
            			<form:hidden path="fiasCode" />
            			<br/><span style="font-size:80%">*необходимо указать для поиска по адресу</span>
            		</c:when>
            		<c:otherwise>
            			<label>${portalObject.fiasCode}</label>
            		</c:otherwise>
            	</c:choose>
      		</td>            
        </tr>
    </table>
    <c:choose>
    <c:when test="${editor}">
    <table>
         <tr>
            <td>Принадлежность слоям:</td>
            <td>
            	 Присутствует на слоях: 
            	 <c:forEach items="${portalObject.layers}" var="layer">
            	 	"${layer.name}",
            	 </c:forEach><br/>
            	 Добавить к слою: 
            	 <select name="layerId" id="layerId" style="width:60%">
        		 <option value="">не выбран</option>
        		 <c:forEach items="${layers}" var="layer" varStatus="status">
        			 	<c:if test="${layer.typeId == 1}">
        			 	<option value="${layer.id}">${layer.name}</option>
        			 	</c:if>
        		  </c:forEach>
        		  </select>
        		  <a class="ui-button-text" onclick="map.addToLayer(${portalObject.id}, $('#layerId'))" href="#">добавить</a>
      		</td>            
        </tr>
    </table>
    <table>
        <thead>
            <tr style="height: 0;">
                <th style="width: 170px;"></th>
                <th><form:hidden path="id" /></th>
            </tr>
        </thead>
        <c:choose>
			<c:when test="${portalObject.objectGeomType == 'Point'}">
        <tr>
            <td>Иконка объекта точка: </td>
            <td>
                <img id="_ObjectEditIcon_" src="geo/object/flagicon/${portalObject.id}" />
                <input style="width: auto;" type="file" id="file" name="file"></input>
                <button type="button" onclick="ObjectEditDialog.uploadIcon();">Загрузить</button>
            </td>
        </tr>
        </c:when>
        <c:otherwise>
        <tr>
            <td>Цвет границ: </td>
            <td>
            	<form:input path="lineColor" class="LayerLineColor__"/>
            </td>
        </tr>
        <tr>
            <td>Толщина линии: </td>
            <td>
           		<form:input path="lineWeight" />
            </td>
        </tr>
        <tr>
            <td>Цвет заливки: </td>
            <td>
            	<form:input path="fillColor" class="LayerLineColor__"/>
            </td>
        </tr>
        </c:otherwise>
        </c:choose>
    </table>
    </c:when>
    </c:choose>
    <div class="obj_tags">
    <table id="ObjectTagsTable">
        <tr>
            <th>Ключ</th>
            <th>Значение</th>
            <th></th>
        </tr>
        <c:forEach items="${portalObject.tags}" var="tag" varStatus="status">
        <tr id="ObjectTagsTableRow${status.index}">
        <c:choose>
      		<c:when test="${editor}">
            	<td><input type="hidden" name="properties[${status.index}].id" value="${tag.id}"/>
            	<input class="tagKey" name="properties[${status.index}].key" value="${tag.key}"/></td>
            	<td><input name="properties[${status.index}].value" value="${fn:escapeXml(tag.value)}"/></td>
            	<td><input class="del-tag" type="button" value="X" onclick="ObjectEditDialog.deleteTag(${status.index})"/></td>
      		</c:when>
   			<c:otherwise>
                    <td><label>${tag.portalTagsDictionary.alias != "" && tag.portalTagsDictionary.alias != null ? tag.portalTagsDictionary.alias : tag.key}</label></td>
                    <c:choose>
                        <c:when test="${tag.portalTagsDictionary.type == 'url'}">
                            <td><a href="${tag.value}" target="_blank">${tag.value}</a></td>
                        </c:when>
                        <c:when test="${tag.portalTagsDictionary.type == 'image'}">
                            <td><img src="${tag.value}" style="max-height: 200;" /></td>
                        </c:when>
                        <c:otherwise>
                            <td><label>${tag.value}</label></td>
                        </c:otherwise>
                    </c:choose>
                    <td></td>
      		</c:otherwise>
		</c:choose>
        </tr>
        </c:forEach>
    </table>
    </div>
</form:form>
<c:if test="${editor}">
<button onclick="ObjectEditDialog.addTag()">Добавить</button>
<c:choose>
	<c:when test="${portalObject.objectGeomType == 'Point'}">
		<button onclick="ObjectEditDialog.addAdressTag()">Определить адрес</button>
	</c:when>
</c:choose>
</c:if>