package org.example.quanlynhahang.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class KitchenDashboardController {

    @FXML private Button btnUpdateMenu;
    @FXML private Button btnViewOrders;

    @FXML
    void handleUpdateMenu(ActionEvent event) {
        switchScene(event, "/org/example/quanlynhahang/UpdateMenuView.fxml", "SÉLECTION DU MENU");
    }

    @FXML
    void handleViewOrders(ActionEvent event) {
        switchScene(event, "/org/example/quanlynhahang/KitchenView.fxml", "L'ÉLITE DE CUISINE");
    }

    @FXML
    void handleLogout(ActionEvent event) {
        switchScene(event, "/org/example/quanlynhahang/WelcomeView.fxml", "BIENVENUE");
    }

    private void switchScene(ActionEvent event, String fxmlPath, String title) {
        try {
            if (getClass().getResource(fxmlPath) == null) {
                System.err.println("LỖI: Không tìm thấy file FXML tại: " + fxmlPath);
                return;
            }

            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Tạo Scene mới với nội dung đã load
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);

            // Cấu hình kích thước "Vàng" để nhỏ gọn và không tràn màn hình
            if (fxmlPath.contains("KitchenView")) {
                stage.setWidth(950);   // Tăng nhẹ lên 980 để các cột thoải mái hơn chút
                stage.setHeight(600);  // Giữ 650 để né thanh Taskbar của Windows
                stage.setResizable(false); // Khóa khung để giữ form cổ điển
            } else {
                // Các màn hình khác có thể để nhỏ hơn cho thanh thoát
                stage.setWidth(850);
                stage.setHeight(550);
                stage.setResizable(true);
            }

            // Luôn căn giữa sau khi đổi kích thước để app không bị lệch góc
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("Lỗi khi tải giao diện: " + e.getMessage());
            e.printStackTrace();
        }
    }
}