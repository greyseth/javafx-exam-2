package org.example.intellijfx.intellijfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.example.intellijfx.intellijfx.data.EditManager;
import org.example.intellijfx.intellijfx.models.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EditAccount {
    @FXML
    private TextField accountNameInput;

    @FXML
    private TextArea addressInput;

    @FXML
    private TextArea contactInfoInput;

    @FXML
    private TextField emailInput;

    @FXML
    private FlowPane header;

    @FXML
    private Label headerText;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private TextField phoneInput;

    @FXML
    private ComboBox<String> roleInput;

    User2 editingUser;

    @FXML
    void initialize() {
        headerText.setText("Editing Account "+EditManager.editingUserId);

        ObservableList<String> roles = FXCollections.observableArrayList();
        roles.add("Customer");
        roles.add("Supplier");
        roles.add("Admin");
        roleInput.setItems(roles);

        if (JDBC.getConnection().isPresent()) {
            try {
                Statement statement = JDBC.getConnection().get().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE user_id = "+ EditManager.editingUserId+";");
                if (rs.next()) {
                    editingUser = new User2(rs.getInt("user_id"), rs.getString("name"), rs.getString("email"), rs.getString("phone"), rs.getString("address"), rs.getString("contact_info"), rs.getString("role"));

                    accountNameInput.setText(editingUser.getName());
                    addressInput.setText(editingUser.getAddress());
                    phoneInput.setText(editingUser.getPhone());
                    emailInput.setText(editingUser.getEmail());
                    passwordInput.setText(rs.getString("password"));
                    contactInfoInput.setText(editingUser.getContactInfo());
                    roleInput.setValue(editingUser.getRole());
                }else {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Akun tidak ditemukan!");
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
                PreparedStatement ps = JDBC.getConnection().get().prepareStatement("DELETE FROM users WHERE user_id = "+EditManager.editingUserId );
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    Tools.changeScene(event, "account-list.fxml");
                }else {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Gagal menghapus data");
                    a.showAndWait();
                }
            }catch(SQLException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Kesalahan saat menghapus data");
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
        Tools.changeScene(event, "account-list.fxml");
    }

    @FXML
    void updateAction(ActionEvent event) throws IOException {
        String name = accountNameInput.getText();
        String email = emailInput.getText();
        String phone = phoneInput.getText();
        String address = addressInput.getText();
        String contact = contactInfoInput.getText();
        String role = roleInput.getValue();
        String password = passwordInput.getText();

        if (JDBC.getConnection().isPresent()) {
            try {
                String query = "UPDATE users SET name = ?, email = ?, phone = ?, address = ?, contact_info = ?, role = ?, password = ? WHERE user_id = ?;";
                PreparedStatement ps = JDBC.getConnection().get().prepareStatement(query);
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, phone);
                ps.setString(4, address);
                ps.setString(5, contact);
                ps.setString(6, role);
                ps.setString(7, password);
                ps.setInt(8, EditManager.editingUserId);

                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setContentText("Berhasil mengubah data");
                    a.showAndWait();
                }else {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("Gagal menghapus data");
                    a.showAndWait();
                }
            }catch(SQLException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Kesalahan saat menghapus data");
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
