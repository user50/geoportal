<%@ page language="java" contentType="text/html; charset=utf8" pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf8">
    <title>HQL</title>
    <style>
    table
    {
    border-collapse:collapse;
    }
    table,th, td
    {
    border: 1px dashed black;
    }
    </style>
    <script language="Javascript" type="text/javascript" src="http://www.cdolivet.com/editarea/editarea/edit_area/edit_area_full.js"></script>	
	<script language="Javascript" type="text/javascript">
		// initialisation
		editAreaLoader.init({
			id: "query",	// id of the textarea to transform		
			start_highlight: true,	// if start with highlight
			allow_resize: "both",
			allow_toggle: true,
			word_wrap: true,
			language: "en",
			syntax: "sql",
			font_size: 12
		});
	</script>
</head>
<body>
<div>
    <h1>HQL</h1>
    
    <form:form method="post" action="">
        <textarea id=query name=query rows="12" style="width:100%;">${query}</textarea>
        <br/>
        <button>
            <span>RUN</span>
        </button>
    </form:form>
    
    <c:if test="${not empty result}">
        <table style="width:100%">
            <tbody>
                <c:forEach items="${result}" var="resEntry">
                    <tr>
                        <td>${resEntry}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
    
</div>
<div style="color: red">
<c:if test="${not empty _exception}">
    ${_exception}
</c:if>
</div>

</body>
</html>