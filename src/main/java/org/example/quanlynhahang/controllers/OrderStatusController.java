package org.example.quanlynhahang.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.quanlynhahang.database.DatabaseConnection;
import org.example.quanlynhahang.models.ChiTietHoaDon;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderStatusController {

    @FXML private Label lblTableNum;
    @FXML private Label lblCustomerInfo;
    @FXML private Label lblPoints;
    @FXML private Label lblSubtotal;
    @FXML private Label lblTax;
    @FXML private Label lblDiscount;
    @FXML private Label lblFinalTotal;

    @FXML private TableView<ChiTietHoaDon> tableStatus;
    @FXML private TableColumn<ChiTietHoaDon, String> colMon;
    @FXML private TableColumn<ChiTietHoaDon, Integer> colSL;
    @FXML private TableColumn<ChiTietHoaDon, String> colTrangThai;

    private ObservableList<ChiTietHoaDon> orderDetails = FXCollections.observableArrayList();
    private boolean isMember = false;
    private String customerName = "Khách vãng lai";
    private String currentMaBan = "";

    // TỚ THÊM: Bộ đếm thời gian để tự động cập nhật
    private Timeline autoUpdateTimeline;

    @FXML
    public void initialize() {
        colMon.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTenMon()));
        colSL.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getSoLuong()));
        colTrangThai.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getTrangThaiMon()));

        tableStatus.setItems(orderDetails);

        // TỚ THÊM: Cấu hình cứ 5 giây quét Database một lần
        autoUpdateTimeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> {
            if (!currentMaBan.isEmpty()) {
                refreshStatusFromDB();
            }
        }));
        autoUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
        autoUpdateTimeline.play();
    }

    public void setOrderData(ObservableList<ChiTietHoaDon> items, String name, boolean member, String tableNo) {
        this.currentMaBan = tableNo; // Lưu lại mã bàn để dùng quét DB
        this.customerName = name;
        this.isMember = member;

        if (items != null) {
            this.orderDetails.setAll(items);
        }

        if (lblTableNum != null) lblTableNum.setText(tableNo);
        if (lblCustomerInfo != null) lblCustomerInfo.setText("Xin chào: " + customerName);

        calculateBill();
        tableStatus.refresh();
    }

    // TỚ THÊM: Hàm này sẽ "hỏi" Database để lấy trạng thái mới nhất từ Bếp
    private void refreshStatusFromDB() {
        String sql = "SELECT od.MA_MON, m.TEN_MON, od.SO_LUONG, od.DON_GIA, od.GHI_CHU, od.TRANG_THAI_MON " +
                "FROM ORDER_DETAIL od " +
                "JOIN ORDERS o ON od.MA_ORDER = o.MA_ORDER " +
                "JOIN HOADON h ON o.MA_HOA_DON = h.MA_HOA_DON " +
                "JOIN MONAN m ON od.MA_MON = m.MA_MON " +
                "WHERE h.MA_BAN = ? AND h.TRANG_THAI = 'DANG_MO' " +
                "ORDER BY od.THOI_GIAN_DAT ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentMaBan);
            ResultSet rs = pstmt.executeQuery();

            ObservableList<ChiTietHoaDon> latestDetails = FXCollections.observableArrayList();
            while (rs.next()) {
                latestDetails.add(new ChiTietHoaDon(
                        "", // Mã order không quan trọng lúc hiển thị lại
                        rs.getString("MA_MON"),
                        rs.getString("TEN_MON"),
                        rs.getInt("SO_LUONG"),
                        rs.getDouble("DON_GIA"),
                        rs.getString("GHI_CHU"),
                        rs.getString("TRANG_THAI_MON"),
                        currentMaBan
                ));
            }

            // Nếu có thay đổi thì cập nhật lại bảng
            if (!latestDetails.isEmpty()) {
                orderDetails.setAll(latestDetails);
                calculateBill(); // Tính lại tiền nếu có gọi thêm món mới
                tableStatus.refresh();
            }

        } catch (SQLException e) {
            System.err.println("Lỗi tự động cập nhật: " + e.getMessage());
        }
    }

    private void calculateBill() {
        if (orderDetails.isEmpty()) return;

        double subtotal = orderDetails.stream()
                .mapToDouble(item -> item.getDonGia() * item.getSoLuong())
                .sum();

        double tax = subtotal * 0.1;
        double discount = isMember ? (subtotal * 0.05) : 0;
        double finalTotal = subtotal + tax - discount;
        int rewardPoints = (int) (finalTotal / 10000);

        lblSubtotal.setText(String.format("%,.0f VNĐ", subtotal));
        lblTax.setText(String.format("%,.0f VNĐ", tax));
        lblDiscount.setText(String.format("- %,.0f VNĐ", discount));
        lblFinalTotal.setText(String.format("%,.0f VNĐ", finalTotal));

        if (isMember) {
            lblPoints.setText("Hạng: Thành viên | Điểm nhận thêm: +" + rewardPoints);
        } else {
            lblPoints.setText("Khách vãng lai (Không tích điểm)");
        }
    }

    @FXML
    void handleAddNewDish(ActionEvent event) {
        if (autoUpdateTimeline != null) autoUpdateTimeline.stop(); // Dừng quét khi chuyển cảnh
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/quanlynhahang/MenuView.fxml"));
            Parent root = loader.load();

            MenuController menuController = loader.getController();
            menuController.restoreCart(orderDetails, customerName);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Thực đơn - Đặt thêm món");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lỗi khi quay lại Menu: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    void handlePayment(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText(null);
        alert.setContentText("Yêu cầu thanh toán cho bàn " + currentMaBan + " đã được gửi đi!");
        alert.showAndWait();
    }
}