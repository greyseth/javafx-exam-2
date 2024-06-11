package org.example.intellijfx.intellijfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.intellijfx.intellijfx.models.JDBC;
import org.example.intellijfx.intellijfx.models.User;

import java.io.IOException;
import java.sql.*;
import java.util.LinkedList;

public class UserView {
    LinkedList<User> userData = new LinkedList<>();

    @FXML
    FlowPane header;

    @FXML
    Label loadingText;

    @FXML
    TableView<User> userTable;

    @FXML
    TableColumn<User, Integer> userId;

    @FXML
    TableColumn<User, String> username;

    @FXML
    TableColumn<User, String> email;

    @FXML
    TableColumn<User, String> password;

    @FXML
    TableColumn<User, Boolean> isAdmin;

    @FXML
    Button addUserButton;

    @FXML
    Button returnButton;

    @FXML
    public void initialize() {
        userTable.setVisible(false);
        loadingText.setVisible(true);

        if (JDBC.getConnection().isPresent()) {
            try {
                Statement statement = JDBC.getConnection().get().createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM users");
                while(rs.next()) {
                    User addUser = new User(rs.getInt(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4),
                            rs.getBoolean(5));
                    userData.add(addUser);
                }

//                JDBC.getConnection().get().close();

                ObservableList<User> observableList = FXCollections.observableArrayList();
                userId.setCellValueFactory(new PropertyValueFactory<>("userId"));
                username.setCellValueFactory(new PropertyValueFactory<>("username"));
                email.setCellValueFactory(new PropertyValueFactory<>("email"));
                password.setCellValueFactory(new PropertyValueFactory<>("password"));
                isAdmin.setCellValueFactory(new PropertyValueFactory<>("isAdmin"));

                observableList.addAll(userData);
                userTable.setItems(observableList);

                loadingText.setVisible(false);
                userTable.setVisible(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else loadingText.setText("A database error has occurred");
    }

    @FXML
    void addUser() {
        System.out.println("Change to add user page");
    }

    @FXML
    void goBack(ActionEvent event) throws IOException {
        Stage originalStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
        originalStage.setScene(new Scene(fxmlLoader.load(), 800, 600));
    }
}