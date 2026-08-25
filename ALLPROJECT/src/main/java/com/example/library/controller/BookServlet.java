package com.example.library.controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.library.dao.BookDAO;
import com.example.library.model.Account;
import com.example.library.model.Book;
import com.example.library.util.DBUtil;

@WebServlet("/books")
public class BookServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession(false);
		Account user = (session != null)
				? (Account) session.getAttribute("loginUser")
				: null;

		String keyword = request.getParameter("keyword");
		String category = request.getParameter("category");

		BookDAO bookDAO = new BookDAO();

		List<Book> books = bookDAO.searchBooks(keyword, category);
		List<String> categories = bookDAO.getCategories();

		request.setAttribute("books", books);
		request.setAttribute("categories", categories);

		String view = request.getParameter("view");

		if ("admin".equals(view)
				&& user != null
				&& "Admin".equals(user.getRole())) {

			request.getRequestDispatcher(
					"/WEB-INF/views/admin_book_manage.jsp")
					.forward(request, response);

		} else {

			request.getRequestDispatcher(
					"/WEB-INF/views/book_list.jsp")
					.forward(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession(false);
		Account user = (session != null)
				? (Account) session.getAttribute("loginUser")
				: null;

		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			return;
		}

		String action = request.getParameter("action");
		BookDAO bookDAO = new BookDAO();

		// 管理者権限が必要なアクション
		if ("add".equals(action) || "update".equals(action) || "delete".equals(action) || "forceReturn".equals(action)) {
			if (user == null || !"Admin".equals(user.getRole())) {
				response.sendRedirect(request.getContextPath() + "/books");
				return;
			}
		}

		if ("add".equals(action)) {
			Book book = new Book();
			book.setIsbn(request.getParameter("isbn"));
			book.setTitle(request.getParameter("title"));
			book.setCategory(request.getParameter("category"));
			book.setWriter(request.getParameter("writer"));
			book.setPublisher(request.getParameter("publisher"));
			book.setUrl(request.getParameter("url"));
			book.setRentStatus("Available");
			bookDAO.addBook(book);

		} else if ("update".equals(action)) {
			Book book = new Book();
			book.setIsbn(request.getParameter("isbn"));
			book.setTitle(request.getParameter("title"));
			book.setCategory(request.getParameter("category"));
			book.setWriter(request.getParameter("writer"));
			book.setPublisher(request.getParameter("publisher"));
			book.setUrl(request.getParameter("url"));
			bookDAO.updateBook(book);

		} else if ("delete".equals(action)) {
			String isbn = request.getParameter("isbn");
			bookDAO.deleteBook(isbn);

		} else if ("forceReturn".equals(action)) {
			// 強制返却処理：データベース上のステータスをAvailableに更新
			String isbn = request.getParameter("isbn");
			try (Connection conn = DBUtil.getConnection()) {
				bookDAO.updateRentStatus(isbn, "Available", conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 処理後は管理者画面の状態（?view=admin）を維持してリダイレクト
		String view = request.getParameter("view");
		if ("admin".equals(view) && "Admin".equals(user.getRole())) {
			response.sendRedirect(request.getContextPath() + "/books?view=admin");
		} else {
			response.sendRedirect(request.getContextPath() + "/books");
		}
	}
}