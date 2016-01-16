<%@page trimDirectiveWhitespaces="true"%>
<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<form id="__ImportToLayer__" enctype="multipart/form-data">
    <input type="hidden" id="id" name="id" value="{{:id}}"/>
    <table>
        <tr>
            <td></td>
            <td><div id="importErrorMesg" style="color: red;"></div></td>
        </tr>
         <tr>
            <td></td>
            <td>Система координат: <select name="ref" id="ref">
        		<option selected="selected" value="WGS84">World Geodetic System 1984</option>
        		<c:forEach items="${msk}" var="rs" varStatus="status">
        			<option value="${rs.key}">${rs.description}</option>
        		</c:forEach>
        		</select></td>
        </tr>
        <tr>
            <td></td>
            <td><input type="file" id="file" name="file"></input></td>
        </tr>
        <tr>
            <td></td>
            <td><progress></progress></td>
        </tr>
    </table>
    
</form>