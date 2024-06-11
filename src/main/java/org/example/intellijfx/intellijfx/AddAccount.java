package org.example.intellijfx.intellijfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import org.example.intellijfx.intellijfx.models.JDBC;
import org.example.intellijfx.intellijfx.models.Tools;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddAccount {
    @FXML
    private TextField accountNameInput;

    @FXML
    private TextArea addressInput;

    @FXML
    private Button clearFormButton;

    @FXML
    private TextField emailInput;

    @FXML
    private FlowPane header;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private TextField phoneInput;

    @FXML
    private TextArea contactInfoInput;

    @FXML
    private ComboBox<String> roleInput;

    @FXML
    private Button returnButton;

    @FXML
    void initialize() {
        ObservableList<String> observableList = FXCollections.observableArrayList();
        observableList.add("Customer");
        observableList.add("Supplier");
        observableList.add("Admin");

        roleInput.setItems(observableList);
        roleInput.setValue("Customer");
    }

    @FXML
    void goBack(ActionEvent event) throws IOException {
        Tools.changeScene(event, "account-list.fxml");
    }

    @FXML
    void clearForm() {
        accountNameInput.setText("");
        addressInput.setText("");
        emailInput.setText("");
        passwordInput.setText("");
        phoneInput.setText("");
        contactInfoInput.setText("");
        roleInput.setValue("Customer");
    }

    @FXML
    void saveAction() {
        String name = accountNameInput.getText();
        String address = addressInput.getText();
        String contactInfo = contactInfoInput.getText();
        String email = emailInput.getText();
        String phone = phoneInput.getText();
        String password = passwordInput.getText();

        if (name.isEmpty() || address.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || contactInfo.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Semua field harus terisi!");
            a.showAndWait();

            return;
        }

        if (JDBC.getConnection().isPresent()) {
            try {
                String query = "INSERT INTO users(name, email, phone, address, contact_info, role, password) VALUES(?, ?, ?, ?, ?, ?, ?);";
                PreparedStatement statement = JDBC.getConnection().get().prepareStatement(query);
                statement.setString(1, name);
                statement.setString(2, email);
                statement.setString(3, phone);
                statement.setString(4, address);
                statement.setString(5, contactInfo);
                statement.setString(6, roleInput.getValue());
                statement.setString(7, password);

                int affectedRows = statement.executeUpdate();
                if (affectedRows > 0) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setContentText("Akun Baru Telah Dibuat");
                    a.showAndWait();
                }else {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Tidak bisa membuat akun baru");
                    a.showAndWait();
                }
            }catch(SQLException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Kesalahan saat menambahkan data!");
                a.showAndWait();
            }
        }else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Tidak bisa menghubungi database");
            a.showAndWait();
        }
    }
}
