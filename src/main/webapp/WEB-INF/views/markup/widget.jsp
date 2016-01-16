<%@page trimDirectiveWhitespaces="true"%>
<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div id="ipdyo_wdg" style="width:600px;height:600px"></div>
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.js"></script>
<script src="${href}/js/jquery.url.min.js"></script>
<script>
function init() {
	var map = L.map('ipdyo_wdg').setView([ ${lat}, ${lon} ], ${zoom});
	L.tileLayer('http://maps.yarcloud.ru:8088/enk/{z}/{x}/{y}.png',{attribution : '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'}).addTo(map);
	var lIds = [];
	<c:forEach items="${layers}" var="layer">
		<c:if test="${layer.typeId == 1}">
			lIds.push(${layer.id});
		</c:if>
		<c:if test="${layer.typeId == 2}">
			openNativeWMS(map, '${layer.url}');
		</c:if>
		<c:if test="${layer.typeId == 3}">
			L.tileLayer('${layer.url}', {}).addTo(map);
		</c:if>
		<c:if test="${layer.typeId == 5}">
			L.esri.dynamicMapLayer('${layer.url}', {useCors: false}).addTo(_map);
		</c:if>
	</c:forEach>
	if(lIds.length > 0)openWMS(map,lIds.join());
	map.on('click', function(e) {
		var url = '${href}/?c=${lat},${lon}&z=${zoom}&layers=${vlayers}';
		window.open(url);
	});
}
function loadTheme() {
	var href = "http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.css";
	var newlink = document.createElement("link");
	newlink.setAttribute("rel", "stylesheet");
	newlink.setAttribute("type", "text/css");
	newlink.setAttribute("href", href);
	document.getElementsByTagName("head")[0].appendChild(newlink);
}
function openWMS(map, layerIds){
	if(layerIds == '')return;
	L.tileLayer.wms('${href}/wms/', {
	    layers: layerIds,
	    format: 'image/png',
	    transparent: true,
	    attribution: "wms",
	    version : ${ver},
	    crs: L.CRS.Simple
	}).addTo(map).bringToFront();
}
function openNativeWMS(map, u){
	var wmsUrl = u.replace(/([^?]*)\?(.*)/,'$1');
	var wmsLayer =  url('?LAYERS', u);
	var wmsSrs =  url('?SRS', u);
	var wms = L.tileLayer.wms(wmsUrl + '?f=image', {
	    format: 'image/png',
	    transparent: true,
	    attribution: "wms",
	    layers: (null == wmsLayer)?'':wmsLayer        		    
	}).addTo(map);
}
window.onload = function(){
	loadTheme();
	init();
};
</script>