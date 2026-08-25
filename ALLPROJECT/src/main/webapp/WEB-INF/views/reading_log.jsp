<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.library.model.ReadingLog" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>貸出ログ</title>
    <style>
        table { border-collapse: collapse; width: 100%; margin-top: 15px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/header.jsp" />

    <h2>
        <% Boolean isAdmin = (Boolean) request.getAttribute("isAdminView"); %>
        <%= (isAdmin != null && isAdmin) ? "【管理者用】全ユーザーの貸出履歴一覧" : "マイ貸出履歴一覧" %>
    </h2>

    <%
        List<ReadingLog> logs = (List<ReadingLog>) request.getAttribute("logs");
        if (logs == null || logs.isEmpty()) {
    %>
        <p>現在、貸出履歴はありません。</p>
    <%
        } else {
    %>
        <table>
            <thead>
                <tr>
                    <th>Log ID</th>
                    <th>User ID</th>
                    <th>ISBN</th>
                    <th>書籍タイトル</th>
                    <th>貸出日時</th>
                </tr>
            </thead>
            <tbody>
                <% for (ReadingLog log : logs) { %>
                    <tr>
                        <td><%= log.getLogId() %></td>
                        <td><%= log.getUserId() %></td>
                        <td><%= log.getIsbn() %></td>
                        <td><%= log.getBookTitle() %></td>
                        <td><%= log.getRentDate() %></td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    <%
        }
    %>
</body>
</html>