package org.example.quanlynhahang;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Sửa đường dẫn để trỏ vào file WelcomeView.fxml cậu vừa tạo
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/org/example/quanlynhahang/WelcomeView.fxml"));

        // Cậu nên để kích thước lớn một chút (ví dụ 800x600) cho giống máy Kiosk
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        stage.setTitle("Hệ Thống Quản Lý Nhà Hàng - Chào Mừng");
        stage.setScene(scene);

        // Mẹo: Nếu muốn nó hiện to toàn màn hình ngay từ đầu thì thêm dòng này:
        // stage.setMaximized(true);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}