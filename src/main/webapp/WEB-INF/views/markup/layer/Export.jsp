<%@page trimDirectiveWhitespaces="true"%>
<%@page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

	<input type="hidden" id="export_id" name="export_id" value="{{:id}}"/>
    <table>
        <tr>
            <td></td>
            <td><div id="exportErrorMesg" style="color: red;"></div></td>
        </tr>
         <tr>
            <td></td>
            <td>Система координат: <select name="export_ref" id="export_ref">
        		<option selected="selected" value="WGS84">World Geodetic System 1984</option>
        		<c:forEach items="${msk}" var="rs" varStatus="status">
        			<option value="${rs.key}">${rs.description}</option>
        		</c:forEach>
        		</select></td>
        </tr>
        <tr>
            <td></td>
            <td>Формат: <select name="export_type" id="export_type">
        		<option value="MIFMID">Экспорт в .MIF/.MID</option>
        		<option value="CSV">Экспорт в .CSV</option>
        		<option value="TXT">Экспорт в .TXT</option>
        	</select></td>
        </tr>
    </table>
    
</form>