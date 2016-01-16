<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:eval var="editor" expression="T(org.w2fc.conf.Constants).isEditor()"/>
<spring:eval var="admin" expression="T(org.w2fc.conf.Constants).isAdministrator()"/>
<spring:eval var="anonymous" expression="T(org.w2fc.conf.Constants).isAnonymous()"/>
<spring:eval var="userName" expression="T(org.springframework.security.core.context.SecurityContextHolder).getContext().getAuthentication().getName()"/>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Реестр метаданных</title>
<link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css"
    type="text/css" />
<script src="http://code.jquery.com/jquery-1.9.1.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>
<script src="../js/jquery-migrate-1.2.1.min.js"></script>
<script src="../js/jquery.cookie.js"></script>
<script src="../js/jquery.stylesheet.js"></script>
<script src="../js/jquery.jstree.js"></script>
<script src="../js/jsrender.js"></script>
<script src="../js/jquery.form.min.js"></script>
<script src="../js/jquery.minicolors.min.js"></script>
<script src="../js/jquery.ajaxMultiQueue.js"></script>
<script src="../js/jquery.autocomplete.js"></script>
<script src="../js/jquery.raty.js"></script>
<link rel="stylesheet" href="../css/jquery.minicolors.css" />
<link rel="stylesheet" href="../css/style.css" type="text/css" />
 <link rel="stylesheet" href="../css/jquery.raty.css" type="text/css" />
 
 <c:set var="isStandalone" value="true"/>
 
</head>
<body style="overflow: auto;">
<script type="text/javascript">
        function isEditor(){
            return <c:out value="${editor}"></c:out> ;  
        }
        function isAnonymous(){
            return <c:out value="${anonymous}"></c:out> ;   
        }
        function getUserName(){
            return '<c:out value="${userName}"></c:out>' ;  
        }
        
        function showMask() {
        	var mask = document.getElementById('loadmask');
        	var img = document.getElementById('loadimg');
        	mask.style.display = 'block';
        	wWidth = (('innerWidth' in window) ? window.innerWidth : document.documentElement.clientWidth);
        	wHeight = (('innerHeight' in window) ? window.innerHeight
        			: document.documentElement.clientHeight);
        	mask.style.height = wHeight + 'px';
        	mask.style.width = wWidth + 'px';
        	img.style.left = wWidth / 2 + 'px';
        	img.style.top = wHeight / 2 + 'px';
        }

        function hideMask() {
        	var mask = document.getElementById('loadmask');
        	mask.style.display = 'none';
        }
        
        function alertMessage(message) {
        	window.alertDiv = window.alertDiv || $("<div id='drop_down_bar'/>").appendTo("body");
        	window.alertDiv.text(message).slideDown(350, function() {
        			$(this).delay(4000).slideUp("slow");
        	});
        }
        
        function save(){
            showMask();
            var id = NaN;
            try{
                id = parseInt($("#_LayerEditForm_ #id")[0].value);
            }catch(e){}
            $.post( 
                    isNaN(id) ? '../layer/createOrUpdate' : 'update', 
                $('#_LayerEditForm_').serialize(),
                'json' // I expect a JSON response
            ).done(function(res){
                try{
                    if(isNaN(id)){
                        var id1 = parseInt(res);
                    	$("#_LayerEditForm_ #id").val(id1);
                    }
                    hideMask();
                    alertMessage("Данные сохранены успешно.");
                }catch(e){
                    alertMessage("Ошибка обработки данных.");
                }
            }).fail( function(xhr, textStatus, errorThrown) {
                hideMask();
                //alertMessage(xhr.responseText);
                alertMessage("Ошибка сохранения.");
            });
        }
        function closeWindow(){
            //window.location.href=".";
            window.close();
        }
        
        $(document).ready(function() {
        	var rO = false;
        	var id = parseInt($("#_LayerEditForm_ #id")[0].value);
            if($.cookie("layer" + id + "like"))rO = true;
             $('#stars').raty({readOnly: rO, score: $('#stars').attr('likes'),  path: '../css/images', 
             	click: function(score, evt) {
             			$.cookie("layer" + id + "like", true);
                 		 $.post( 
                 				 '../layer/setLike',
                 				 {id:id, likes:score}
                 	     ).done(function(res){
                 	    	 alertMessage("Ваш голос учтен!");      
                 	     });
                 	
             	}
             });
        });
        
</script>

<div class="ui-dialog ui-widget ui-widget-content ui-corner-all ui-front gis-card ui-dialog-buttons ui-draggable ui-resizable">
    <div class="ui-dialog-titlebar ui-widget-header ui-corner-all ui-helper-clearfix">
        <span id="ui-id-3" class="ui-dialog-title">${title}</span>
    </div>
    <div id="pageDialog" class="ui-dialog-content ui-widget-content">
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
    <%@ include file="LayerEdit.jsp" %>
    </div>
</div>

<c:if test="${editor}">
<div style="margin: 10px;">
    <button onclick="save()">Сохранить</button>
    <button onclick="closeWindow()">Закрыть</button>
</div>
</c:if>
    
    <div style="display: none" id="loadmask">
        <img id="loadimg" src="../css/images/ajax-loader.gif">
    </div>  
</body>
</html>