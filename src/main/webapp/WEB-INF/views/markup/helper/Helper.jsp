<%@page trimDirectiveWhitespaces="true"%>
<%@page contentType="text/html; charset=UTF-8" %>

<script id="helper_body" type="text/x-jquery-tmpl">
	<table style="width:100%">
		<tr>
			<td colspan=2><div>Поиск 
				<a style="color: #858383;border-bottom: 1px dotted #858383;" id='by_tags'>по тегам</a>
				| <a style="color: #858383;" id='by_addr'>по адресу</a></div></td>
		</tr>
		<tr>
			<td colspan=2 style="border-bottom:1px solid #303030;">
				<input id="helper_search"/>
				<input type="hidden" id="helper_fias_search"/>
				<span id="search"></span>
			</td>
		</tr>
	    <tr>
			<td colspan=2><div>Фильтр по названию</div><input id="helper_filter"/></td>
		</tr>
		<tr>
			<td colspan=2 style="padding-top: 0px;">
				<a style="color: #858383;border-bottom: 1px dotted #858383;" id='by_name'>По названию</a>
				<a style="color: #858383;border-bottom: 1px dotted #858383;" id='by_far'>По отдаленности</a>
			</td>
		</tr>
		<tr>
			<td><div id="helper-status">0 объектов</div></td>
			<td style="text-align: right;"><a style="color: #858383;border-bottom: 1px dotted #858383;" id='history_back'>назад</a></td>
		</tr>
	</table>
	<div id="helper_err_mess">Слишком много объектов в списке.<br/>Введите ограничение в фильтре.</div>
	<div id="helper_obj_list"></div>
</script>


<script id="helper_section" type="text/x-jquery-tmpl">
{{for data}}
<table id="link_{{:id}}" class='helper_section'>
<tr>
<td>
	{{if name == '' || name == null}}
		<a title="Без имени" >Без имени</a>
	{{else}}
		<a title="{{:name}}" >{{:name}}</a>
	{{/if}}
		<span>{{:lat}} x {{:lon}}</span>
</td>
</tr>
</table>
{{/for}}
</script>