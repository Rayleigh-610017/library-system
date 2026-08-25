<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="com.example.library.model.Book"%>
<%@ page import="com.example.library.model.Account"%>

<%
// セッションからログインユーザー・エラー情報を取得
Account loginUser = (Account) session.getAttribute("loginUser");
boolean isLoggedIn = (loginUser != null);
boolean isAdmin = (isLoggedIn && "Admin".equals(loginUser.getRole()));

// 管理者以外がアクセスした場合は一般画面へリダイレクト
if (!isAdmin) {
    response.sendRedirect(request.getContextPath() + "/books");
    return;
}

String rentError = (String) session.getAttribute("rentError");
if (rentError != null) { 
    session.removeAttribute("rentError"); 
}

// リクエスト属性からデータ一覧・カテゴリ一覧を取得
List<Book> books = (List<Book>) request.getAttribute("books");
List<String> categories = (List<String>) request.getAttribute("categories");

// 検索用パラメータの取得（再表示用）
String keyword = request.getParameter("keyword") != null ? request.getParameter("keyword") : "";
String selectedCategory = request.getParameter("category") != null ? request.getParameter("category") : "";

// 管理者画面用のビューパラメータ文字列（常にview=adminを維持するため）
String viewParam = "&view=admin";
String viewParamOnly = "?view=admin";
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>書籍データ管理（管理者）</title>
</head>
<body>
	<jsp:include page="/WEB-INF/header.jsp" />

	<h2>書籍一覧・貸出管理（管理者画面）</h2>

	<%
	if (rentError != null) {
	%>
	<p style="color: red; font-weight: bold;"><%=rentError%></p>
	<%
	}
	%>

	<!-- ★新規登録フォーム -->
	<div class="form-section" style="border: 1px solid #ccc; padding: 15px; margin-bottom: 20px;">
		<h3>1. 新しい書籍の登録</h3>
		<form action="${pageContext.request.contextPath}/books" method="post">
			<input type="hidden" name="action" value="add">
			<input type="hidden" name="view" value="admin">
			
			<div class="form-group" style="margin-bottom: 8px;">
				<label>ISBN:</label> <input type="text" name="isbn" required placeholder="例: 978-4000000001">
			</div>
			<div class="form-group" style="margin-bottom: 8px;">
				<label>タイトル:</label> <input type="text" name="title" required>
			</div>
			<div class="form-group" style="margin-bottom: 8px;">
				<label>カテゴリ:</label> <input type="text" name="category" required>
			</div>
			<div class="form-group" style="margin-bottom: 8px;">
				<label>著者:</label> <input type="text" name="writer" required>
			</div>
			<div class="form-group" style="margin-bottom: 8px;">
				<label>出版社:</label> <input type="text" name="publisher" required>
			</div>
			<div class="form-group" style="margin-bottom: 8px;">
				<label>URL/画像:</label> <input type="text" name="url">
			</div>
			<button type="submit">書籍を登録する</button>
		</form>
	</div>

	<!-- ★検索フォーム & 簡単検索エリア -->
	<div class="search-section" style="margin-bottom: 15px;">
		<h3>書籍の検索</h3>
		
		<form action="books" method="get" style="margin-bottom: 8px;">
			<!-- 管理者画面であることを維持する隠しフィールド -->
			<input type="hidden" name="view" value="admin">

			<!-- カテゴリ選択を維持する隠しフィールド -->
			<% if (!selectedCategory.isEmpty()) { %>
			<input type="hidden" name="category" value="<%=selectedCategory%>">
			<% } %>

			<input type="text" name="keyword" value="<%=keyword%>" placeholder="タイトル・著者・カテゴリ・ISBN">
			<button type="submit">検索</button>

			<% if (!keyword.isEmpty() || !selectedCategory.isEmpty()) { %>
			<a href="books<%=viewParamOnly%>" style="margin-left: 10px; font-size: 0.9em;">検索を解除</a>
			<% } %>
		</form>

		<div style="margin-top: 10px;">
			【簡単検索】

			<% String allActive = selectedCategory.isEmpty() ? "font-weight:bold;" : ""; %>
			<a href="books<%=viewParamOnly%>" style="margin-right: 8px; <%=allActive%>">すべて</a>

			<%
			if (categories != null) {
				for (String cat : categories) {
					String activeStyle = cat.equals(selectedCategory) ? "font-weight:bold;" : "";
					String encodedCat = java.net.URLEncoder.encode(cat, "UTF-8");
					String keywordParam = !keyword.isEmpty() ? "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8") : "";
			%>

			<a href="books?category=<%=encodedCat%><%=keywordParam%><%=viewParam%>"
				style="margin-right: 8px; <%=activeStyle%>"> <%=cat%>
			</a>

			<%
				}
			}
			%>
		</div>
	</div>

	<!-- ★書籍一覧テーブル（強制返却・編集・削除機能付き） -->
	<table border="1" cellpadding="8" style="margin-top: 10px; border-collapse: collapse;">
		<tr>
			<th>画像</th>
			<th>ISBN</th>
			<th>タイトル</th>
			<th>ジャンル</th>
			<th>著者</th>
			<th>出版元</th>
			<th>状態</th>
			<th>操作（管理者用）</th>
		</tr>
		<%
		if (books != null) {
			for (Book book : books) {
		%>
		<tr>
			<td><img src="<%=book.getUrl()%>" width="50" alt="img"></td>
			<td><%=book.getIsbn()%></td>
			<td><%=book.getTitle()%></td>
			<td><%=book.getCategory()%></td>
			<td><%=book.getWriter()%></td>
			<td><%=book.getPublisher()%></td>
			<td>
				<% if ("Rent".equals(book.getRentStatus())) { %> 
					<span style="color: red; font-weight: bold;">Rent (貸出不可)</span> 
				<% } else { %>
					<span style="color: green;">貸出可能</span> 
				<% } %>
			</td>
			<td>
				<%
				// 貸出中の場合のみ「強制返却」ボタンを表示
				if ("Rent".equals(book.getRentStatus())) {
				%>
				<form action="books" method="post" style="display: inline; margin-right: 5px;" onsubmit="return confirm('この書籍を強制返却しますか？');">
					<input type="hidden" name="action" value="forceReturn">
					<input type="hidden" name="isbn" value="<%=book.getIsbn()%>">
					<input type="hidden" name="view" value="admin">
					<button type="submit" style="background-color: #ffcccc; color: #900;">強制返却</button>
				</form>
				<%
				}
				%>

				<!-- 編集用ドロップダウン -->
				<details style="display:inline-block;">
					<summary style="cursor:pointer; color:blue; display:inline;">編集</summary>
					<form action="books" method="post" style="background:#f9f9f9; padding:5px; margin-top:5px; border:1px solid #ddd;">
						<input type="hidden" name="action" value="update">
						<input type="hidden" name="isbn" value="<%=book.getIsbn()%>">
						<input type="hidden" name="view" value="admin">
						<div>タイトル: <input type="text" name="title" value="<%=book.getTitle()%>" required></div>
						<div>カテゴリ: <input type="text" name="category" value="<%=book.getCategory()%>" required></div>
						<div>著者: <input type="text" name="writer" value="<%=book.getWriter()%>" required></div>
						<div>出版社: <input type="text" name="publisher" value="<%=book.getPublisher()%>" required></div>
						<div>URL: <input type="text" name="url" value="<%=book.getUrl() != null ? book.getUrl() : ""%>"></div>
						<button type="submit" style="margin-top:4px;">更新する</button>
					</form>
				</details>

				<!-- 削除ボタン -->
				<form action="books" method="post" style="display: inline; margin-left: 5px;" onsubmit="return confirm('本当にこの書籍を削除しますか？');">
					<input type="hidden" name="action" value="delete">
					<input type="hidden" name="isbn" value="<%=book.getIsbn()%>">
					<input type="hidden" name="view" value="admin">
					<button type="submit" style="color: red;">削除</button>
				</form>
			</td>
		</tr>
		<%
			}
		}
		%>
	</table>
</body>
</html>