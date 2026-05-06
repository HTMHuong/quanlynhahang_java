module org.example.quanlynhahang {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;                // Cần cái này để dùng được Connection, ResultSet (Hết lỗi java.sql)
    requires mysql.connector.j;       // Cần cái này để kết nối với MySQL

    // Dòng này cực kỳ quan trọng để JavaFX đọc được các file Controller của cậu
    opens org.example.quanlynhahang.controllers to javafx.fxml;

    opens org.example.quanlynhahang to javafx.fxml;
    exports org.example.quanlynhahang;
}