package org.example.intellijfx.intellijfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.example.intellijfx.intellijfx.data.LoginManager;
import org.example.intellijfx.intellijfx.models.Tools;
import org.example.intellijfx.intellijfx.models.User2;

import java.io.*;
import java.util.Optional;
import java.util.Properties;

public class Dashboard {
    @FXML
    BorderPane container;

    @FXML
    Label welcomeText;

    @FXML
    Button masterButton;

    @FXML
    Button transactionButton;

    @FXML
    Button stocksButton;

    @FXML
     Button addAccountButton;

    @FXML
    Button addProductButton;

    @FXML
    private Label userAddressDisplay;

    @FXML
    private Label userContactDisplay;

    @FXML
    private Label userEmailDisplay;

    @FXML
    private Label userIdDisplay;

    @FXML
    private Label userNameDisplay;

    @FXML
    private Label userPhoneDisplay;

    @FXML
    private FlowPane profilePanel;

    @FXML
    private FlowPane welcomePanel;

    @FXML
    private ImageView accImage;

    boolean showingProfile = false;

    @FXML
    void initialize() throws IOException {
        if (LoginManager.loggedIn.isEmpty()) NotLoggedIn();
        else {
            User2 user = LoginManager.loggedIn.get();

            welcomeText.setText("Selamat Datang, " + user.getName());

            userIdDisplay.setText("User ID: "+user.getUserId());
            userNameDisplay.setText("Name: "+user.getName());
            userEmailDisplay.setText("Email: "+user.getEmail());
            userPhoneDisplay.setText("Phone: "+user.getPhone());
            userAddressDisplay.setText("Address: "+user.getAddress());
            userContactDisplay.setText("Contact Info: "+user.getContactInfo());

            Image accountImage = new Image(getClass().getResourceAsStream("/assets/account.png"));
            accImage.setImage(accountImage);

            if (user.getRole().equals("Customer")) {
                transactionButton.setVisible(true);
            }else if (user.getRole().equals("Supplier") || user.getRole().equals("Admin")) {
                transactionButton.setVisible(true);
                masterButton.setVisible(true);
                stocksButton.setVisible(true);
            }

            if (user.getRole().equals("Admin")) {
                addProductButton.setVisible(true);
                addAccountButton.setVisible(true);
            }
        }
    }

    void NotLoggedIn() throws IOException {
        Stage originalStage = (Stage) container.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        originalStage.setScene(new Scene(fxmlLoader.load(), 800, 600));
    }

    @FXML
    void logoutAction(ActionEvent event) throws IOException {
        LoginManager.loggedIn = Optional.empty();

        try (InputStream inputStream = new FileInputStream("C:/Users/Gilland/AppData/Local/config.properties"); OutputStream outputStream = new FileOutputStream("C:/Users/Gilland/AppData/Local/config.properties")) {
            Properties prop = new Properties();
            prop.load(inputStream);
            prop.clear();
            prop.store(outputStream, null);
        }catch (IOException e) {e.printStackTrace();}

        Tools.changeScene(event, "login.fxml");
    }

    @FXML
    void profileAction() {
        showingProfile = !showingProfile;

        welcomePanel.setVisible(!showingProfile);
        profilePanel.setVisible(showingProfile);
    }

    @FXML
    void addProductAction(ActionEvent event) throws IOException {
        Tools.changeScene(event, "item-list.fxml");
    }

    @FXML
    void addAccountAction(ActionEvent event) throws IOException {
        Tools.changeScene(event, "account-list.fxml");
    }

    @FXML
    void manageStocksAction(ActionEvent event) throws IOException {
        Tools.changeScene(event, "supply-order.fxml");
    }

    @FXML
    void transactionAction(ActionEvent event) throws IOException {
        Tools.changeScene(event, "customer-order.fxml");
    }

    @FXML
    void masterAction(ActionEvent event) throws IOException {
        Tools.changeScene(event, "inventory-list.fxml");
    }
}
