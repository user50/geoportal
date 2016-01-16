<%@ page language="java" contentType="text/html; charset=utf8" pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf8">
	<title>Пользователи</title>
	<link rel="stylesheet" href="../css/style.css" type="text/css" />
    <style>
/* <![CDATA[ */
body {
	font-size: 13px;
	background: #deded3;
	color: #6c6c6c;
	overflow: auto;
}

table {
	font-size: 13px;
	color: #0c0c0c;
	background-color: #ffffff;
    width: 100%;
    border-collapse: collapse;
}

th {
	font-weight: normal;
    text-align: left;
    color: #aaaaaa;
}

tr{
    border-bottom: 1px solid #e2e2e2;
    height: 45px;
}

td{
    padding: 5px;
}

.main {
	margin-left: auto;
	margin-right: auto;
	width: 950px;
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
    background: url("../css/admin/close.png");
    background-repeat: no-repeat;
    width: 12px;
    height: 20px;
    right: 25px;
    top: 18px;
    position: absolute;
    cursor: pointer;
}

a {
    display: block;
    background: url("../css/admin/icon-grey.png");
    background-repeat: no-repeat;
    width: 25px;
    height: 25px;
    margin-top: 8px;
}

a:hover {
    background: url("../css/admin/icon-blue.png");
    background-repeat: no-repeat;
}
/* ]]> */

</style>
<script type="text/javascript">
	function close1(){
	    open(location, '_self').close();
	}
</script>
</head>
<body>
<spring:eval var="myName" expression="T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()"/>

<div class=main>
    <div class=header>
        <span class=header-label>ПОЛЬЗОВАТЕЛИ</span>
        <span class=header-close onclick="close1();"></span>
    </div>
    <c:set var="count" value="0" scope="page" />
	<c:if test="${not empty userList}">
		<table align="center">
			<thead>
				<tr>
                    <th style="width: 60px;"></th>
					<th style="width: 210px;">Логин</th>
					<th style="width: 160px;">Фамилия</th>
					<th style="width: 130px;">Имя</th>
					<th style="width: 200px;">Эл.почта</th>
					<th style="width: 130px;">Телефон</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${userList}" var="user">
                <c:set var="count" value="${count + 1}" scope="page"/>
					<tr>
                        <td style="text-align: right;">${count}.</td>
						<td style="font-weight: bold; color: #3390eb;">${user.login}</td>
                        <td>${user.lastName}</td>
						<td>${user.firstName}</td>
						<td>${user.email}</td>
						<td>${user.phone}</td>
						<td>
                            <c:if test="${user.login != myName}"><a href="user/${user.id}"></a></c:if>
                        </td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
 	<button onclick='window.location.href="user/new"' type="button" >
	    <span>Создать нового</span>
	</button>
</div>

</body>
</html>