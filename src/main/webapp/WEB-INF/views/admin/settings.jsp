<%@page trimDirectiveWhitespaces="true"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<script type="text/javascript">
	function getSettings(){
		var settings = {};
				<c:forEach items="${settings}" var="setting" varStatus="status">
					settings['${setting.key}'] = '${fn:replace(setting.value,'\\','&#92;')}';
				</c:forEach>
		return settings;		
	}
</script>
