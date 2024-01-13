<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="constants.ForwardConst" %>

<%-- ページスコープ --%>
<c:set var="action" value="${ForwardConst.ACT_EMP.getValue()}" />
<c:set var="commIdx" value="${ForwardConst.CMD_INDEX.getValue()}" />

<!DOCTYPE html>
<html lang="ja">
    <head>
        <meta charset="UTF-8">
                <title><c:out value="日報管理システム" /></title> <%-- なんでc:out？？？ --%>
        <link rel="stylesheet" href="<c:url value='/css/reset.css' />">
        <link rel="stylesheet" href="<c:url value='/css/style.css' />">
    </head>
    <body>
        <div id="wrapper">
            <div id="header">
                <div id="header_menu">
                    <h1><a href="<c:url value='?action=${action}&command=${commIdx}' />">日報管理システム</a></h1>
                </div>
            </div>
            <div id="content">${param.content}</div>
            <div id="footer">by Taro Kirameki.</div>
        </div>
    </body>
</html>