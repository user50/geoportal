<%@ page language="java" contentType="text/html; charset=utf8" pageEncoding="utf8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf8">
    <title>Отчет по интеграционному сервису</title>
    <link rel="stylesheet" href="../css/style.css" type="text/css" />
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.10.3/themes/smoothness/jquery-ui.css" type="text/css" />

    <script src="http://code.jquery.com/jquery-1.9.1.js"></script>
    <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.js"></script>

    <script type="text/javascript">

    </script>

    <style type="text/css">
        /* <![CDATA[ */

        body {
            font-size: 13px;
            background: #deded3;
            color: #6c6c6c;
        }

        table {
            font-size: 15px;
            color: #6с6с6с;
            background-color: #ffffff;
            width: 100%;
        }

        th {
        }

        tr{
            border-bottom: 0px solid #000000;
            height: 45px;
        }

        td{
            padding: 0px;
        }

        .main {
            margin-left: auto;
            margin-right: auto;
            width: 1000px;
            background-color: white;
            border: 1px solid #e2e2e2;
        }

        .header {
            background-color: #eeeeee;
            border: 1px solid #e2e2e2;
            height: 45px;
            position: relative;
        }

        .header-label {
            line-height: 45px;
            margin-left: 25px;
            font-weight: bold;
            font-size: 12;
            width: 880px;
            display: block;
            float: left;
        }

        .header-close{
            display: block;
            background: url("../css/admin/close.png");
            background-repeat: no-repeat;
            width: 12px;
            height: 20px;
            right: 25px;
            top: 18px;
            position: absolute;
            cursor: pointer;
        }

        .input1 {
            width: 210px;
            height: 30px;
            border: 1px solid #e2e2e2;
            border-radius: 5px;
            padding: 6px;
        }

        .num{
            text-align: center;
            font-size: 14px;
        }

        .desc{
            font-size: 14px;
        }

        .tools-role {
            width: 100%;
            padding-top: 30px;
        }

        .tools-btn{
            width: 100%;
            margin-top: 30;
        }

        /* ]]> */
    </style>

    <script type="text/javascript">
        function deleteUser(){
            document.forms["remove"].submit();
        }

        function close1(){
            open(location, '_self').close();
        }

        $(function() {
            $('#user').submit(function() {
                var p1 = $("#user").find("#passwd").val();
                var p2 = $("#user").find("#confirmPasswd").val();

                if(p1 == p2){
                    return true;
                }

                alert("Пароли не совпадают. Проверьте введенные данные");
                return false;
            });
        });
    </script>

</head>
<body>
<div class=main>
    <div class=header>
        <span class=header-label>ОТЧЕТ ПО ИНТЕГРЦИОННОМУ СЕРВИСУ</span>
        <span class=header-close  onclick="close1();"></span>
    </div>
    <p></p>
    <table>
        <thead>
        <tr style="height: 0;">
            <th style="width: 150px;">Дата</th>
            <th style="width: 400px;">Ссылка на отчет</th>
            <th style="width: 100px;">Пользователь</th>
        </tr>
        </thead>
        <c:forEach items="${list}" var="item">
            <tr>
                <td><fmt:formatDate type="both" value="${item.date}" /></td>
                <td><a href="../report/<c:out value="${item.reportFile}" />" target="_blank" style="color: inherit; text-decoration: inherit;"><c:out value="${item.reportFile}" /></a></td>
                <td><c:out value="${item.userName}" /></td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
