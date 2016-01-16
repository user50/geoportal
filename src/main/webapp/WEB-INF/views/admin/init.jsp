<%@ page language="java" contentType="text/html; charset=utf8" pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf8">
	<title>Initialization</title>
</head>
<body>
	<div>
		<h1>Initialization</h1>
		<form:form method="post" action="init" modelAttribute="properties">
			<table>
				<thead>
					<tr>
						<th>Name</th>
						<th>Value</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${properties}" var="prop">
						<tr>
							<td>${prop.key}</td>
							<td><input name="prop['${prop.key}']" value="${prop.value}" readonly="readonly" /></td>
						</tr>
					</c:forEach>
					<tr>
						<td></td>
						<td>
							<button type="submit">
								<span>Сохранить</span>
							</button>
							<button onclick='window.location.href="user"' type="button">
								<span>Отмена</span>
							</button>
						</td>
					</tr>

				</tbody>
			</table>
		</form:form>
	</div>
</body>
</html>