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
import com.example.library.dao.ReadingLogDAO;
import com.example.library.model.Account;
import com.example.library.model.ReadingLog;
import com.example.library.util.DBUtil;

@WebServlet("/rent")
public class RentServlet extends HttpServlet {

	// 貸出ログ表示
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		Account user = (session != null) ? (Account) session.getAttribute("loginUser") : null;

		// Guest制限チェック
		if (user == null) {
			request.setAttribute("error", "貸出ログの確認にはログインが必要です。");
			request.getRequestDispatcher("/login.jsp").forward(request, response);
			return;
		}

		ReadingLogDAO logDAO = new ReadingLogDAO();
		List<ReadingLog> logs;

		// 管理者の場合は全ユーザーのログを表示、一般ユーザーは自分のログのみを表示
		if ("Admin".equals(user.getRole())) {
			logs = logDAO.getAllLogs();
			request.setAttribute("isAdminView", true);
		} else {
			logs = logDAO.getLogsByUserId(user.getUserId());
			request.setAttribute("isAdminView", false);
		}

		request.setAttribute("logs", logs);
		request.getRequestDispatcher("/WEB-INF/views/reading_log.jsp").forward(request, response);
	}

	// 貸出・返却実行
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Account user = (session != null) ? (Account) session.getAttribute("loginUser") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String isbn = request.getParameter("isbn");
        ReadingLogDAO logDAO = new ReadingLogDAO();

        if ("rent".equals(action)) {
            boolean success = logDAO.rentBook(user.getUserId(), isbn);
            if (!success) {
                session.setAttribute("rentError", "この本は既に貸出されているため貸出できません。");
            }
        } else if ("return".equals(action)) {
            logDAO.returnBook(user.getUserId(), isbn);
        } else if ("forceReturn".equals(action)) {
            // 管理者の場合のみ、BookDAOを使ってステータスを強制的にAvailableに戻す
            if ("Admin".equals(user.getRole())) {
                BookDAO bookDAO = new BookDAO();
                try (Connection conn = DBUtil.getConnection()) {
                    bookDAO.updateRentStatus(isbn, "Available", conn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 管理者画面からの操作であれば管理者画面へリダイレクト
        String view = request.getParameter("view");
        if ("admin".equals(view) && user != null && "Admin".equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + "/books?view=admin");
        } else {
            response.sendRedirect(request.getContextPath() + "/books");
        }
    }
}