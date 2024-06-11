package org.example.intellijfx.intellijfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.example.intellijfx.intellijfx.models.Item;
import org.example.intellijfx.intellijfx.models.JDBC;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AddItem {
    @FXML
    private Button addItemButton;

    @FXML
    private Button clearFormButton;

    @FXML
    private FlowPane header;

    @FXML
    private TextArea itemDescriptionInput;

    @FXML
    private TextField itemNameInput;

    @FXML
    private TextField itemPriceInput;

    @FXML
    private Button returnButton;

    @FXML
    private TextField stockQuantityInput;


    @FXML
    void initialize() {

    }

    @FXML
    void addItem(ActionEvent event) {
        String itemName = itemNameInput.getText();
        String itemDescription = itemDescriptionInput.getText();
        String itemPrice = itemPriceInput.getText();
        String stockQuantity = stockQuantityInput.getText();

        if (itemName.isEmpty() || itemDescription.isEmpty() || itemPrice.isEmpty() || stockQuantity.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Semua field harus terisi!");
            a.showAndWait();

            return;
        }

        if (JDBC.getConnection().isPresent()) {
            try {
                int itemPriceNum = Integer.parseInt(itemPrice);
                int stockQuantityNum = Integer.parseInt(stockQuantity);

                String query = "INSERT INTO product(name, description, price, stock_quantity) VALUES(?, ?, ?, ?);";
                PreparedStatement statement = JDBC.getConnection().get().prepareStatement(query);
                statement.setString(1, itemName);
                statement.setString(2, itemDescription);
                statement.setInt(3, itemPriceNum);
                statement.setInt(4, stockQuantityNum);

                int affectedRows = statement.executeUpdate();
                if (affectedRows > 0) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setContentText("Barang "+itemName+" telah ditambahkan");
                    a.showAndWait();
                }
            }catch(SQLException | NumberFormatException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Kesalahan saat menginput data!");
                a.showAndWait();

                e.printStackTrace();
            }
        }else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Tidak bisa menghubungi database!");
            a.showAndWait();
        }
    }

    @FXML
    void clearForm(ActionEvent event) {
        itemNameInput.setText("");
        itemDescriptionInput.setText("");
        itemPriceInput.setText("");
        stockQuantityInput.setText("");
    }

    @FXML
    void goBack(ActionEvent event) throws IOException {
        Stage originalStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("item-list.fxml"));
        originalStage.setScene(new Scene(fxmlLoader.load(), 800, 600));
    }
}
