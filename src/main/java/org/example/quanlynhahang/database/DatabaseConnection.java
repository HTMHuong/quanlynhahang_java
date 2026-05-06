package org.example.quanlynhahang.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Nhớ kiểm tra tên database trong URL xem có đúng là 'quanlynhahang' không nhé
    private static final String URL = "jdbc:mysql://localhost:3306/quanlynhahang?useUnicode=true&characterEncoding=utf8";
    private static final String USER = "root";
    private static final String PASSWORD = "huong230806@";

    public static Connection getConnection() throws SQLException {
        try {
            // Đảm bảo Driver đã được load (tránh lỗi trên một số bản Java cũ)
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy Driver MySQL! Bà kiểm tra lại file pom.xml nhé.");
            e.printStackTrace();
            throw new SQLException(e);
        }
    }

    // Hàm bổ trợ để đóng kết nối cho an toàn
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}