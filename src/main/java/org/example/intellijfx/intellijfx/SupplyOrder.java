package org.example.intellijfx.intellijfx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.intellijfx.intellijfx.data.CustomDialog;
import org.example.intellijfx.intellijfx.data.LoginManager;
import org.example.intellijfx.intellijfx.models.JDBC;
import org.example.intellijfx.intellijfx.models.Tools;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class SupplyOrder {
    @FXML
    private Button clearFormButton;

    @FXML
    private Button enterList;

    @FXML
    private FlowPane header;

    @FXML
    private TextField priceInput;

    @FXML
    private TextField productSelectDisplay;

    @FXML
    private TextField quantityInput;

    @FXML
    private Button returnButton;

    @FXML
    private TableView<org.example.intellijfx.intellijfx.models.SupplyOrder> supplyOrderTable;
    @FXML
    private TableColumn<org.example.intellijfx.intellijfx.models.SupplyOrder, Integer> supplyIdColumn;
    @FXML
    private TableColumn<org.example.intellijfx.intellijfx.models.SupplyOrder, Integer> productIdColumn;
    @FXML
    private TableColumn<org.example.intellijfx.intellijfx.models.SupplyOrder, String> supplierNameColumn;
    @FXML
    private TableColumn<org.example.intellijfx.intellijfx.models.SupplyOrder, Integer> quantityColumn;

    @FXML
    private Button updateButton;

    @FXML
    private FlowPane productSelectPane;

    @FXML
    private VBox productSelectContainer;

    int selectedItemId = -1;
    LinkedList<org.example.intellijfx.intellijfx.models.SupplyOrder> supplyOrders = new LinkedList<>();

    @FXML
    void initialize() {
        if (JDBC.getConnection().isPresent()) {
            try {
                Statement statement = JDBC.getConnection().get().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = statement.executeQuery("SELECT product_id, name FROM product;");
                while (rs.next()) {
                    int productId = rs.getInt(1);
                    String name = rs.getString(2);

                    Button selectionButton = new Button(name);
                    selectionButton.setOnAction(
                            e -> {
                                selectedItemId = productId;
                                productSelectDisplay.setText(name);
                                productSelectPane.setVisible(false);
                            }
                    );
                    productSelectContainer.getChildren().add(selectionButton);
                }

                rs = statement.executeQuery("SELECT supplier_order.*, users.name FROM supplier_order LEFT JOIN users ON users.user_id = supplier_order.supplier_id GROUP BY supplier_order.order_id;");
                boolean hasContent = false;
                if (rs.last()) {
                    if (rs.getRow() > 0) hasContent = true;
                }

                if (hasContent) {
                    supplyOrders.clear();

                    rs.beforeFirst();
                    while(rs.next()) {
                        org.example.intellijfx.intellijfx.models.SupplyOrder so = new org.example.intellijfx.intellijfx.models.SupplyOrder(rs.getInt("order_id"), rs.getDate("order_date").toString(), rs.getInt("supplier_id"), rs.getString("name"), rs.getString("status"),rs.getInt("product_id"), rs.getInt("quantity"), rs.getInt("price"));
                        supplyOrders.add(so);
                    }

                    ObservableList<org.example.intellijfx.intellijfx.models.SupplyOrder> observableList = FXCollections.observableArrayList();
                    supplyIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
                    productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
                    supplierNameColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
                    quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

                    observableList.addAll(supplyOrders);
                    supplyOrderTable.setItems(observableList);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void goBack(ActionEvent event) throws IOException {
        Tools.changeScene(event, "dashboard.fxml");
    }

    @FXML
    void clearForm() {
        selectedItemId = -1;
        productSelectDisplay.setText("");
        quantityInput.setText("");
        priceInput.setText("");
    }

    @FXML
    void productSelectAction(ActionEvent event) {
        productSelectPane.setVisible(true);
    }

    @FXML
    void productSelectCancel() {
        productSelectPane.setVisible(false);
    }

    @FXML
    void enterListAction() {
        if (selectedItemId == -1 || priceInput.getText().isEmpty() || quantityInput.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Semua field form harus terisi!");
            a.showAndWait();

            return;
        }

        if (JDBC.getConnection().isPresent()) {
            try {
                String query = "INSERT INTO supplier_order (order_date, supplier_id, status, product_id, quantity, price) VALUES (CURRENT_DATE(), ?, 'pending', ?, ?, ?);";
                PreparedStatement statement = JDBC.getConnection().get().prepareStatement(query);
                statement.setInt(1, LoginManager.loggedIn.get().getUserId());
                statement.setInt(2, selectedItemId);
                statement.setInt(3, Integer.parseInt(quantityInput.getText()));
                statement.setInt(4, Integer.parseInt(priceInput.getText()));

                int affectedRows = statement.executeUpdate();
                if(affectedRows > 0) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setContentText("Data pesanan berhasil ditambahkan");
                    a.showAndWait();

                    clearForm();
                    updateAction();
                }
            }catch (SQLException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Kesalahan saat menambahkan data");
                a.showAndWait();

                e.printStackTrace();
            }
        }else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Tidak bisa menghubungi database");
            a.showAndWait();
        }
    }

    @FXML
    void updateAction() {
        supplyOrderTable.getItems().clear();

        if (JDBC.getConnection().isPresent()) {
            try {
                Statement statement = JDBC.getConnection().get().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = statement.executeQuery("SELECT supplier_order.*, users.name FROM supplier_order LEFT JOIN users ON users.user_id = supplier_order.supplier_id GROUP BY supplier_order.order_id;");
                boolean hasContent = false;
                if (rs.last()) {
                    if (rs.getRow() > 0) hasContent = true;
                }

                if (hasContent) {
                    supplyOrders.clear();

                    rs.beforeFirst();
                    while(rs.next()) {
                        org.example.intellijfx.intellijfx.models.SupplyOrder so = new org.example.intellijfx.intellijfx.models.SupplyOrder(rs.getInt("order_id"), rs.getDate("order_date").toString(), rs.getInt("supplier_id"), rs.getString("name"), rs.getString("status"),rs.getInt("product_id"), rs.getInt("quantity"), rs.getInt("price"));
                        supplyOrders.add(so);
                    }

                    ObservableList<org.example.intellijfx.intellijfx.models.SupplyOrder> observableList = FXCollections.observableArrayList();
                    supplyIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
                    productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
                    supplierNameColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
                    quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

                    observableList.addAll(supplyOrders);
                    supplyOrderTable.setItems(observableList);
                }
            }catch(SQLException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Kesalahan saat mengambil data tabel");
                a.showAndWait();

                e.printStackTrace();
            }
        }else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Tidak bisa menghubungi database");
            a.showAndWait();
        }
    }
}
