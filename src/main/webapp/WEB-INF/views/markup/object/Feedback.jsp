<%@page trimDirectiveWhitespaces="true"%>
<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:eval var="anonymous" expression="T(org.w2fc.conf.Constants).isAnonymous()"/>
<form id="_FeedbackForm_"> 
    <table>
    <tr>
    	<td>Объект:</td><td>${object.name}</td>
    </tr>
    <tr>
    	<c:if test="${!anonymous}">
    		<c:set var="fromName" value="${auth.lastName} ${auth.firstName}" scope="request"/>
    	</c:if>
    	<td>От имени:</td><td><input name="from" value="${fromName}"/></td>    	
    </tr>
    <tr>
    	<c:if test="${!anonymous}">
    		<c:set var="fromMail" value="${auth.email}" scope="request"/>
    	</c:if>
    	<td>Обратный адрес:</td><td><input name="frommail" value="${fromMail}"/></td>    	
    </tr>
    <tr>
    	<td colspan="2">Комментарий:<br/><textarea name="comment" rows="5"></textarea></td>
    </tr>
    </table>
    <c:forEach items="${layers}" var="layer" varStatus="status">
    <input name="mails[${status.index}]" type="hidden" value="${layer.ownerEmail}">    
    </c:forEach>
    <input type="hidden" name="objectId" value="${object.id}">
</form>