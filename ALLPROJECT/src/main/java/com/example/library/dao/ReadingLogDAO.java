package com.example.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.library.model.ReadingLog;
import com.example.library.util.DBUtil;

public class ReadingLogDAO {

	// 貸出処理（トランザクション制御）
	public boolean rentBook(int userId, String isbn) {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			conn.setAutoCommit(false); // トランザクション開始

			// 1. 本の貸出状態チェック
			String checkSql = "SELECT Rent_Status FROM Book WHERE ISBN = ? FOR UPDATE";
			try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
				checkStmt.setString(1, isbn);
				ResultSet rs = checkStmt.executeQuery();
				if (rs.next()) {
					if ("Rent".equals(rs.getString("Rent_Status"))) {
						conn.rollback();
						return false; // 既に貸出中
					}
				}
			}

			// 2. Reading_Log に追加
			String logSql = "INSERT INTO Reading_Log (User_ID, ISBN, Rent_Date) VALUES (?, ?, NOW())";
			try (PreparedStatement logStmt = conn.prepareStatement(logSql)) {
				logStmt.setInt(1, userId);
				logStmt.setString(2, isbn);
				logStmt.executeUpdate();
			}

			// 3. Book のステータスを 'Rent' に更新
			BookDAO bookDAO = new BookDAO();
			bookDAO.updateRentStatus(isbn, "Rent", conn);

			conn.commit(); // 正常完了
			return true;
		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	// 返却処理
	public boolean returnBook(int userId, String isbn) {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection();
			conn.setAutoCommit(false);

			// Book のステータスを 'Available' に戻す
			BookDAO bookDAO = new BookDAO();
			bookDAO.updateRentStatus(isbn, "Available", conn);

			conn.commit();
			return true;
		} catch (SQLException e) {
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	// 個人ログ参照（一般ユーザー用）
	public List<ReadingLog> getLogsByUserId(int userId) {
		List<ReadingLog> logs = new ArrayList<>();
		String sql = "SELECT l.Log_ID, l.User_ID, l.ISBN, l.Rent_Date, b.Title " +
				"FROM Reading_Log l JOIN Book b ON l.ISBN = b.ISBN " +
				"WHERE l.User_ID = ? ORDER BY l.Rent_Date DESC";

		try (Connection conn = DBUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, userId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				ReadingLog log = new ReadingLog();
				log.setLogId(rs.getInt("Log_ID"));
				log.setUserId(rs.getInt("User_ID"));
				log.setIsbn(rs.getString("ISBN"));
				log.setRentDate(rs.getTimestamp("Rent_Date"));
				log.setBookTitle(rs.getString("Title"));
				logs.add(log);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return logs;
	}

	// 全ログ参照（管理者Admin用）
	public List<ReadingLog> getAllLogs() {
		List<ReadingLog> logs = new ArrayList<>();
		String sql = "SELECT l.Log_ID, l.User_ID, l.ISBN, l.Rent_Date, b.Title " +
				"FROM Reading_Log l JOIN Book b ON l.ISBN = b.ISBN " +
				"ORDER BY l.Rent_Date DESC";

		try (Connection conn = DBUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				ReadingLog log = new ReadingLog();
				log.setLogId(rs.getInt("Log_ID"));
				log.setUserId(rs.getInt("User_ID"));
				log.setIsbn(rs.getString("ISBN"));
				log.setRentDate(rs.getTimestamp("Rent_Date"));
				log.setBookTitle(rs.getString("Title"));
				logs.add(log);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return logs;
	}

	// 古いログの削除処理
	public int deleteoldLogs(int monthAgo) {
		String sql = "DELETE FROM Reading_Log WHERE Rent_Date < NOW() - INTERVAL ? MONTH";
		int deletedRows = 0;

		try (Connection conn = DBUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setInt(1, monthAgo);
			deletedRows = pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return deletedRows;
	}
}