package org.example.intellijfx.intellijfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import org.example.intellijfx.intellijfx.data.EditManager;
import org.example.intellijfx.intellijfx.models.Item;
import org.example.intellijfx.intellijfx.models.JDBC;
import org.example.intellijfx.intellijfx.models.Tools;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EditItem {
    @FXML
    private Button deleteButton;

    @FXML
    private FlowPane header;

    @FXML
    private Label headerText;

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
    private Button updateButton;

    Item editingItem;

    @FXML
    void initialize() {
        headerText.setText("Editing Item "+EditManager.editingItemId);

        if (JDBC.getConnection().isPresent()) {
            try {
                Statement statement = JDBC.getConnection().get().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = statement.executeQuery("SELECT * FROM product WHERE product_id = "+ EditManager.editingItemId+";");
                if (rs.next()) {
                    editingItem = new Item(rs.getInt("product_id"), rs.getString("name"), "", rs.getString("description"), rs.getInt("price"), rs.getInt("stock_quantity"));

                    itemNameInput.setText(editingItem.getItemName());
                    itemDescriptionInput.setText(editingItem.getDescription());
                    itemPriceInput.setText(String.valueOf(editingItem.getPrice()));
                    stockQuantityInput.setText(String.valueOf(editingItem.getStock()));
                }else {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Barang tidak ditemukan!");
                    a.showAndWait();
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
    void deleteAction(ActionEvent event) throws IOException {
        if (JDBC.getConnection().isPresent()) {
            try {
                PreparedStatement s = JDBC.getConnection().get().prepareStatement("DELETE FROM product WHERE product_id = "+EditManager.editingItemId);
                int affectedRows = s.executeUpdate();

                Tools.changeScene(event, "item-list.fxml");
            }catch(SQLException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Kesalahan saat mengubah data");
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
        Tools.changeScene(event, "item-list.fxml");
    }

    @FXML
    void updateAction(ActionEvent event) {
        String name = itemNameInput.getText();
        String description = itemDescriptionInput.getText();
        String priceText = itemPriceInput.getText();
        String stockText = stockQuantityInput.getText();

        if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Semua field harus terisi!");
            a.showAndWait();

            return;
        }

        if (JDBC.getConnection().isPresent()) {
            try {
                int price = Integer.parseInt(priceText);
                int stock = Integer.parseInt(stockText);

                String query = "UPDATE product SET name = ?, description = ?, price = ?, stock_quantity = ? WHERE product_id = ?;";
                PreparedStatement ps = JDBC.getConnection().get().prepareStatement(query);
                ps.setString(1, name);
                ps.setString(2, description);
                ps.setInt(3, price);
                ps.setInt(4, stock);
                ps.setInt(5, EditManager.editingItemId);

                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setContentText("Berhasil mengubah data");
                    a.showAndWait();
                }else {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Gagal mengubah data");
                    a.showAndWait();
                }
            }catch(SQLException | NumberFormatException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Kesalahan saat mengubah data");
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
