package org.example.quanlynhahang.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.quanlynhahang.database.DatabaseConnection;
import org.example.quanlynhahang.models.ChiTietHoaDon;
import org.example.quanlynhahang.models.MonAn;
import org.example.quanlynhahang.utils.MenuStatusManager;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MenuController {

    @FXML private FlowPane paneMenuList;
    @FXML private Label lblTotal;
    @FXML private Label lblTableInfo;
    @FXML private Label lblCustomerName;
    @FXML private TableView<MonAn> tableCart;

    @FXML private TableColumn<MonAn, String> colTenMon;
    @FXML private TableColumn<MonAn, Integer> colSoLuong;
    @FXML private TableColumn<MonAn, String> colGhiChu;
    @FXML private TableColumn<MonAn, Double> colGia;

    private ObservableList<MonAn> cartItems = FXCollections.observableArrayList();
    // TỚ THÊM: Danh sách để đánh dấu những món đã được lưu vào DB rồi
    private List<String> committedMonAnIds = new ArrayList<>();

    @FXML
    public void initialize() {
        colTenMon.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTenMon()));
        colSoLuong.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getSoLuong()));
        colGhiChu.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGhiChu()));
        colGia.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getGiaTien()));

        colGia.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) setText(null);
                else setText(String.format("%,.0f", price));
            }
        });

        tableCart.setItems(cartItems);

        tableCart.setOnMouseClicked(event -> {
            MonAn selected = tableCart.getSelectionModel().getSelectedItem();
            if (event.getClickCount() == 1 && selected != null) {
                showQuantityAndNotePopup(selected, true);
            }
        });

        loadMenuByCategory("Tất cả");
        updateTotal();
    }

    public void restoreCart(ObservableList<ChiTietHoaDon> oldDetails, String oldCustomerName) {
        cartItems.clear();
        committedMonAnIds.clear(); // Xóa dấu cũ

        for (ChiTietHoaDon detail : oldDetails) {
            MonAn mon = new MonAn(
                    detail.getMaMon(), detail.getTenMon(), detail.getDonGia(),
                    "", "", "", true
            );
            mon.setSoLuong(detail.getSoLuong());
            mon.setGhiChu(detail.getGhiChu());
            cartItems.add(mon);

            // Đánh dấu món này là món cũ (đã có trong DB)
            committedMonAnIds.add(detail.getMaMon());
        }

        if (oldCustomerName != null && !oldCustomerName.isEmpty() && !oldCustomerName.equals("Chưa chọn")) {
            lblCustomerName.setText("Khách hàng: " + oldCustomerName);
        }

        tableCart.refresh();
        updateTotal();
    }

    @FXML private void filterAll(ActionEvent event) { loadMenuByCategory("Tất cả"); }
    @FXML private void filterAppetizer(ActionEvent event) { loadMenuByCategory("Khai vị"); }
    @FXML private void filterMain(ActionEvent event) { loadMenuByCategory("Món chính"); }
    @FXML private void filterDrink(ActionEvent event) { loadMenuByCategory("Đồ uống"); }
    @FXML private void filterDessert(ActionEvent event) { loadMenuByCategory("Tráng miệng"); }

    private void loadMenuByCategory(String category) {
        if (paneMenuList == null) return;
        paneMenuList.getChildren().clear();
        Set<String> lockedItems = MenuStatusManager.loadLockedItems();

        String sql = category.equals("Tất cả")
                ? "SELECT * FROM MONAN WHERE IS_AVAILABLE = 1"
                : "SELECT * FROM MONAN WHERE IS_AVAILABLE = 1 AND DANH_MUC = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (!category.equals("Tất cả")) pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                MonAn mon = new MonAn(
                        rs.getString("MA_MON"), rs.getString("TEN_MON"),
                        rs.getDouble("GIA_TIEN"), rs.getString("MO_TA"),
                        rs.getString("HINH_ANH"), rs.getString("DANH_MUC"), true
                );

                VBox card = createFoodCard(mon);
                if (lockedItems.contains(mon.getMaMon())) {
                    card.setOpacity(0.4);
                    for (Node node : card.getChildren()) {
                        if (node instanceof Button) {
                            Button btn = (Button) node;
                            btn.setText("HẾT MÓN");
                            btn.setDisable(true);
                        }
                    }
                }
                paneMenuList.getChildren().add(card);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private VBox createFoodCard(MonAn mon) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #e4e1d9; -fx-border-radius: 10; -fx-background-radius: 10; -fx-alignment: center;");
        card.setPrefWidth(180);

        ImageView imgView = new ImageView();
        try {
            String path = "/org/example/quanlynhahang/images/" + mon.getHinhAnh();
            var imageStream = getClass().getResourceAsStream(path);
            if (imageStream != null) imgView.setImage(new Image(imageStream));
            else {
                var defaultStream = getClass().getResourceAsStream("/org/example/quanlynhahang/images/default.png");
                if (defaultStream != null) imgView.setImage(new Image(defaultStream));
            }
        } catch (Exception e) { }
        imgView.setFitWidth(150); imgView.setFitHeight(100); imgView.setPreserveRatio(true);

        Label lblName = new Label(mon.getTenMon());
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
        lblName.setOnMouseClicked(event -> showDescriptionOnlyPopup(mon));

        Label lblPrice = new Label(String.format("%,.0f VNĐ", mon.getGiaTien()));
        lblPrice.setTextFill(Color.web("#D95D39"));

        Button btnAdd = new Button("Chọn món");
        btnAdd.setStyle("-fx-background-color: #D95D39; -fx-text-fill: white; -fx-cursor: hand;");
        btnAdd.setOnAction(e -> showQuantityAndNotePopup(mon, false));

        card.getChildren().addAll(imgView, lblName, lblPrice, btnAdd);
        return card;
    }

    private void showDescriptionOnlyPopup(MonAn mon) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết món ăn");
        alert.setHeaderText(mon.getTenMon());
        alert.setContentText(mon.getMoTa() != null && !mon.getMoTa().isEmpty() ? mon.getMoTa() : "Món này chưa có mô tả.");
        alert.showAndWait();
    }

    private void showQuantityAndNotePopup(MonAn mon, boolean isUpdate) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(isUpdate ? "Sửa món" : "Thêm vào đơn");
        dialog.setHeaderText(mon.getTenMon());

        Spinner<Integer> spinner = new Spinner<>(1, 100, isUpdate ? mon.getSoLuong() : 1);
        TextArea txtNote = new TextArea(isUpdate ? mon.getGhiChu() : "");
        txtNote.setPrefRowCount(2);

        VBox layout = new VBox(10, new Label("Số lượng:"), spinner, new Label("Ghi chú:"), txtNote);
        layout.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(layout);

        ButtonType btnConfirm = new ButtonType("Xác nhận", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnConfirm, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == btnConfirm) {
                if (isUpdate) {
                    mon.setSoLuong(spinner.getValue());
                    mon.setGhiChu(txtNote.getText());
                } else {
                    MonAn orderItem = new MonAn(mon.getMaMon(), mon.getTenMon(), mon.getGiaTien(),
                            mon.getMoTa(), mon.getHinhAnh(), mon.getDanhMuc(), true);
                    orderItem.setSoLuong(spinner.getValue());
                    orderItem.setGhiChu(txtNote.getText());
                    cartItems.add(orderItem);
                }
                tableCart.refresh();
                updateTotal();
            }
        });
    }

    private void updateTotal() {
        double total = cartItems.stream().mapToDouble(item -> item.getGiaTien() * item.getSoLuong()).sum();
        lblTotal.setText(String.format("TỔNG: %,.0f VNĐ", total));
    }

    @FXML
    private void handleMembershipCard(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Thành viên");
        dialog.setHeaderText("Nhập mã thẻ/SĐT:");
        dialog.showAndWait().ifPresent(input -> {
            String sql = "SELECT TEN_KHACH FROM THETHANHVIEN WHERE MA_THE = ? OR SO_DIEN_THOAI = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, input); pstmt.setString(2, input);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    lblCustomerName.setText("Khách hàng: " + rs.getString("TEN_KHACH"));
                }
            } catch (SQLException e) { e.printStackTrace(); }
        });
    }

    @FXML
    void handleConfirmOrder(ActionEvent event) {
        // TỚ SỬA: Lọc ra chỉ những món mới (chưa có trong committedMonAnIds)
        List<MonAn> newItems = new ArrayList<>();
        for (MonAn item : cartItems) {
            if (!committedMonAnIds.contains(item.getMaMon())) {
                newItems.add(item);
            }
        }

        if (newItems.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Bà chưa nhặt thêm món mới nào cả!").showAndWait();
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String soBanRaw = (lblTableInfo != null) ? lblTableInfo.getText() : "Bàn: B05";
            String maBan = soBanRaw.contains(":") ? soBanRaw.split(":")[1].trim() : "B05";

            String customerName = lblCustomerName.getText().replace("Khách hàng: ", "");
            boolean isMember = !customerName.equals("Chưa chọn") && !customerName.equals("Khách hàng");

            // Kiểm tra xem bàn này đã có hóa đơn 'DANG_MO' chưa
            String maHD = null;
            String checkHDSql = "SELECT MA_HOA_DON FROM HOADON WHERE MA_BAN = ? AND TRANG_THAI = 'DANG_MO' LIMIT 1";
            try (PreparedStatement psCheck = conn.prepareStatement(checkHDSql)) {
                psCheck.setString(1, maBan);
                ResultSet rs = psCheck.executeQuery();
                if (rs.next()) {
                    maHD = rs.getString("MA_HOA_DON");
                }
            }

            // Nếu chưa có (khách mới) thì tạo HD mới
            if (maHD == null) {
                maHD = "HD" + System.currentTimeMillis();
                String sqlHD = "INSERT INTO HOADON (MA_HOA_DON, MA_BAN, THOI_GIAN_MO, TRANG_THAI) VALUES (?, ?, CURRENT_TIMESTAMP, 'DANG_MO')";
                try (PreparedStatement psHD = conn.prepareStatement(sqlHD)) {
                    psHD.setString(1, maHD);
                    psHD.setString(2, maBan);
                    psHD.executeUpdate();
                }
            }

            // Luôn tạo một mã Order mới cho đợt nhặt món này
            String maOrder = "OR" + System.currentTimeMillis();
            String sqlORD = "INSERT INTO ORDERS (MA_ORDER, MA_HOA_DON, THOI_GIAN_TAO, TRANG_THAI) VALUES (?, ?, CURRENT_TIMESTAMP, 'MOI_TAO')";
            try (PreparedStatement psORD = conn.prepareStatement(sqlORD)) {
                psORD.setString(1, maOrder);
                psORD.setString(2, maHD);
                psORD.executeUpdate();
            }

            // Insert các món MỚI vào ORDER_DETAIL
            String sqlDT = "INSERT INTO ORDER_DETAIL (MA_ORDER, MA_MON, SO_LUONG, DON_GIA, GHI_CHU, TRANG_THAI_MON, THOI_GIAN_DAT) VALUES (?, ?, ?, ?, ?, 'MOI_DAT', CURRENT_TIMESTAMP)";
            try (PreparedStatement psDT = conn.prepareStatement(sqlDT)) {
                for (MonAn mon : newItems) {
                    psDT.setString(1, maOrder);
                    psDT.setString(2, mon.getMaMon());
                    psDT.setInt(3, mon.getSoLuong());
                    psDT.setDouble(4, mon.getGiaTien());
                    psDT.setString(5, mon.getGhiChu());
                    psDT.addBatch();
                }
                psDT.executeBatch();
            }

            conn.commit();

            // QUAN TRỌNG: Xóa sạch giỏ hàng và danh sách đánh dấu để sẵn sàng cho lần tới
            cartItems.clear();
            committedMonAnIds.clear();

            // Chuyển sang màn hình trạng thái
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/quanlynhahang/OrderStatus.fxml"));
            Parent root = loader.load();
            OrderStatusController controller = loader.getController();

            // Màn hình OrderStatus sẽ tự động refresh từ DB nên nó sẽ thấy cả món cũ lẫn mới
            controller.setOrderData(null, customerName, isMember, maBan);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lỗi Database: " + e.getMessage()).showAndWait();
        }
    }
}