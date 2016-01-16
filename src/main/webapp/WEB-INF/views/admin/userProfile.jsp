<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<spring:eval var="admin" expression="T(org.w2fc.conf.Constants).isAdministrator()"/>
<spring:eval var="editor" expression="T(org.w2fc.conf.Constants).isEditor()"/>
<spring:eval var="anonymous" expression="T(org.w2fc.conf.Constants).isAnonymous()"/>
<span class="user_section">
	<span>${userName}</span> 
</span>
<div class="user_section_popup">
	<c:if test="${anonymous}">
		<div><span><a id="loginAnchor">Авторизоваться</a></span></div>
	</c:if>
	<c:if test="${admin}">
		<div><span><a onclick="$('.user_section_popup').hide();userManager.openAdminConsole()">Администрирование</a></span></div>
	</c:if>
	<div><span><a onclick="$('.user_section_popup').hide();map.getLink()">Получить ссылку на карту</a></span></div>
	<div><span><a onclick="$('.user_section_popup').hide();map.getWidget()">Скачать виджет</a></span></div>
	<div><span><a onclick="$('.user_section_popup').hide();map.toHome()">Мое местоположение</a></span></div>
	<div><span><a onclick="$('.user_section_popup').hide();map.getRoute()">Проложить маршрут</a></span></div>
    <div><span><a href="markup/docs" target="_blank" style="color: inherit; text-decoration: inherit;">Документация</a></span></div>
     <div><span><a href="registry/" target="_blank" style="color: inherit; text-decoration: inherit;">Реестры метаданных</a></span></div>
    <c:if test="${admin}">
        <div><span><a href="settings/" target="_blank" style="color: inherit; text-decoration: inherit;">Настройки системы</a></span></div>
    </c:if>
	<c:if test="${!anonymous}">
		<div><span><a onclick="userManager.exit()">Выход</a></span></div>
	</c:if>
</div>

<div id="login_section" style="display: none; position: absolute;">
<form name='f' action='j_spring_security_check' method='POST'>
							<table align="left">
								<tr id="login_notify" style="display: none; background: red">
									<td colspan="2">Имя или пароль не верны!</td>
								</tr>
								<tr>
									<td>Логин</td><td><input type='text' name='j_username' value=''></td>
								</tr>
								<tr>
									<td>Пароль</td><td><input type='password' name='j_password' /></td>
								</tr>
								<tr>
									<td colspan="2" style="border-top: 1px solid #e7e7e7;padding-top: 10px;padding-bottom: 10px;">
										<div align="right">
											<button name="submit" type="submit">Вход</button>
											<button id="login_section_close">Отмена</button>
										</div>
									</td>
								</tr>
							</table>
						</form>
</div>

<div id="link_section">
  <input style="width: 100%" id="link" readonly="readonly">
  <div align="right">
 	<button id="link_section_open">Открыть в новом окне</button>
  	<button id="link_section_rebuild">Перегенерировать</button>
  	<button id="link_section_close">Закрыть</button>
  </div>
</div>
