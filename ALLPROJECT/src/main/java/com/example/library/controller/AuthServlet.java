package com.example.library.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.library.dao.AccountDAO;
import com.example.library.model.Account;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        AccountDAO accountDAO = new AccountDAO();

        if ("login".equals(action)) {
            String userIdStr = request.getParameter("userId");
            String password = request.getParameter("password");

            // 数値変換チェック (NumberFormatException対策)
            int userId = -1;
            try {
                if (userIdStr != null && !userIdStr.trim().isEmpty()) {
                    userId = Integer.parseInt(userIdStr);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "User IDは半角数字で入力してください。");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }

            Account account = accountDAO.authenticate(userId, password);
            if (account != null) {
                HttpSession session = request.getSession();
                session.setAttribute("loginUser", account);
                
                // ★修正箇所: コンテキストパスを含めた絶対パスでリダイレクト
                response.sendRedirect(request.getContextPath() + "/books");
            } else {
                request.setAttribute("error", "IDまたはパスワードが不正です。");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }

        } else if ("signin".equals(action)) {
            String password = request.getParameter("password");
            
            // バリデーション: 全角不可・最大10文字
            if (password == null || !password.matches("^[a-zA-Z0-9]{1,10}$")) {
                request.setAttribute("error", "パスワードは半角英数字10文字以内で設定してください。");
                request.getRequestDispatcher("login.jsp").forward(request, response);
                return;
            }

            Account newAccount = new Account();
            newAccount.setUserPassword(password);
            newAccount.setRole("User");

            if (accountDAO.registerUser(newAccount)) {
                request.setAttribute("message", "登録成功！あなたのUser_IDは: " + newAccount.getUserId() + " です。");
            } else {
                request.setAttribute("error", "登録に失敗しました。");
            }
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate(); // セッション破棄 (Guest状態へ還元)
            }
            
            // ★修正箇所: コンテキストパスを含めた絶対パスでリダイレクト
            response.sendRedirect(request.getContextPath() + "/books");
        }
    }
}