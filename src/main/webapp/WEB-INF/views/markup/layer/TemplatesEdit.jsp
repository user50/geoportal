<%@page trimDirectiveWhitespaces="true"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8">
<title>Редактор шаблонов</title>
<link rel="stylesheet" href="../css/style.css" type="text/css" />
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css"
	type="text/css" />

<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>

<script src="//cdn.ckeditor.com/4.4.3/full/ckeditor.js"></script>

<script src="../js/templates.js"></script>

<style type="text/css">
/* <![CDATA[ */
body {
	font-size: 13px;
	background: #deded3;
	color: #6c6c6c;	
}

table {
	font-size: 15px;
	color: #6с6с6с;
	background-color: #ffffff;
	width: 100%;
	margin: 0 auto;
}

td {
	vertical-align: top;
}

h1{
	text-align: center;
	font-size: 18px;
}

.container{
	text-align: center;
}

#tmpl_list .ui-selecting { background: #00a4d3; }
#tmpl_list .ui-selected { background: #00a4d3; color: white; }
#tmpl_list { list-style-type: none; margin: 0; padding: 0; max-height: 800px; overflow: scroll;}
#tmpl_list li { margin: 3px; padding: 0.4em; font-size: 12px; height: 18px; cursor: pointer;}

.comment {
	font-size: 10px;
	color: gray;
}

/* ]]> */
</style>

<script type="text/javascript">
	$(function() {
		templates.init('tmpl_list');
		templates.setEditor(${tmplId});
	});
</script>

</head>
<body>
<h1>Редактор шаблонов</h1>
<div class="container">
	<table style="width:800px">
		<tr>
			<td width="30%">
				<ol id="tmpl_list">
					<c:forEach items="${templates}" var="tmpl" varStatus="status">
						<li id="${tmpl.id}" class="ui-widget-content">${tmpl.name}</li>
					</c:forEach>
				</ol>
			</td>
			<td>
				<button id="add" onclick="templates.clearFields()">Создать новый шаблон</button>
				<hr/>
				<form action="createOrUpdate" method="POST">
					<input type="hidden" id="id" name="id">
					<p>
					Наименование шаблона: <input id="name" name="name" style="width: 228px; ">
					 <input type="button" onclick="templates.deleteTemplate();return false;" value="Удалить шаблон"/>
					</p>
					<p>
						<textarea id="template" name="template" style="width: 100%; height: 157px"></textarea>					 
  					</p>
					<button type="submit">Сохранить</button>
				</form>
			</td>
		</tr>
		<tr>
			<td colspan="2">
			<div class="comment">
				*В отображении всплывающей подсказки для объекта используется шаблонизатор <a href="http://www.jsviews.com/#jsrender" target="_blank">JSRender</a>. 
				Для получения свойств объекта используйте синтаксис {{:key}} , где в качестве key используется значение ключа тега объекта. Если в именах переменных 
				используются + или - используйте такую схему {{:~root['key-key']}}
			</div> 
			</td>
		</tr>
	</table>
</div>
<div style="display: none" id="loadmask">
		<img id="loadimg" src="../css/images/ajax-loader.gif">
	</div>	
</body>
</html>
