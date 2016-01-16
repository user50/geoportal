<%@page trimDirectiveWhitespaces="true"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8">
<title>Настройки системы</title>



<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css"
	type="text/css" />
<link rel="stylesheet" href="../css/registry.css" type="text/css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>

<script src="../js/jsrender.js"></script>
<script src="../js/jquery.form.min.js"></script>
<script src="../js/user.js"></script>

</head>
<body>
	<section>
		<header>
			<div id="top-caption">
				<span><label>Геопортал Ярославской области - Настройки</label></span>
			</div>
		</header>
		<h1 style="white-space: nowrap;">Настройки системы</h1>

		<div id="list" class="popup0">
			<div class="popup1">
				<form method="post">
				<c:forEach items="${settings}" var="setting" varStatus="status">
					<div class="snippet">
						<span style="width: 25px;padding-left: 20px;"></span>
						<span style="width: 300px;">${setting.key}</span>
						<input type="hidden" name="settings[${status.index}].id" value="${setting.id}"/>            	
						<span><input name="settings[${status.index}].value" value="${setting.value}"/></span>
					</div>	
				</c:forEach>
				<c:forEach items="${params}" var="p">
					<div class="snippet">
						<span style="width: 25px;padding-left: 20px;"></span>
						<span style="width: 300px;">${p.key}</span>
						<span>${p.value}</span>
					</div>
				</c:forEach>
				<div style="text-align: right; margin: 20px;">
					<input type="submit" value="Сохранить"/>
				</div>
				</form>
			</div>			
		</div>
	</section>
</body>
</html>