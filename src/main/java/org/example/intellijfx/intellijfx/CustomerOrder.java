package org.example.intellijfx.intellijfx;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.intellijfx.intellijfx.data.LoginManager;
import org.example.intellijfx.intellijfx.models.JDBC;
import org.example.intellijfx.intellijfx.models.Tools;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class CustomerOrder {

    @FXML
    private Button clearFormButton;

    @FXML
    private TableColumn<org.example.intellijfx.intellijfx.models.CustomerOrder, String> customerColumn;

    @FXML
    private TableView<org.example.intellijfx.intellijfx.models.CustomerOrder> customerOrderTable;

    @FXML
    private Label customerLabel;

    @FXML
    private HBox customerContainer;

    @FXML
    private FlowPane header;

    @FXML
    private TableColumn<org.example.intellijfx.intellijfx.models.CustomerOrder, Integer> orderIdColumn;

    @FXML
    private TableColumn<org.example.intellijfx.intellijfx.models.CustomerOrder, Integer> priceColumn;

    @FXML
    private TextField priceInput;

    @FXML
    private TableColumn<org.example.intellijfx.intellijfx.models.CustomerOrder, Integer> productIdColumn;

    @FXML
    private VBox productSelectContainer;

    @FXML
    private TextField productSelectDisplay;

    @FXML
    private FlowPane productSelectPane;

    @FXML
    private TableColumn<org.example.intellijfx.intellijfx.models.CustomerOrder, Integer> quantityColumn;

    @FXML
    private TextField quantityInput;

    @FXML
    private Button returnButton;

    @FXML
    private Button updateButton;

    @FXML
    private VBox userSelectContainer;

    @FXML
    private TextField userSelectDisplay;

    @FXML
    private FlowPane userSelectPane;

    @FXML
            private GridPane formContainer;

    int selectedUserId = -1;
    int selectedItemId = -1;
    int selectedItemPrice = 0;
    LinkedList<org.example.intellijfx.intellijfx.models.CustomerOrder> customerOrders = new LinkedList<>();

    @FXML
    void initialize() {
        if (LoginManager.loggedIn.get().getRole().equals("Customer")) {
            selectedUserId = LoginManager.loggedIn.get().getUserId();

            customerLabel.setVisible(false);
            customerContainer.setVisible(false);

            formContainer.getChildren().removeIf(node -> GridPane.getRowIndex(node) == null || GridPane.getRowIndex(node) == 0);
        }

        if (JDBC.getConnection().isPresent()) {
            try {
                Statement statement = JDBC.getConnection().get().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                //Creates users selection
                ResultSet rs = statement.executeQuery("SELECT user_id, name FROM users WHERE role = 'Customer';");
                while(rs.next()) {
                    int userId = rs.getInt(1);
                    String name = rs.getString(2);

                    Button selectionButton = new Button(name);
                    selectionButton.setOnAction(e -> {
                        selectedUserId = userId;
                        userSelectDisplay.setText(name);
                        userSelectPane.setVisible(false);
                    });
                    userSelectContainer.getChildren().add(selectionButton);
                }

                //Creates product selection
                rs = statement.executeQuery("SELECT product_id, name, price FROM product");
                while(rs.next()) {
                    int productId = rs.getInt(1);
                    String name = rs.getString(2);
                    int price = rs.getInt(3);

                    Button selectionButton = new Button(name);
                    selectionButton.setOnAction(
                            e -> {
                                selectedItemId = productId;
                                selectedItemPrice = price;

                                productSelectDisplay.setText(name);
                                productSelectPane.setVisible(false);
                                quantityChange();
                            }
                    );
                    productSelectContainer.getChildren().add(selectionButton);
                }

                //Loads table data
                rs = statement.executeQuery("SELECT customer_order.*, users.name FROM customer_order LEFT JOIN users ON users.user_id = customer_order.customer_id GROUP BY customer_order.order_id;");
                boolean hasContent = false;
                if (rs.last()) {
                    if (rs.getRow() > 0) hasContent = true;
                }

                if (hasContent) {
                    rs.beforeFirst();

                    while(rs.next()) {
                        org.example.intellijfx.intellijfx.models.CustomerOrder co = new org.example.intellijfx.intellijfx.models.CustomerOrder(rs.getInt("order_id"), rs.getDate("order_date").toString(), rs.getInt("customer_id"), rs.getString("name"), rs.getString("status"), rs.getInt("total_amount"), rs.getInt("product_id"), rs.getInt("quantity"));
                        customerOrders.add(co);
                    }

                    ObservableList<org.example.intellijfx.intellijfx.models.CustomerOrder> observableList = FXCollections.observableArrayList();
                    orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
                    productIdColumn.setCellValueFactory(new PropertyValueFactory<>("product_id"));
                    customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
                    quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
                    priceColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

                    observableList.addAll(customerOrders);
                    customerOrderTable.setItems(observableList);
                }
            }catch(SQLException e) {e.printStackTrace();}
        }
    }

    @FXML
    void clearForm(ActionEvent event) {
        selectedUserId = -1;
        selectedItemId = -1;
        productSelectDisplay.setText("");
        userSelectDisplay.setText("");
        quantityInput.setText("");
    }

    @FXML
    void enterListAction(ActionEvent event) {
        if (selectedItemId == -1 || selectedUserId == -1 || quantityInput.getText().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Semua field dalam form harus terisi!");
            a.showAndWait();

            return;
        }

        if (JDBC.getConnection().isPresent()) {
            try {
                String query = "INSERT INTO customer_order(order_date, customer_id, status, total_amount, product_id, quantity) VALUES(CURRENT_DATE(), ?, 'pending', ?, ?, ?);";
                PreparedStatement statement = JDBC.getConnection().get().prepareStatement(query);
                statement.setInt(1, selectedUserId);
                statement.setInt(2, Integer.parseInt(quantityInput.getText())*selectedItemPrice);
                statement.setInt(3, selectedItemId);
                statement.setInt(4, Integer.parseInt(quantityInput.getText()));

                int affectedRows = statement.executeUpdate();
                if (affectedRows > 0) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setContentText("Berhasil menambahkan pesanan");
                    a.showAndWait();

                    updateAction();
                }else {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Kesalahan saat memasukkan data");
                    a.showAndWait();
                }
            }catch(SQLException | NumberFormatException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Kesalahan saat memasukkan data");
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

    @FXML
    void productSelectAction(ActionEvent event) {
        productSelectPane.setVisible(true);
    }

    @FXML
    void productSelectCancel(ActionEvent event) {
        productSelectPane.setVisible(false);
    }

    @FXML
    void quantityChange() {
        try {
            priceInput.setText(String.valueOf(selectedItemPrice * Integer.parseInt(quantityInput.getText())));
        }catch(Exception e) {
            priceInput.setText("Invalid Quantity Input");
        }
    }

    @FXML
    void updateAction() {
        customerOrderTable.getItems().clear();

        if (JDBC.getConnection().isPresent()) {
            try {
                Statement statement = JDBC.getConnection().get().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = statement.executeQuery("SELECT customer_order.*, users.name FROM customer_order LEFT JOIN users ON users.user_id = customer_order.customer_id GROUP BY customer_order.order_id;");

                boolean hasContent = false;
                if (rs.last()) {
                    if (rs.getRow() > 0) hasContent = true;
                }

                if (hasContent) {
                    rs.beforeFirst();
                    customerOrders.clear();

                    while(rs.next()) {
                        org.example.intellijfx.intellijfx.models.CustomerOrder co = new org.example.intellijfx.intellijfx.models.CustomerOrder(rs.getInt("order_id"), rs.getDate("order_date").toString(), rs.getInt("customer_id"), rs.getString("name"), rs.getString("status"), rs.getInt("total_amount"), rs.getInt("product_id"), rs.getInt("quantity"));
                        customerOrders.add(co);
                    }

                    ObservableList<org.example.intellijfx.intellijfx.models.CustomerOrder> observableList = FXCollections.observableArrayList();
                    observableList.addAll(customerOrders);

                    customerOrderTable.setItems(observableList);
                }
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
    void userSelectAction(ActionEvent event) {
        userSelectPane.setVisible(true);
    }

    @FXML
    void userSelectCancel(ActionEvent event) {
        userSelectPane.setVisible(false);
    }
}
