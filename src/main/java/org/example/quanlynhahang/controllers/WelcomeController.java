package org.example.quanlynhahang.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.example.quanlynhahang.database.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WelcomeController {

    @FXML private ComboBox<String> comboLanguage;
    @FXML private Label lblTable;

    // Bà Hương có thể đổi số bàn này tùy ý hoặc làm hàm set động nhé
    private int currentTable = 5;

    @FXML
    public void initialize() {
        comboLanguage.getItems().addAll("Tiếng Việt", "English");
        comboLanguage.getSelectionModel().selectFirst();

        lblTable.setText("Bàn số: " + String.format("%02d", currentTable));
    }

    @FXML
    void handleStartOrder(ActionEvent event) {
        // 1. Cập nhật trạng thái bàn trong DB
        updateTableStatus(currentTable, "Đang có khách");

        try {
            // ✅ SỬA LỖI TẠI ĐÂY: Bỏ chữ "/views/" đi vì file của bà nằm ngay trong package gốc
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/quanlynhahang/MenuView.fxml"));
            Parent root = loader.load();

            // Lấy Stage hiện tại
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Thiết lập Scene mới
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Menu Nhà Hàng - Bàn " + currentTable);
            stage.centerOnScreen(); // Cho màn hình ra giữa cho đẹp bà ạ
            stage.show();

        } catch (IOException e) {
            System.err.println("Lỗi chuyển màn hình: Không tìm thấy file MenuView.fxml");
            e.printStackTrace();
        }
    }

    private void updateTableStatus(int tableNumber, String status) {
        String sql = "UPDATE BANAN SET TRANG_THAI = ? WHERE SO_BAN = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, tableNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật Database: " + e.getMessage());
        }
    }
}