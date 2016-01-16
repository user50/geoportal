<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Портал ИПД</title>
<link href='http://fonts.googleapis.com/css?family=Open+Sans' rel='stylesheet' type='text/css'/>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css"
    type="text/css" />
<link rel="stylesheet" href="css/style.css" type="text/css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script src="js/jquery-migrate-1.2.1.min.js"></script>
<script type="text/javascript" src="js/jquery.jcarousel.js"></script>
 <link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.6.4/leaflet.css" />
 <link rel="stylesheet" href="css/leaflet.draw.css" />
 <link rel="stylesheet" type="text/css" href="css/jcarousel/tango/skin.css" />
 <!--[if lte IE 8]>
     <link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.6.4/leaflet.ie.css" />
     <link rel="stylesheet" href="css/leaflet.draw.ie.css" />
 <![endif]-->
</head>

<style type="text/css">

/* JCAROUSEL */

.jcarousel-skin-tango .jcarousel-container-horizontal {
    width: 837px;
    padding-top: 0px;
    padding-bottom: 0px;
}

.jcarousel-skin-tango .jcarousel-clip-horizontal {
    width: 100%;
    height: 350px;
}
.jcarousel-skin-tango .jcarousel-item {
    height: 350px;
}
.jcarousel-skin-tango .jcarousel-container {
    background: transparent;
    border: 0px;
}
.jcarousel-skin-tango .jcarousel-prev-horizontal {
    top: 0px;
}

.jcarousel-skin-tango .jcarousel-next-horizontal {
    top: 0px;
}

/* Style */

body {
	background: #deded3;
    font-family: 'Open Sans', sans-serif, "Trebuchet MS", "Helvetica", "Arial", "Verdana";
}

.page-header {
	background: #6a7386;
	width: 100%;
	height: 1200px;
}

.logo {
    background-image: url("css/images/logo.png");
    background-repeat: no-repeat;
    margin-left: auto;
    margin-right: auto;
    width: 980px;
    height: 100px;
}

.page-subheader1 {
	background-image: url("css/images/subhderbgr.png");
    background-repeat: repeat-x;
	width: 100%;
	height: 100px;
}

.page-subheader2 {
    margin-left: auto;
    margin-right: auto;
    background-image: url("css/images/bg2-bnnr.png");
    background-repeat: repeat-x;
    /*opacity: 0.4;
    filter: alpha(opacity=40);*/
    width: 980px;
    height: 500px;
}

.search-panel {
    width: 100%;
    height: 75px;
}

.projects-panel {
    margin-left: auto;
    margin-right: auto;
    // background: white;
    width: 920px;
    height: 425px;
}

.project-container{
    width: 200px;
    height: 340px;
    border: 1px solid #deded3;
}

.project-container-hdr{
    background-color: #deded3;
    width: 100%;
    height: 100px;
}

.project-container-bdy{
    background-color: #ffffff;
    width: 100%;
    height: 240px;
}

.project-container-selected{
    border: 3px solid #7ccbfc;
    color: white;
}

.project-container-hdr-selected{
    background-color: #475060;
}

.project-container-bdy-selected{
    background-color: #4d5669;
}

.page-body {
	margin-left: auto;
	margin-right: auto;
	background: #e5e5dc;
	width: 980px;
}

.block-news {
    width: 720px;
}

.block-feedback {
    //width: 258px;
}

.block-info-update {
    margin-left: auto;
	margin-right: auto;
	background: #edede7;
	width: 850px;
	height: 350px;
}

.page-footer {
    background: #1b253b;
    width: 100%;
    height: 100px;
}

a{
    color: white;
}


table{
    width: 80%;
    margin-left: auto;
    margin-right: auto;
    border-right: 1px solid white;
    border-bottom: 1px solid white;
    font-size: 12px;
}


th{
    color: white;
    padding: 5px;
    border-left: 1px solid white;
    border-top: 1px solid white;
}

td{
    color: white;
    padding: 15px;
    padding-right: 15px;
    border-left: 1px solid white;
    border-top: 1px solid white;
}

</style>

<script type="text/javascript">

jQuery(document).ready(function() {
    jQuery('#mycarousel').jcarousel({
        wrap: 'circular',
        visible: 4,
        scroll: 1
    });
});

</script>
</head>

