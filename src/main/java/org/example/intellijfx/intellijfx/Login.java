package org.example.intellijfx.intellijfx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.intellijfx.intellijfx.data.LoginManager;
import org.example.intellijfx.intellijfx.models.JDBC;
import org.example.intellijfx.intellijfx.models.User;
import org.example.intellijfx.intellijfx.models.User2;

import javax.xml.transform.Result;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Properties;

public class Login {

    @FXML
    private BorderPane root;

    @FXML
    private Button cancelButton;

    @FXML
    private VBox vbox;

    @FXML
    private FlowPane header;

    @FXML
    private TextField passwordInput;

    @FXML
    private Label registerRedir;

    @FXML
    private Button signinButton;

    @FXML
    private TextField userEmailInput;

    @FXML
    void initialize() {
        try (InputStream input = new FileInputStream("C:/Users/Gilland/AppData/Local/config.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            if (JDBC.getConnection().isPresent()) {
                try {
                    Statement s = JDBC.getConnection().get().createStatement();
                    ResultSet rs = s.executeQuery("SELECT * FROM users WHERE user_id = "+prop.getProperty("login"));
                    if (rs.next()) {
                        LoginManager.loggedIn = Optional.of(new User2(rs.getInt("user_id"), rs.getString("name"), rs.getString("email"), rs.getString("phone"), rs.getString("address"), rs.getString("contact_info"), rs.getString("role")));
//                        propLogin();
                        //graaaahhh cant figure out how to redirect after logging in with properties
                        userEmailInput.setText(rs.getString("email"));
                        passwordInput.setText(rs.getString("password"));
                    }
                }catch(SQLException e) {e.printStackTrace();}
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    void propLogin() throws IOException {
        Stage originalStage = (Stage) root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("dashboard.fxml"));
        originalStage.setScene(new Scene(loader.load(), 800, 600));
    }

    @FXML
    void signinAction() throws IOException {
        if (userEmailInput.getText().toString().isEmpty() || passwordInput.getText().toString().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Email dan/atau password belom terisi");
            a.showAndWait();

            return;
        }

        if (JDBC.getConnection().isPresent()) {
            try {
                Statement statement = JDBC.getConnection().get().createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE email = '"+userEmailInput.getText()+"' AND password = '"+passwordInput.getText()+"';");
                if (rs.next()) {
                    Optional<User2> foundUser = Optional.of(new User2(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)));
                    LoginManager.loggedIn = foundUser;

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
                }else {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setContentText("User Id dan/atau password tidak sesuai!");
                    a.showAndWait();
                }
            }catch(SQLException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("Error koneksi database!");
                a.showAndWait();

                e.printStackTrace();
            }
        }else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Error koneksi database!");
            a.showAndWait();
        }
    }

    @FXML
    void cancelAction() {
        Platform.setImplicitExit(false);
        Platform.exit();
    }

    @FXML
    void signupRedir(MouseEvent event) throws IOException {
        Stage originalStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("register.fxml"));
        originalStage.setScene(new Scene(fxmlLoader.load(), 800, 600));
    }
}
