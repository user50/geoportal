<%@ page language="java" contentType="text/html; charset=utf8" pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8">
<title>Роль</title>
<link rel="stylesheet" href="../../css/style.css" type="text/css" />
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" type="text/css" />
<link type="text/css" href="../../css/jquery.uix.multiselect.css" rel="stylesheet" />

<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script src="../../js/jquery.uix.multiselect.js"></script>
<script type="text/javascript" src="../../js/locales/jquery.uix.multiselect_ru.js"></script>
<script type="text/javascript" src="../../js/locales/jquery.uix.multiselect_ru1.js"></script>
<script type="text/javascript">
    $(function() {
        $("#aclIds").multiselect({
            selectionMode : 'd&d',
            searchField : false,
            splitRatio: 0.5,
            locale: 'ru'
        });
        $("#userIds").multiselect({
            selectionMode : 'd&d',
            searchField : false,
            splitRatio: 0.5,
            locale: 'ru1'
        });
    });
</script>

<style type="text/css">
/* <![CDATA[ */

body {
    font-size: 13px;
    background: #deded3;
    color: #6c6c6c;
    overflow: scroll;
}

table {
    font-size: 15px;
    color: #6с6с6с;
    background-color: #ffffff;
    width: 100%;
}

th {
}

tr{
    border-bottom: 0px solid #000000;
    height: 45px;
}

td{
    padding: 0px;
}

.main {
    margin-left: auto;
    margin-right: auto;
    width: 580px;
    background-color: white;
    border: 1px solid #e2e2e2;
}

.header {
    background-color: #eeeeee;
    border: 1px solid #e2e2e2;
    height: 45px;
    position: relative;
}

.header-label {
    line-height: 45px;
    margin-left: 25px;
    font-weight: bold;
    font-size: 12;
    width: 880px;
    display: block;
    float: left;
}

.header-close{
    display: block;
    background: url("../../css/admin/close.png");
    background-repeat: no-repeat;
    width: 12px;
    height: 20px;
    right: 25px;
    top: 18px;
    position: absolute;
    cursor: pointer;
}

.frm-body{
    padding: 25px;
}

.multiselect {
	width: 530px;
	height: 300px;
}

.uix-multiselect {
    float: none;
}

.uix-multiselect .ui-widget-header {
    height: 45px;
}

.uix-multiselect .ui-widget-header div.header-text {
    margin: 14;
    font-size: 13px;
    color: #6c6c6c;
}


.uix-multiselect .ui-widget-header .uix-control-right {
    margin-top: 12;
}

.ui-state-default, .ui-widget-content .ui-state-default{
    color: #6c6c6c;
}

.ui-widget-header {
    background: #eeeeee;
}

.ui-corner-tl {
    border-top-left-radius: 0px;
    border-right: 0px;
}

.ui-corner-tr {
    border-top-right-radius: 0px;
}

.multiselect-available-list{
    
}

.ui-state-highlight, .ui-widget-content .ui-state-highlight{
    border: 0px;
}


.ui-state-default, .ui-widget-content .ui-state-default{
    background: white;
}

#multiselect0_selListContent{
    border-top: 0px;
    border-right: 0px;
}

#multiselect0_avListContent{
    border-top: 0px;
}


.ui-state-hover, .ui-widget-content .ui-state-hover {
    background: #3390eb;
    font-weight: bold;
    color: white;
    padding: 12px;
}

.ui-state-highlight, .ui-widget-content .ui-state-highlight {
    border: 1px solid #e2e2e2;
}


.uix-multiselect .option-element{
    border: 0px;
    padding: 12px;
}


.ui-widget {
    font-family: inherit;
    font-size: inherit;
}

.input1 {
    width: 210px;
    height: 30px;
    border: 1px solid #e2e2e2;
    border-radius: 5px;
    padding: 6px;
}

.tools-role {
    width: 100%;
    padding-top: 30px;
}

.tools-btn{
    width: 100%;
    margin-top: 30;
}

/* ]]> */
</style>

<script type="text/javascript">
function close1(){
    //open(location, '_self').close();
    window.location = "../users";
}

$(function() {
    $('#user').submit(function() {
        return true;
    });
});
</script>

</head>
<body>
	<div class=main>
        <div class=header>
            <span class=header-label>Роль</span>
            <span class=header-close  onclick="close1();"></span>
        </div>
		<c:if test="${not empty exception}">
	        <div id="exception" style="color: red">${exception}</div>
	    </c:if>
        <div class=frm-body>
    		<form:form method="post" action="${command}" modelAttribute="role">
    			<table>
                    <thead>
                        <tr style="height: 0;">
                            <th style="width: 90px;"></th>
                            <th style="width: 230px;"><form:hidden path="id"/></th>
                            <th></th>
                        </tr>
                    </thead>
    				<tr>
    					<td>Название роли</td>
    					<td>
                        <c:choose>
                            <c:when test="${(role.id > 10) or (empty role.id)}"><form:input class="input1" path="name" /></c:when>
                            <c:otherwise><label>${role.name}</label><form:hidden path="name" /></c:otherwise>
                        </c:choose>
                        </td>
                        <td></td>
    				</tr>
                </table>
                <div class=tools-role>
                    <b>Пользователи:</b>
                    <form:select path="userIds" multiple="true" class="multiselect">
                        <form:options style="margin: 12px;" items="${userList}" />
                    </form:select>
                    <br/>
                    <b>Территориальные права:</b>
                    <form:select path="aclIds" multiple="true" class="multiselect">
                        <form:options style="margin: 12px;" items="${aclList}" />
                    </form:select>
                </div>
                <div class=tools-btn>
                    <button type="submit">
                        <span>Сохранить</span>
                    </button>
                    <button onclick='close1()' type="button">
                        <span>Закрыть</span>
                    </button>
                </div>
    		</form:form>
        </div>
	</div>
</body>
</html>