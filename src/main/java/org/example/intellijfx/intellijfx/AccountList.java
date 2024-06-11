package org.example.intellijfx.intellijfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.example.intellijfx.intellijfx.data.EditManager;
import org.example.intellijfx.intellijfx.models.*;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class AccountList {

    @FXML
    private TableColumn<User2, Integer> accIdColumn;

    @FXML
    private TableView<User2> accountsTable;

    @FXML
    private TableColumn<User2, Button> actionsColumn;

    @FXML
    private TableColumn<User2, String> addressColumn;

    @FXML
    private TableColumn<User2, String> contactColumn;

    @FXML
    private TableColumn<User2, String> emailColumn;

    @FXML
    private TableColumn<User2, String> nameColumn;

    @FXML
    private TableColumn<User2, String> phoneColumn;

    @FXML
    private TableColumn<User2, String> roleColumn;

    LinkedList<User2> users = new LinkedList<>();

    @FXML
    void initialize() {
        if (JDBC.getConnection().isPresent()) {
            try {
                Statement statement = JDBC.getConnection().get().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = statement.executeQuery("SELECT * FROM users");
                boolean hasContent = false;
                if (rs.last()) {
                    if (rs.getRow() > 0) hasContent = true;
                }

                if (hasContent) {
                    rs.beforeFirst();

                    while(rs.next()) {
                        User2 u = new User2(rs.getInt("user_id"), rs.getString("name"), rs.getString("email"), rs.getString("phone"), rs.getString("address"), rs.getString("contact_info"), rs.getString("role"));
                        users.add(u);
                    }

                    ObservableList<User2> observableList = FXCollections.observableArrayList();
                    observableList.addAll(users);

                    accIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
                    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
                    emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
                    phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
                    addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
                    contactColumn.setCellValueFactory(new PropertyValueFactory<>("contactInfo"));
                    roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
                    actionsColumn.setCellFactory(ActionButtonTableCell.<User2>forTableColumn("Edit", (User2 i) -> {
                        EditManager.editingUserId = i.getUserId();
                        try {
                            toEditAccount();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return i;
                    }));

                    accountsTable.setItems(observableList);
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
    void toEditAccount() throws IOException {
        Stage originalStage = (Stage) accountsTable.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("edit-account.fxml"));
        originalStage.setScene(new Scene(loader.load(), 800, 600));
    }

    @FXML
    void addItemAction(ActionEvent event) throws IOException {
        Tools.changeScene(event, "add-account.fxml");
    }

    @FXML
    void goBack(ActionEvent event) throws IOException {
        Tools.changeScene(event, "dashboard.fxml");
    }
}
