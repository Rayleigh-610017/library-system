package com.example.library.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    // 接続先URL（Port番号が標準の3306の場合）
    private static final String URL = "jdbc:mariadb://localhost:3306/library_db?useUnicode=true&characterEncoding=utf8";
    
    // MariaDBにログインするユーザー名（デフォルトは root）
    private static final String USER = "root"; 
    
    // MariaDBのパスワード（ご自身のMariaDBのRootパスワードを設定してください）
    private static final String PASSWORD = "password"; 

    static {
        try {
            // ドライバクラスのロード
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MariaDB JDBC Driver が見つかりません。WEB-INF/lib に JAR ファイルを配置してください。");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}