<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page
	import="java.util.List, com.example.library.model.Book, com.example.library.model.Account"%>
<%
List<Book> books = (List<Book>) request.getAttribute("books");
Account loginUser = (Account) session.getAttribute("loginUser");
String rentError = (String) session.getAttribute("rentError");
if (rentError != null) {
	session.removeAttribute("rentError");
}

// 検索・カテゴリパラメータの取得
String keyword = request.getParameter("keyword") != null ? request.getParameter("keyword") : "";
String selectedCategory = request.getParameter("category") != null ? request.getParameter("category") : "";

// カテゴリ一覧の取得
List<String> categories = (List<String>) request.getAttribute("categories");
%>
<!DOCTYPE html>
<html>
<head>
<title>書籍一覧</title>
</head>
<body>
	<jsp:include page="/WEB-INF/header.jsp" />

	<h2>書籍検索・一覧</h2>

	<%
	if (rentError != null) {
	%>
	<p style="color: red; font-weight: bold;"><%=rentError%></p>
	<%
	}
	%>

	<!-- 検索フォーム & 簡単検索エリア -->
	<div style="margin-bottom: 15px;">
		<form action="books" method="get" style="margin-bottom: 8px;">
			<!-- カテゴIリ選択を維持したままキーワード検索できるようにする隠しフィールド -->
			<%
			if (!selectedCategory.isEmpty()) {
			%>
			<input type="hidden" name="category" value="<%=selectedCategory%>">
			<%
			}
			%>

			<input type="text" name="keyword" value="<%=keyword%>"
				placeholder="タイトル・著者・カテゴリ・ISBN">

			<button type="submit">検索</button>

			<%
			if (!keyword.isEmpty() || !selectedCategory.isEmpty()) {
			%>
			<a href="books" style="margin-left: 10px; font-size: 0.9em;">検索を解除</a>
			<%
			}
			%>
		</form>

		<div style="margin-top: 10px;">
			【簡単検索】

			<%
		String allActive = selectedCategory.isEmpty() ? "font-weight:bold;" : "";
		%>
			<a href="books" style="margin-right: 8px; <%=allActive%>">すべて</a>

			<%
			if (categories != null) {
				for (String cat : categories) {
					String activeStyle = cat.equals(selectedCategory) ? "font-weight:bold;" : "";
					String encodedCat = java.net.URLEncoder.encode(cat, "UTF-8");

					// キーワードがある場合はキーワードも維持してカテゴリリンクを作成
					String keywordParam = !keyword.isEmpty() ? "&keyword=" + java.net.URLEncoder.encode(keyword, "UTF-8") : "";
			%>

			<a href="books?category=<%=encodedCat%><%=keywordParam%>"
				style="margin-right: 8px; <%=activeStyle%>"> <%=cat%>
			</a>

			<%
			}
			}
			%>
		</div>
	</div>

	<table border="1" cellpadding="8"
		style="margin-top: 10px; border-collapse: collapse;">
		<tr>
			<th>画像</th>
			<th>ISBN</th>
			<th>タイトル</th>
			<th>ジャンル</th>
			<th>著者</th>
			<th>出版元</th>
			<th>状態</th>
			<th>操作</th>
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
				<%
				if ("Rent".equals(book.getRentStatus())) {
				%> <span
				style="color: red; font-weight: bold;">Rent (貸出不可)</span> <%
 } else {
 %>
				<span style="color: green;">貸出可能</span> <%
 }
 %>
			</td>
			<td>
				<%
				if (loginUser != null) {
				%> <%
 if ("Available".equals(book.getRentStatus())) {
 %>
				<form action="rent" method="post" style="display: inline;">
					<input type="hidden" name="action" value="rent"> <input
						type="hidden" name="isbn" value="<%=book.getIsbn()%>">
					<button type="submit">貸出</button>
				</form> <%
 } else {
 %>
				<form action="rent" method="post" style="display: inline;">
					<input type="hidden" name="action" value="rent"> <input
						type="hidden" name="isbn" value="<%=book.getIsbn()%>">
					<button type="submit">返却</button>
				</form> <%
 }
 %> <%
 } else {
 %> <small>ログイン後操作可能</small> <%
 }
 %>
			</td>
		</tr>
		<%
		}
		}
		%>
	</table>
</body>
</html>