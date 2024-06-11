package org.example.intellijfx.intellijfx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.example.intellijfx.intellijfx.data.LoginManager;
import org.example.intellijfx.intellijfx.models.JDBC;
import org.example.intellijfx.intellijfx.models.Tools;
import org.example.intellijfx.intellijfx.models.User2;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.*;
import java.util.Optional;
import java.util.Properties;

public class Register {
    @FXML
    private TextArea contactInfoInput;

    @FXML
    private TextArea addressInput;

    @FXML
    private TextField emailInput;

    @FXML
    private TextField passwordInput;

    @FXML
    private FlowPane header;

    @FXML
    private Label loginRedir;

    @FXML
    private TextField nameInput;

    @FXML
    private TextField phoneInput;

    @FXML
    private Button signinButton;

    @FXML
    void signinAction() throws IOException {
        String name = nameInput.getText();
        String email = emailInput.getText();
        String password = passwordInput.getText();
        String phone = phoneInput.getText();
        String address = addressInput.getText();
        String contactInfo = contactInfoInput.getText();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || contactInfo.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Semua field harus terisi!");
            a.showAndWait();

            return;
        }

        if (JDBC.getConnection().isPresent()) {
            try {
                String query = "INSERT INTO users(name, email, phone, address, contact_info, role, password) VALUES('"+name+"', '"+email+"', '"+phone+"', '"+address+"', '"+contactInfo+"', 'Customer', '"+password+"');";
                PreparedStatement statement = JDBC.getConnection().get().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

                int insertId = 0;

                int affectedRows = statement.executeUpdate();
                if (affectedRows > 0) {
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) insertId = generatedKeys.getInt(1);
                }else return;

                User2 registered = new User2(insertId, name, email, phone, address, contactInfo, "Customer");
                LoginManager.loggedIn = Optional.of(registered);

                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setContentText("Selamat Datang, "+LoginManager.loggedIn.get().getName());
                a.showAndWait();

                //Saves data to preferences
                try (OutputStream outputStream = new FileOutputStream("C:/Users/Gilland/AppData/Local/config.properties")) {
                    Properties prop = new Properties();
                    prop.setProperty("login", String.valueOf(LoginManager.loggedIn.get().getUserId()));
                    prop.store(outputStream, null);
                }catch(IOException e) {
                    e.printStackTrace();
                }

                Stage originalStage = (Stage) header.getScene().getWindow();
                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("dashboard.fxml"));
                originalStage.setScene(new Scene(fxmlLoader.load(), 800, 600));
            }catch(SQLException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Database error");
                a.showAndWait();

                e.printStackTrace();
            }
        }else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Database error");
            a.showAndWait();
        }
    }

    @FXML
    void cancelAction() {
        Platform.setImplicitExit(false);
        Platform.exit();
    }

    @FXML
    void loginRedir(MouseEvent event) throws IOException {
        Stage originalStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        originalStage.setScene(new Scene(fxmlLoader.load(), 800, 600));
    }
}
