<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:eval var="editor" expression="T(org.w2fc.conf.Constants).isEditor()"/>
<spring:eval var="admin" expression="T(org.w2fc.conf.Constants).isAdministrator()"/>
<spring:eval var="anonymous" expression="T(org.w2fc.conf.Constants).isAnonymous()"/>
<spring:eval var="userName" expression="T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()"/>
<spring:eval var="userPermissionArea" expression="T(org.w2fc.conf.Constants).permissionArea(T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication())"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="description" content="Пространственные данные Ярославской области">
<title>Геопортал Ярославской области</title>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.11.2/themes/smoothness/jquery-ui.css"
    type="text/css" />
<script src="http://code.jquery.com/jquery-1.10.2.js"></script>
<script src="http://code.jquery.com/ui/1.11.2/jquery-ui.js"></script>
<script src="js/jquery-migrate-1.2.1.min.js"></script>
<script src="js/jquery.cookie.js"></script>
<script src="js/jquery.stylesheet.js"></script>
<script src="js/jquery.jstree.js"></script>
<script src="js/jsrender.js"></script>
<script src="js/jquery.form.min.js"></script>
<script src="js/jquery.minicolors.min.js"></script>
<script src="js/jquery.ajaxMultiQueue.js"></script>
<script src="js/jquery.raty.js"></script>
<script src="js/jquery.numeric.js"></script>
<script src="http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.js"></script>
<script src="js/leaflet.draw-src.js"></script>
<script src="js/leaflet.measurecontrol.js"></script>
<script src="js/leaflet.permissioncontrol.js"></script>
<script src="js/leaflet.zoomslider.js"></script>
<script src="js/leaflet.mouseposition.js"></script>
<script src="js/leaflet.rucadastre.js"></script>
<script src="js/leaflet-routing-machine.min.js"></script>
<script src="js/leaflet.markercluster-src.js"></script>
<script src="js/esri-leaflet.js"></script>
<script src="js/esri-leaflet-geocoder.js"></script>
<script src="http://maps.google.com/maps/api/js?v=3&sensor=false"></script>
<script src="js/Google.js"></script>
<script src="js/jquery.url.min.js"></script>
<script src="js/Leaflet.Weather.js"></script>
<c:if test="${!anonymous}">
	<script src="js/leaflet.print.js"></script>
	<script src="js/leaflet-areaselect.js"></script>
</c:if>
<script src="js/Control.Loading.js"></script>
<script src="js/leaflet.label-src.js"></script>    
 <link rel="stylesheet" href="css/leaflet-routing-machine.css" />
 <link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.css" />
 <link rel="stylesheet" href="css/leaflet.draw.css" />
 <link rel="stylesheet" href="css/leaflet.measurecontrol.css" />
 <link rel="stylesheet" href="css/leaflet.mouseposition.css" />
 <link rel="stylesheet" href="css/MarkerCluster.Default.css" />
 <link rel="stylesheet" href="css/MarkerCluster.css" />
 <link rel="stylesheet" href="css/jquery.minicolors.css" />
 <link rel="stylesheet" href="css/Control.Loading.css" />
 <link rel="stylesheet" href="css/Leaflet.Weather.css"  type="text/css" />
 <!--[if lte IE 8]>
     <link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.7.1/leaflet.ie.css" />
     <link rel="stylesheet" href="css/leaflet.draw.ie.css" />
 <![endif]-->
 <link rel="stylesheet" href="css/leaflet.zoomslider.css" type="text/css" />
 <link rel="stylesheet" href="css/style.css" type="text/css" />
 <link rel="stylesheet" href="css/jquery.raty.css" type="text/css" />
 <c:if test="${!anonymous}">
 	<link rel="stylesheet" href="css/leaflet.print.css" type="text/css" />
 	<link rel="stylesheet" href="css/leaflet-areaselect.css" type="text/css" />
 </c:if>
 <link rel="stylesheet" href="css/leaflet.label.css" type="text/css" />
 <script src="js/gis.js"></script>
 <script src="js/layers.js"></script>
 <script src="js/user.js"></script>  
 <script src="js/helper.js"></script>   
 <script src="js/index.js"></script>
 <script src="js/autocomplete.js"></script>
 <script src="js/keep-alive.js"></script>
 
</head>
<body onresize="onResize()">
<script type="text/javascript">
		function isEditor(){
			return <c:out value="${editor}"></c:out> ;	
		}
		function isAdmin(){
			return <c:out value="${admin}"></c:out> ;	
		}
		function isAnonymous(){
			return <c:out value="${anonymous}"></c:out> ;	
		}
		function getUserName(){
			return '<c:out value="${userName}"></c:out>' ;	
		}
		function getUserPermissionObjectId(){
			return <c:out value="${userPermissionArea}"></c:out>;	
		}
	</script>
	<c:import url="admin/settings"/>
<div id="top-caption"><span><label>Геопортал Ярославской области</label></span>
		<c:import url="admin/userProfile"/>
</div>
<div id="map" center="<%= request.getParameter("c")%>" zoom="<%= request.getParameter("z")%>"></div>
	<div id="open-left-tool" class="common-widget"></div>
	<div id="main-tool" class="common-widget">
	<div class="common-widget" id="left-tool">Слои
		<div id="close-left-tool"></div>
	</div>
	<div class="common-widget" id="tabs">
		<ul>
			<li><div><a href="#tabs-1">Слои</a></div></li>
		</ul>
		<div id="tabs-1">
					<div><c:if test="${editor}">
							<div style="border-bottom: 1px solid #e7e7e7; padding: 10px;">
								<button id="add_layer">Зарегистрировать слой</button>
							</div>
							</c:if>
    						<div style="overflow: auto;" id="layers" layers="<%= request.getParameter("layers")%>"></div>	
  					</div>
  							
		</div>
	</div>
	<div class="bottom-pane">
		<button id="reset_all">Очистить</button>
        <button id="show_report">Отчет</button>
	</div>
	</div>
	
	<div id="open-right-tool" class="common-widget"></div>
	<div class="common-widget" id="helper-tool">
		<div class="common-widget" id="right-tool">
			<div id="close-right-tool"></div>
		</div>
		<div id="helper"></div>
	</div>
	
	<div style="display: none" id="loadmask">
		<img id="loadimg" src="css/images/ajax-loader.gif">
	</div>
	
	<script id="esri_legend_template" type="text/x-jquery-tmpl">
	{{for data}}
		<b>{{:layerName}}</b>
		<table>
		{{for legend}}
		<tr>
		<td>
			<img src="data:{{:contentType}};base64,{{:imageData}}"/>
		</td>
		<td>
			<label>{{:label}}</label>
		</td>
		</tr>
		{{/for}}
		</table>
	{{/for}}
	</script>		
</body>
</html>
