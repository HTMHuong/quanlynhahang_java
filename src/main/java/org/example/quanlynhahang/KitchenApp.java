package org.example.quanlynhahang;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class KitchenApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // 1. Gọi đúng file KitchenView.fxml bà vừa đổi tên
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/org/example/quanlynhahang/KitchenView.fxml"));

        // 2. TẠO SCENE: Đặt kích thước mặc định đủ rộng để chứa 3 cột (1200x800)
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);

        // 3. CÀI ĐẶT CỬA SỔ (STAGE)
        stage.setTitle("HỆ THỐNG ĐIỀU PHỐI BẾP TRUNG TÂM - LA MAISON DE LUXE");

        // Gắn scene vào stage
        stage.setScene(scene);

        // Cho phép thay đổi kích thước hoặc cho to toàn màn hình để dễ điều phối
        stage.setResizable(true);

        // Đưa cửa sổ ra giữa màn hình khi mới mở
        stage.centerOnScreen();

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}