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

td {
    white-space: nowrap;
}

-->
</style>

<script type="text/javascript">

window.LayerPermissionDialog = (function(){
    
    var counter = null;
    
    return {
       disableSelect : function(){
           $('input[name^=permission][name$=id]').each(function(i, o){
               var type = $('input[name=' + (o.name.substring(0, o.name.length - 3) + '.type').replace(/([ #;?%&,.+*~\':"!^$[\]()=>|\/@])/g,'\\\\$1')+']')[0]; 
               $('#__permissionEntry__ option').each(function(i1, o1){
                   if(o1.value == "" + type.value.substring(0,1) + o.value)
                       $(o1).prop("disabled","disabled");
               });
           });
       },
       deleteEntry : function(index1, val){
           $('#perm-' + index1).remove();
           $('#__permissionEntry__ option[value='+ val +']').removeAttr("disabled");
       },
       addEntry : function(){
           var optionVal = $('#__permissionEntry__').val();
           if(!optionVal || "" == optionVal) return;
           
           var type = "user";
           if("r" == optionVal.substring(0,1))
               type = "role";
           
           $('#PermissionTable')
              .append($.templates('#LayerPermissionTmpl').render([
                {
                    index: counter++, 
                    o: {
                            id : optionVal.substring(1),
                            name : $('#__permissionEntry__ option[value='+ optionVal +']').text(),
                            type : type,
                            t : type.substring(0,1)
                        }
                }
           ]));
           LayerPermissionDialog.disableSelect();
       },
       setCounter : function(c){counter = c;}
    };
    
})();



</script>

<spring:eval var="editor" expression="T(org.w2fc.conf.Constants).isEditor()"/>
<spring:eval var="write" expression="T(org.w2fc.geoportal.layer.LayerController$Permission).MODE_WRITE"/>
<spring:eval var="read" expression="T(org.w2fc.geoportal.layer.LayerController$Permission).MODE_READ"/>

<c:set var="counter" scope="page" value="0"/>

<script id="LayerPermissionTmpl" type="text/x-jquery-tmpl">
<tr id="perm-{{:index}}">
    <td>{{:o.name}}</td>
    <td>
        <input type="hidden" name="permission[{{:index}}].id" value="{{:o.id}}"> 
        <input type="hidden" name="permission[{{:index}}].type" value="{{:o.type}}"> 
        <select name="permission[{{:index}}].permission">
 			<option value="${read}">Чтение</option>           
 			<option value="${write}">Запись</option>
        </select>
    </td>
    <td><input class="del-tag" type="button" value="X" onclick="LayerPermissionDialog.deleteEntry({{:index}}, '{{:o.t}}{{:o.id}}')"/></td>
</tr>
</script>

<form:form id="_PermissionsForm_" modelAttribute="layer">
    <table>
        <thead>
            <tr style="height: 0;">
                <th style="width: 260px;"></th>
                <th><form:hidden path="id" /></th>
            </tr>
        </thead>
        <tr>
            <td>Идентификатор</td>
            <td><label>${layer.id}</label></td>
        </tr>
        <tr>
            <td>Название слоя</td>
            <td><label>${layer.name}</label></td>
        </tr>
    </table>
	<br/>
	<select id="__permissionEntry__">
        <option value=""> ---------- </option>
		<optgroup label="Пользователи">
			<c:forEach items="${allUsers}" var="user" varStatus="status">
				<option value="u${user.id}">${user.login}</option>
			</c:forEach>
		</optgroup>
		<optgroup label="Роли">
			<c:forEach items="${allRoles}" var="role" varStatus="status">
				<option value="r${role.id}">${role.name}</option>
			</c:forEach>
		</optgroup>
	</select>
	<button type="button" onclick="LayerPermissionDialog.addEntry()">Добавить</button>
	
	<table id="PermissionTable">
        <tr>
            <th>Имя</th>
            <th>Уровень доступа</th>
            <th></th>
        </tr>
        <c:forEach items="${layerUsers}" var="user" varStatus="status">
        <tr id="perm-${counter}">
            <td>${user.name}</td>
            <td>
            	<input type="hidden" name="permission[${counter}].id" value="${user.id}">
            	<input type="hidden" name="permission[${counter}].type" value="${user.type}">
                <select name="permission[${counter}].permission">
                    <option value="${read}" ${read == user.permission ? 'selected="selected"':''}>Чтение</option>
                    <option value="${write}" ${write == user.permission ? 'selected="selected"':''}>Запись</option>
                </select>
            </td>
            <td><input class="del-tag" type="button" value="X" onclick="LayerPermissionDialog.deleteEntry(${counter}, 'u${user.id}')"/></td>
        </tr>
        <c:set var="counter" value="${counter + 1}"/>
        </c:forEach>
        <c:forEach items="${layerRoles}" var="role" varStatus="status">
        <tr id="perm-${counter}">
            <td>${role.name}</td>
            <td>
            	<input type="hidden" name="permission[${counter}].id" value="${role.id}">
            	<input type="hidden" name="permission[${counter}].type" value="${role.type}">
                <select name="permission[${counter}].permission">
                    <option value="${read}" ${read == role.permission ? 'selected="selected"':''}>Чтение</option>
                    <option value="${write}" ${write == role.permission ? 'selected="selected"':''}>Запись</option>
                </select>
            </td>
            <td><input class="del-tag" type="button" value="X" onclick="LayerPermissionDialog.deleteEntry(${counter}, 'r${role.id}')"/></td>
        </tr>
        <c:set var="counter" value="${counter + 1}"/>
        </c:forEach>
    </table>
</form:form>

<script type="text/javascript">
    $(function() {
        LayerPermissionDialog.disableSelect();
        LayerPermissionDialog.setCounter(${counter});
    });
</script>


