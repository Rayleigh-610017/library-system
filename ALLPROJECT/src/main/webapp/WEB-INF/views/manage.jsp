<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.library.model.Book" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>【管理者】書籍データ管理</title>
    <style>
        table { border-collapse: collapse; width: 100%; margin-top: 15px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .form-section { background: #f9f9f9; padding: 15px; border: 1px solid #ddd; margin-bottom: 20px; }
        .form-group { margin-bottom: 10px; }
        .form-group label { display: inline-block; width: 100px; }
    </style>
</head>
<body>
    <jsp:include page="/WEB-INF/header.jsp" />

    <h2>【管理者用】書籍の新規登録・削除</h2>

    <!-- 1. 書籍新規追加フォーム -->
    <div class="form-section">
        <h3>1. 新しい書籍の登録</h3>
        <form action="${pageContext.request.contextPath}/books" method="post">
            <input type="hidden" name="action" value="add">
            <div class="form-group">
                <label>ISBN:</label>
                <input type="text" name="isbn" required placeholder="例: 978-4000000001">
            </div>
            <div class="form-group">
                <label>タイトル:</label>
                <input type="text" name="title" required>
            </div>
            <div class="form-group">
                <label>カテゴリ:</label>
                <input type="text" name="category" required>
            </div>
            <div class="form-group">
                <label>著者:</label>
                <input type="text" name="writer" required>
            </div>
            <div class="form-group">
                <label>出版社:</label>
                <input type="text" name="publisher" required>
            </div>
            <div class="form-group">
                <label>URL/画像:</label>
                <input type="text" name="url">
            </div>
            <button type="submit">書籍を登録する</button>
        </form>
    </div>

    <!-- 2. 既存書籍の一覧・削除フォーム -->
    <h3>2. 登録済み書籍一覧（削除処理）</h3>
    <%
        List<Book> books = (List<Book>) request.getAttribute("books");
        if (books == null || books.isEmpty()) {
    %>
        <p>登録されている書籍はありません。</p>
    <%
        } else {
    %>
        <table>
            <thead>
                <tr>
                    <th>ISBN</th>
                    <th>タイトル</th>
                    <th>カテゴリ</th>
                    <th>著者</th>
                    <th>出版社</th>
                    <th>ステータス</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <% for (Book book : books) { %>
                    <tr>
                        <td><%= book.getIsbn() %></td>
                        <td><%= book.getTitle() %></td>
                        <td><%= book.getCategory() %></td>
                        <td><%= book.getWriter() %></td>
                        <td><%= book.getPublisher() %></td>
                        <td><%= book.getRentStatus() %></td>
                        <td>
                            <form action="${pageContext.request.contextPath}/books" method="post" style="margin:0;" onsubmit="return confirm('本当に削除しますか？');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="isbn" value="<%= book.getIsbn() %>">
                                <button type="submit" style="color: red;">削除</button>
                            </form>
                        </td>
                    </tr>
                <% } %>
            </tbody>
        </table>
    <%
        }
    %>
</body>
</html>