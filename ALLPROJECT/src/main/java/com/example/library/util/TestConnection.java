package com.example.library.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("MariaDB 接続テストを開始します...");

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Book")) {

            System.out.println("✅ データベースへの接続に成功しました！");
            System.out.println("--- 登録されている書籍データ ---");

            while (rs.next()) {
                String isbn = rs.getString("ISBN");
                String title = rs.getString("Title");
                String status = rs.getString("Rent_Status");
                System.out.println("ISBN: " + isbn + " | タイトル: " + title + " | ステータス: " + status);
            }

        } catch (Exception e) {
            System.err.println("❌ 接続に失敗しました。URL、ユーザー名、パスワード、またはJARファイルを確認してください。");
            e.printStackTrace();
        }
    }
}