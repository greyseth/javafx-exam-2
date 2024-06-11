package org.example.intellijfx.intellijfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import org.example.intellijfx.intellijfx.models.CustomerOrder;
import org.example.intellijfx.intellijfx.models.JDBC;
import org.example.intellijfx.intellijfx.models.SupplyOrder;
import org.example.intellijfx.intellijfx.models.Tools;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class InventoryList {
    @FXML
    private FlowPane header;

    @FXML
    private TableColumn<SupplyOrder, Integer> inPriceColumn;

    @FXML
    private TableColumn<org.example.intellijfx.intellijfx.SupplyOrder, Integer> inProductColumn;

    @FXML
    private TableColumn<org.example.intellijfx.intellijfx.SupplyOrder, Integer> inQtyColumn;

    @FXML
    private TableColumn<org.example.intellijfx.intellijfx.SupplyOrder, String> inSupplierColumn;

    @FXML
    private TableColumn<SupplyOrder, Integer> inOrderIdColumn;

    @FXML
    private TableColumn<SupplyOrder, String> inDateColumn;

    @FXML
    private TableColumn<CustomerOrder, String> outCustomerColumn;

    @FXML
    private TableColumn<CustomerOrder, Integer> outPriceColumn;

    @FXML
    private TableColumn<CustomerOrder, Integer> outProductColumn;

    @FXML
    private TableColumn<CustomerOrder, Integer> outQtyColumn;

    @FXML
    private TableColumn<CustomerOrder, Integer> outOrderIdColumn;

    @FXML
    private TableColumn<CustomerOrder, String> outDateColumn;

    @FXML
    private TableView<SupplyOrder> qtyInTable;

    @FXML
    private TableView<CustomerOrder> qtyOutTable;

    @FXML
    private Button returnButton;

    LinkedList<CustomerOrder > customerOrders = new LinkedList<>();
    LinkedList<SupplyOrder> supplyOrders = new LinkedList<>();

    @FXML
    void initialize() {
        if (JDBC.getConnection().isPresent()) {
            try {
                //Statement for customer orders
                Statement statement = JDBC.getConnection().get().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = statement.executeQuery("SELECT customer_order.*, users.name FROM customer_order LEFT JOIN users ON users.user_id = customer_order.customer_id GROUP BY customer_order.order_id;");
                boolean hasCustomerContent = false;
                if (rs.last()) {
                    if (rs.getRow() > 0) hasCustomerContent = true;
                }

                if (hasCustomerContent) {
                    rs.beforeFirst();

                    while(rs.next()) {
                        org.example.intellijfx.intellijfx.models.CustomerOrder co = new org.example.intellijfx.intellijfx.models.CustomerOrder(rs.getInt("order_id"), rs.getDate("order_date").toString(), rs.getInt("customer_id"), rs.getString("name"), rs.getString("status"), rs.getInt("total_amount"), rs.getInt("product_id"), rs.getInt("quantity"));
                        customerOrders.add(co);
                    }
                }

                //Statement for supplier orders
                rs = statement.executeQuery("SELECT supplier_order.*, users.name FROM supplier_order LEFT JOIN users ON users.user_id = supplier_order.supplier_id GROUP BY supplier_order.order_id;");
                boolean hasSupplierContent = false;
                if (rs.last()) {
                    if (rs.getRow() > 0) hasSupplierContent = true;
                }

                if (hasSupplierContent) {
                    rs.beforeFirst();

                    while(rs.next()) {
                        org.example.intellijfx.intellijfx.models.SupplyOrder so = new org.example.intellijfx.intellijfx.models.SupplyOrder(rs.getInt("order_id"), rs.getDate("order_date").toString(), rs.getInt("supplier_id"), rs.getString("name"), rs.getString("status"),rs.getInt("product_id"), rs.getInt("quantity"), rs.getInt("price"));
                        supplyOrders.add(so);
                    }
                }

                //Sets up table columns
                inProductColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
                inSupplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
                inPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
                inQtyColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
                inOrderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
                inDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

                outProductColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
                outCustomerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
                outPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
                outQtyColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
                outOrderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
                outDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

                //Initializes table content
                ObservableList<CustomerOrder> coList = FXCollections.observableArrayList();
                ObservableList<SupplyOrder> soList = FXCollections.observableArrayList();

                coList.addAll(customerOrders);
                soList.addAll(supplyOrders);

                qtyOutTable.setItems(coList);
                qtyInTable.setItems(soList);
            }catch(SQLException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Kesalahan saat mengambil data");
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
    void goBack(ActionEvent event) throws IOException {
        Tools.changeScene(event, "dashboard.fxml");
    }
}
