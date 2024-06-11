package org.example.intellijfx.intellijfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.example.intellijfx.intellijfx.data.LoginManager;
import org.example.intellijfx.intellijfx.models.JDBC;
import org.example.intellijfx.intellijfx.models.Tools;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AddRequest {
    @FXML
    TextField productSelectDisplay;
    @FXML
    FlowPane productSelectPane;
    @FXML
    VBox productSelectContainer;

    @FXML
    ComboBox<String> reasonInput;
    @FXML
    TextArea reasonOtherInput;
    @FXML
    ComboBox<String> statusInput;

    int selectedItemId = -1;

    @FXML
    void initialize() {
        //Loads product selection
        if (JDBC.getConnection().isPresent()) {
            try {
                Statement s = JDBC.getConnection().get().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = s.executeQuery("SELECT product_id, name FROM product;");
                boolean hasContent = false;
                if (rs.last()) {
                    if (rs.getRow() > 0) hasContent = true;
                }

                if (hasContent) {
                    rs.beforeFirst();
                    while(rs.next()) {
                        int productId = rs.getInt("product_id");
                        String name = rs.getString("name");

                        Button addButton = new Button(name);
                        addButton.setOnAction(e -> {
                            selectedItemId = productId;
                            productSelectDisplay.setText(name);
                            productSelectPane.setVisible(false);
                        });

                        productSelectContainer.getChildren().add(addButton);
                    }
                }
            }catch(SQLException e) {e.printStackTrace();}
        }

        //initializes combo box options
        ObservableList<String> reasons = FXCollections.observableArrayList();
        reasons.add("Low on Item");
        reasons.add("Out of Stock");
        reasons.add("Other (Fill in Below)");
        reasonInput.setItems(reasons);

        ObservableList<String> statuses = FXCollections.observableArrayList();
        statuses.add("pending");
        statuses.add("accepted");
        statuses.add("denied");
        statusInput.setItems(statuses);
        statusInput.setValue("pending");
    }

    @FXML
    void goBack(ActionEvent event) throws IOException {
        Tools.changeScene(event, "request-list.fxml");
    }

    @FXML
    void productSelectAction() {
        productSelectPane.setVisible(true);
    }

    @FXML
    void productSelectCancel() {
        productSelectPane.setVisible(false);
    }

    @FXML
    void clearForm() {
        selectedItemId = -1;
        productSelectDisplay.setText("");
        reasonInput.setValue("");
        reasonOtherInput.setText("");
        statusInput.setValue("");
    }

    @FXML
    void saveAction() {
        String reason = reasonInput.getValue();
        String reasonOther = reasonOtherInput.getText();
        String status = statusInput.getValue();

        if (selectedItemId == -1 || reason.isEmpty() || status.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Semua field harus terisi!");
            a.showAndWait();

            return;
        }

        if (JDBC.getConnection().isPresent()) {
            try {
                String query = "INSERT INTO request(product_id, reason, request_date, status, user_id) VALUES(?, ?, CURRENT_DATE(), ?, ?);";
                PreparedStatement ps = JDBC.getConnection().get().prepareStatement(query);
                ps.setInt(1, selectedItemId);
                ps.setString(2, reason.equals("Out of Stock")||reason.equals("Low on Item")?reason:reasonOther);
                ps.setString(3, status);
                ps.setInt(4, LoginManager.loggedIn.get().getUserId());

                int affectedRows = ps.executeUpdate();

                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("Berhasil menambahkan permintaan");
                a.showAndWait();
            }catch(SQLException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Gagal menambahkan permintaan");
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
