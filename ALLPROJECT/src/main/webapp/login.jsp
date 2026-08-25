<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<!DOCTYPE html>
<html>
<head>
<title>ログイン / サインイン</title>
</head>
<body>
	<jsp:include page="/WEB-INF/header.jsp" />

	<h3 style="color: red;"><%=request.getAttribute("error") != null ? request.getAttribute("error") : ""%></h3>
	<h3 style="color: green;"><%=request.getAttribute("message") != null ? request.getAttribute("message") : ""%></h3>

	<h2>1. ログイン</h2>
	<form action="auth" method="post">
		<input type="hidden" name="action" value="login"> User_ID: <input
			type="number" name="userId" required><br> Password: <input
			type="password" name="password" required><br>
		<button type="submit">ログイン</button>
	</form>

	<hr>

	<h2>2. サインイン (新規登録)</h2>

	<form action="${pageContext.request.contextPath}/auth" method="post">
		<input type="hidden" name="action" value="signin"> Password
		(半角10文字以内): <input type="password" name="password" maxlength="10"
			required><br>
		<button type="submit">サインイン</button>
	</form>
</body>
</html>