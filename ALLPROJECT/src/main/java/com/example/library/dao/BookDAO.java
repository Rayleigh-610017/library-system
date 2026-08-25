package com.example.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.library.model.Book;
import com.example.library.util.DBUtil;

public class BookDAO {

	// 書籍検索・一覧取得（キーワードとカテゴリ両対応）
	public List<Book> searchBooks(String keyword, String category) {
		List<Book> books = new ArrayList<>();

		StringBuilder sql = new StringBuilder("SELECT * FROM Book WHERE 1=1");
		List<Object> params = new ArrayList<>();

		if (keyword != null && !keyword.trim().isEmpty()) {
			sql.append(
					" AND (ISBN LIKE ? " +
							"OR Title LIKE ? " +
							"OR Category LIKE ? " +
							"OR Writer LIKE ?)");

			String searchPattern = "%" + keyword.trim() + "%";

			params.add(searchPattern);
			params.add(searchPattern);
			params.add(searchPattern);
			params.add(searchPattern);
		}

		if (category != null && !category.trim().isEmpty() && !"all".equals(category)) {
			sql.append(" AND Category = ?");
			params.add(category);
		}

		try (Connection conn = DBUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

			for (int i = 0; i < params.size(); i++) {
				pstmt.setObject(i + 1, params.get(i));
			}

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					books.add(new Book(
							rs.getString("ISBN"),
							rs.getString("Title"),
							rs.getString("Category"),
							rs.getString("Writer"),
							rs.getString("Publisher"),
							rs.getString("URL"),
							rs.getString("Rent_Status")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return books;
	}

	public List<Book> searchBooks(String keyword) {
		return searchBooks(keyword, null);
	}

	public List<String> getCategories() {
		List<String> categories = new ArrayList<>();
		String sql = "SELECT DISTINCT Category FROM Book ORDER BY Category";

		try (Connection conn = DBUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery()) {

			while (rs.next()) {
				categories.add(rs.getString("Category"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return categories;
	}

	public boolean updateRentStatus(String isbn, String status, Connection conn) throws SQLException {
		String sql = "UPDATE Book SET Rent_Status = ? WHERE ISBN = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, status);
			pstmt.setString(2, isbn);
			return pstmt.executeUpdate() > 0;
		}
	}

	public boolean addBook(Book book) {
		String sql = "INSERT INTO Book (ISBN, Title, Category, Writer, Publisher, URL, Rent_Status) VALUES (?, ?, ?, ?, ?, ?, 'Available')";
		try (Connection conn = DBUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, book.getIsbn());
			pstmt.setString(2, book.getTitle());
			pstmt.setString(3, book.getCategory());
			pstmt.setString(4, book.getWriter());
			pstmt.setString(5, book.getPublisher());
			pstmt.setString(6, book.getUrl());
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean deleteBook(String isbn) {
		String sql = "DELETE FROM Book WHERE ISBN = ?";
		try (Connection conn = DBUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, isbn);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// 書籍情報更新メソッド（DBUtilに統一）
	public void updateBook(Book book) {
		String sql = "UPDATE Book SET Title = ?, Category = ?, Writer = ?, Publisher = ?, URL = ? WHERE ISBN = ?";
		try (Connection conn = DBUtil.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, book.getTitle());
			pstmt.setString(2, book.getCategory());
			pstmt.setString(3, book.getWriter());
			pstmt.setString(4, book.getPublisher());
			pstmt.setString(5, book.getUrl());
			pstmt.setString(6, book.getIsbn());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}