<body>
<!-- HEADER -->
<div class=page-header>
    <div class=page-subheader1>
        <div class=logo></div>
    </div>
    <p></p>
    <table cellspacing=0 cellpadding=0 >
        <thead>
            <tr>
                <th>Наименование </th>
                <th>Заказчик</th>
                <th>Пользователь </th>
            </tr>
        </thead>
        <tbody>
            <tr>
                <td><a href="http://maps.yarcloud.ru:8081/elections/" target="_blank">Избирательная система Ярославской области</a></td>
                <td>Облизбирком, законодательные органы ЯО</td>
                <td>Органы законодательной и исполнительной власти области, избирательные комиссии</td>
            </tr>
            <tr>
                <td><a href="http://maps.yarcloud.ru:8081/elections/static/beacons.jsp" target="_blank">Система управления пространственной информацией об объектах цифрового телевидения Ярославской области</a></td>
                <td>Департамент информатизации и связи ЯО</td>
                <td>ОИВ</td>
            </tr>
            <tr>
                <td><a href="http://maps.yarcloud.ru:8081/medicine/" target="_blank">Система управления пространственной информацией об инфраструктуре здравоохранения Ярославской области (СУПИИЗ) </a></td>
                <td>Департамент здравоохранения и фармации ЯО</td>
                <td>Департамент здравоохранения и фармации ЯО (в перспективе жители и гости Ярославской области)</td>
            </tr>
            <tr>
                <td><a href="http://env.trans-monitor.ru/" target="_blank">Система управления пространственной информацией о доступности объектов социальной инфраструктуры Ярославской области (СУПИДОСИ)"</a></td>
                <td>Департамент труда и социальной поддержки населения ЯО</td>
                <td>Люди с ограниченными возможностями</td>
            </tr>
            <tr>
                <td><a href="http://yarregion.ru/depts/ukios/pages/map.aspx" target="_blank">Портал органов государственной власти Ярославской области</a></td>
                <td>Органы исполнительной власти области</td>
                <td>Органы исполнительной власти области</td>
            </tr>
            <tr>
                <td><a href="http://vms.glonass76.ru" target="_blank">Единая система мониторинга транспорта Ярославской области</a></td>
                <td>Департамент здравоохранения и фармации, дорожного хозяйства, региональной безопасности,департамент образования</td>
                <td>Департамент здравоохранения и фармации, дорожного хозяйства, региональной безопасности, департамент образования, ГИБДД</td>
            </tr>
            <tr>
                <td colspan="3"><center><b>В процессе создания</b></center></td>
            </tr>
            <tr>
                <td>Интернет-портал инфраструктуры пространственных данных Ярославской области</td>
                <td></td>
                <td></td>
            </tr>
            <tr>
                <td>Интернет-портал Ярославской области "Правовой навигатор"</td>
                <td></td>
                <td></td>
            </tr>
            <tr>
                <td>Туристический Интернет-портал Ярославской области</td>
                <td></td>
                <td></td>
            </tr>
            <tr>
                <td>Информационная система Ярославской области "Инвентаризация земель"</td>
                <td></td>
                <td></td>
            </tr>
            <tr>
                <td>Информационная система обеспечения градостроительной деятельности/Информационный банк данных градостроительной деятельности (ИСОГД/ИБДГД) Ярославской области</td>
                <td></td>
                <td></td>
            </tr>
        </tbody>
    </table>
    <!-- p><a href="http://maps.yarcloud.ru:8081/elections/" target="_blank">Избиркарта</a></p>
    <p><a href="http://maps.yarcloud.ru:8081/elections/static/beacons.jsp" target="_blank">Вышки</a></p>
    <p><a href="http://maps.yarcloud.ru:8081/medicine/" target="_blank">Инфраструктура здравоохранения Ярославской области</a></p>
        
    <p><a href="http://env.trans-monitor.ru/" target="_blank">Доступная среда</a></p>
    <p><a href="http://map.trans-monitor.ru/" target="_blank">Транс монитор</a></p-->
    
    <!-- div class=page-subheader2>
        <div class=search-panel>Проекты</div>
        <div class=projects-panel>
            <ul id="mycarousel" class="jcarousel-skin-tango">
                <li>
                    <div class=project-container>
                        <div class=project-container-hdr>Доступная среда</div>
                        <div class=project-container-bdy></div>
                    </div>
                </li>
                <li>
                    <div class="project-container project-container-selected">
                        <div class="project-container-hdr project-container-hdr-selected">Медицина</div>
                        <div class="project-container-bdy project-container-bdy-selected"></div>
                    </div>
                </li>
                <li>
                    <div class=project-container>
                        <div class=project-container-hdr>Изберкомы</div>
                        <div class=project-container-bdy></div>
                    </div>
                </li>
                <li>
                    <div class=project-container>
                        <div class=project-container-hdr>Изберкомы1</div>
                        <div class=project-container-bdy></div>
                    </div>
                </li>
                <li><img src="http://static.flickr.com/66/199481236_dc98b5abb3_s.jpg" width="200" height="340" alt="" /></li>
                <li><img src="http://static.flickr.com/75/199481072_b4a0d09597_s.jpg" width="200" height="340"  alt="" /></li>
                <li><img src="http://static.flickr.com/57/199481087_33ae73a8de_s.jpg" width="200" height="340"  alt="" /></li>
                <li><img src="http://static.flickr.com/77/199481108_4359e6b971_s.jpg" width="200" height="340"  alt="" /></li>
              </ul>
        </div>
    </div-->
</div>
<!-- BODY -->
<!-- div class=page-body>
    <ul>
        <li class=block-news style="float: left;">
            <p>Новости</p>
            <p>Новости</p>
            <p>Новости</p>
            <p>Новости</p>
        </li>
        <li style="float: left;">
            <p>Обратная связь</p>
        </li>
    </ul>
    <div style="clear: both;"></div>
    <div class=block-info-update></div>
</div-->

<!-- FOOTER -->
<div class=page-footer>
</div>


</body>
</html>
