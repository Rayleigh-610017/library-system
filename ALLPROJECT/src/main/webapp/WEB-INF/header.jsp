<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.example.library.model.Account" %>
<%
    Account loginUser = (Account) session.getAttribute("loginUser");
%>
<header style="background: #f4f4f4; padding: 10px; margin-bottom: 20px;">
    <span><strong>図書管理システム</strong></span> | 
    <a href="${pageContext.request.contextPath}/books">本一覧・検索</a> | 

    <% if (loginUser != null) { %>
        <a href="${pageContext.request.contextPath}/rent">マイ貸出ログ (中間テーブル)</a> | 
        <% if ("Admin".equals(loginUser.getRole())) { %>
            <!-- ★ここを修正：直接JSPではなくBookServletへパラメータ(view=admin)を送る -->
            <a href="${pageContext.request.contextPath}/books?view=admin" style="color: red;">[管理者] 本データ管理</a> | 
        <% } %>
        <span>ログイン中: ID <%= loginUser.getUserId() %> (<%= loginUser.getRole() %>)</span>
        <a href="${pageContext.request.contextPath}/auth?action=logout">ログアウト</a>
    <% } else { %>
        <span>状態: Guest (機能制限中)</span> | 
        <a href="${pageContext.request.contextPath}/login.jsp">ログイン / サインイン</a>
    <% } %>
</header>