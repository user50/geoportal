<%@page contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8">
<link rel="stylesheet" href="../../css/style.css" type="text/css" />
<link rel="stylesheet"
	href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css"
	type="text/css" />
<link rel="stylesheet"
	href="http://cdn.leafletjs.com/leaflet-0.7.1/leaflet.css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script src="http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.js"></script>
<script src="../../js/revisions.js"></script>

<style type="text/css">
#rev_list .ui-selecting {
	background: #00a4d3;
}

#rev_list .ui-selected {
	background: #00a4d3;
	color: white;
}

#rev_list {
	list-style-type: none;
	margin: 0;
	padding: 0;
}

#rev_list li {
	margin: 3px;
	padding: 0.4em;
	font-size: 12px;
	height: 18px;
	cursor: pointer;
	white-space: nowrap;
}

h1 {
  margin-left: 60px;
  margin-top: 80px;
  font-size: 20px;
}

div.popup1 {
  border: 1px solid #e7e7e7;
  -moz-border-radius: 5px;
  -webkit-border-radius: 5px;
  border-radius: 5px;
  -moz-box-shadow: 0 1px 1px #c7c7c7;
  -webkit-box-shadow: 0 1px 1px #c7c7c7;
  box-shadow: 0 0px 30px #c7c7c7;
    padding: 10px;
}

div.popup0 {
  border: none;
  margin: 0px 60px 90px 60px;
}

.common_prop {
 border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 14px;
    width: 100%;
}

</style>

<script type="text/javascript">
	$(function() {
		revisions.init('rev_list');
		revisions.draw(${portalObject.geoJSON});
	});
</script>
</head>
<body style="overflow: auto;">
	<section>
		<header>
			<div id="top-caption">
				<span><label>Геопортал Ярославской области -
						Карточка объекта</label></span>
			</div>
		</header>
		<h1 style="white-space: nowrap;">${portalObject.name}</h1>
		<div class="popup0">
			<div class="popup1">
				<form id="revisionForm" method="get">
					<input type="hidden" name="revId" id="revId" />
					<table>
						<tr>
							<td style="vertical-align: top;">
								<ol id="rev_list">
									<c:forEach items="${history}" var="entity" varStatus="status">
										<c:choose>
											<c:when test="${entity.revId == revId}">
												<li class="ui-selected" id="${entity.revId}" class="ui-widget-content"><fmt:formatDate
												value="${entity.revDate}" pattern="dd/MM/yyyy HH:mm" /></li>	
											</c:when>
											<c:otherwise>
												<li id="${entity.revId}" class="ui-widget-content"><fmt:formatDate
												value="${entity.revDate}" pattern="dd/MM/yyyy HH:mm" /></li>	
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</ol>
							</td>
							<td>
								<table class="common_prop">									
									<thead>
										<tr style="height: 0;">
											<th style="width: 170px;"></th>
											<th><form:hidden path="id" /></th>
										</tr>
									</thead>
									<tr>
										<td>Идентификатор:</td>
										<td><label>${portalObject.id}</label></td>
									</tr>
									<tr>
										<td>Название:</td>
										<td><label>${portalObject.name}</label></td>
									</tr>
									<tr>
										<td>Координаты:</td>
										<td><c:catch var="err">
												<label>${portalObject.lat} x ${portalObject.lon}</label>
											</c:catch> <c:if test="${err != null}">
												<label class="obj_geom">${portalObject.geom}</label>
												<div id="tobuffer">Показать</div>
											</c:if></td>
									</tr>
									<tr>
										<td>Создан:</td>
										<td><label>${creatorName}</label><br />
										<label><fmt:formatDate
													value="${portalObject.createdDateTime}"
													pattern="dd/MM/yyyy HH:mm" /></label></td>
									</tr>
									<tr>
										<td>Ревизия:</td>
										<td><label>${editorName}</label><br />
										<label><fmt:formatDate
													value="${portalObject.changedDateTime}"
													pattern="dd/MM/yyyy HH:mm" /></label></td>
									</tr>
								</table>
								<table>
									<tr>
										<td>Код ФИАС:</td>
										<td><label>${portalObject.fiasCode}</label></td>
									</tr>
								</table>
								<table class="common_prop" id="ObjectTagsTable">
									<tr>
										<th>Ключ</th>
										<th>Значение</th>

									</tr>
									<c:forEach items="${portalObject.tags}" var="tag"
										varStatus="status">
										<tr id="ObjectTagsTableRow${status.index}">
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
										</tr>
									</c:forEach>
								</table>
							</td>
							<td style="vertical-align: top;">
								<div id="history_map" style="width: 300px;"></div>
							</td>
						</tr>
					</table>
				</form>
			</div>
		</div>
	</section>
</body>
</html>