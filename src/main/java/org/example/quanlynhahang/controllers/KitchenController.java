package org.example.quanlynhahang.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.quanlynhahang.database.DatabaseConnection;
import org.example.quanlynhahang.models.MonAn;
import org.example.quanlynhahang.utils.MenuStatusManager;

import java.sql.*;

public class KitchenController {

    @FXML private GridPane paneOrderView;
    @FXML private VBox paneMenuView;
    @FXML private TableView<MonAn> tableMenu;
    @FXML private TableColumn<MonAn, String> colFoodName;
    @FXML private TableColumn<MonAn, String> colCategory;
    @FXML private TableColumn<MonAn, String> colStatus;
    @FXML private TableColumn<MonAn, Void> colAction;

    @FXML private VBox vboxPending;
    @FXML private VBox vboxProcessing;
    @FXML private VBox vboxDone;

    // --- KHÔNG ĐỘNG VÀO CODE CŨ - CHỈ THÊM 3 LABEL MỚI ---
    @FXML private Label lblCountPending;
    @FXML private Label lblCountProcessing;
    @FXML private Label lblCountDone;

    private ObservableList<MonAn> dishList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colFoodName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTenMon()));
        colCategory.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDanhMuc()));

        setupStatusColumn();
        setupActionColumn();

        loadDishesFromDatabase();
        loadOrdersFromDatabase();
        updateStatistics(); // Gọi lần đầu khi mở app

        // --- ĐOẠN CODE FIX LỖI CỦA BÀ - TỚ CHỈ THÊM updateStatistics() VÀO TRONG ---
        Timeline autoUpdate = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            if (paneOrderView != null && paneOrderView.isVisible()) {
                loadOrdersFromDatabase();
                updateStatistics(); // Cập nhật số lượng mỗi 5 giây cùng lúc với load đơn
            }
        }));
        autoUpdate.setCycleCount(Timeline.INDEFINITE);
        autoUpdate.play();
    }

    // --- HÀM MỚI: CHỈ ĐỂ CHẠY THỐNG KÊ (FIX LỖI SQL EXCEPTION) ---
    private void updateStatistics() {
        String sql = "SELECT " +
                "SUM(CASE WHEN TRANG_THAI_MON = 'MOI_DAT' THEN 1 ELSE 0 END) as pending, " +
                "SUM(CASE WHEN TRANG_THAI_MON = 'DANG_CHE_BIEN' THEN 1 ELSE 0 END) as processing, " +
                "SUM(CASE WHEN TRANG_THAI_MON = 'DA_XONG' AND DATE(THOI_GIAN_DAT) = CURDATE() THEN 1 ELSE 0 END) as done " +
                "FROM ORDER_DETAIL";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                // Lấy dữ liệu ra biến tạm trước để tránh lỗi Thread của JavaFX
                int p = rs.getInt("pending");
                int proc = rs.getInt("processing");
                int d = rs.getInt("done");

                Platform.runLater(() -> {
                    if (lblCountPending != null) lblCountPending.setText(String.valueOf(p));
                    if (lblCountProcessing != null) lblCountProcessing.setText(String.valueOf(proc));
                    if (lblCountDone != null) lblCountDone.setText(String.valueOf(d));
                });
            }
        } catch (SQLException e) {
            System.err.println("Lỗi Dashboard: " + e.getMessage());
        }
    }

    private void loadOrdersFromDatabase() {
        Platform.runLater(() -> {
            if (vboxPending == null || vboxProcessing == null || vboxDone == null) return;

            vboxPending.getChildren().clear();
            vboxProcessing.getChildren().clear();
            vboxDone.getChildren().clear();

            String sql = "SELECT b.TEN_BAN, m.TEN_MON, od.SO_LUONG, od.GHI_CHU, od.TRANG_THAI_MON, od.MA_MON, od.MA_ORDER " +
                    "FROM ORDER_DETAIL od " +
                    "JOIN MONAN m ON od.MA_MON = m.MA_MON " +
                    "JOIN ORDERS o ON od.MA_ORDER = o.MA_ORDER " +
                    "JOIN HOADON h ON o.MA_HOA_DON = h.MA_HOA_DON " +
                    "JOIN BANAN b ON h.MA_BAN = b.MA_BAN";

            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    String status = rs.getString("TRANG_THAI_MON");
                    String tenBan = rs.getString("TEN_BAN");
                    String tenMon = rs.getString("TEN_MON");
                    int soLuong = rs.getInt("SO_LUONG");
                    String ghiChu = rs.getString("GHI_CHU");
                    String maMon = rs.getString("MA_MON");
                    String maOrder = rs.getString("MA_ORDER");

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/quanlynhahang/KitchenItem.fxml"));
                        VBox card = loader.load();

                        Label lblTableNum = (Label) card.lookup("#lblTableNum");
                        Label lblFoodName = (Label) card.lookup("#lblFoodName");
                        Label lblQuantity = (Label) card.lookup("#lblQuantity");
                        Label lblNote = (Label) card.lookup("#lblNote");
                        Button btnAction = (Button) card.lookup("#btnDone");

                        if (lblTableNum != null) lblTableNum.setText(tenBan);
                        if (lblFoodName != null) lblFoodName.setText(tenMon);
                        if (lblQuantity != null) lblQuantity.setText("x" + soLuong);
                        if (lblNote != null) lblNote.setText("Ghi chú: " + (ghiChu != null ? ghiChu : ""));

                        if ("MOI_DAT".equals(status)) {
                            btnAction.setText("BẮT ĐẦU CHẾ BIẾN");
                            btnAction.setStyle("-fx-background-color: #C5A059; -fx-text-fill: black; -fx-font-weight: bold;");
                            btnAction.setOnAction(e -> updateOrderStatus(maOrder, maMon, "DANG_CHE_BIEN"));
                            vboxPending.getChildren().add(card);

                        } else if ("DANG_CHE_BIEN".equals(status)) {
                            btnAction.setText("XÁC NHẬN HOÀN THÀNH");
                            btnAction.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
                            btnAction.setOnAction(e -> updateOrderStatus(maOrder, maMon, "DA_XONG"));
                            vboxProcessing.getChildren().add(card);

                        } else if ("DA_XONG".equals(status) || "DA_GIAO".equals(status)) {
                            btnAction.setVisible(false);
                            card.setOpacity(0.7);
                            vboxDone.getChildren().add(card);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                System.err.println("Lỗi SQL Kitchen: " + e.getMessage());
            }
        });
    }

    private void updateOrderStatus(String maOrder, String maMon, String newStatus) {
        String sql = "UPDATE ORDER_DETAIL SET TRANG_THAI_MON = ? WHERE MA_ORDER = ? AND MA_MON = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setString(2, maOrder);
            pstmt.setString(3, maMon);

            if (pstmt.executeUpdate() > 0) {
                loadOrdersFromDatabase();
                updateStatistics(); // Cập nhật lại số liệu ngay khi bấm nút
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDishesFromDatabase() {
        dishList.clear();
        String sql = "SELECT * FROM MONAN";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dishList.add(new MonAn(rs.getString("MA_MON"), rs.getString("TEN_MON"),
                        rs.getDouble("GIA_TIEN"), rs.getString("MO_TA"),
                        rs.getString("HINH_ANH"), rs.getString("DANH_MUC"), true));
            }
            tableMenu.setItems(dishList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupStatusColumn() {
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setText(null); setStyle("");
                } else {
                    MonAn mon = getTableView().getItems().get(getIndex());
                    if (MenuStatusManager.loadLockedItems().contains(mon.getMaMon())) {
                        setText("HẾT MÓN");
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    } else {
                        setText("CÒN PHỤC VỤ");
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-alignment: CENTER;");
                    }
                }
            }
        });
    }

    private void setupActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnToggle = new Button();
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    MonAn mon = getTableView().getItems().get(getIndex());
                    boolean isLocked = MenuStatusManager.loadLockedItems().contains(mon.getMaMon());

                    btnToggle.setText(isLocked ? "BẬT LẠI" : "HẾT MÓN");
                    btnToggle.setStyle(isLocked
                            ? "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;"
                            : "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");

                    btnToggle.setOnAction(e -> {
                        if (isLocked) MenuStatusManager.removeItem(mon.getMaMon());
                        else MenuStatusManager.addItem(mon.getMaMon());
                        tableMenu.refresh();
                    });
                    setGraphic(btnToggle);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });
    }

    @FXML
    private void showOrderView(ActionEvent event) {
        paneOrderView.setVisible(true);
        paneMenuView.setVisible(false);
        loadOrdersFromDatabase();
        updateStatistics(); // Cập nhật lại số liệu khi chuyển tab
    }

    @FXML
    private void showMenuView(ActionEvent event) {
        paneOrderView.setVisible(false);
        paneMenuView.setVisible(true);
        loadDishesFromDatabase();
    }

    @FXML
    private void handleAddNewDish(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/quanlynhahang/AddDishDialog.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Thêm món ăn mới");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(((Node)event.getSource()).getScene().getWindow());

            stage.setScene(new Scene(root));
            stage.showAndWait();

            loadDishesFromDatabase();

        } catch (Exception e) {
            System.err.println("Lỗi mở cửa sổ thêm món: " + e.getMessage());
            e.printStackTrace();
        }
    }
}