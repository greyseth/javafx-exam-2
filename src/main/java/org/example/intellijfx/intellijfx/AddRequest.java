package org.example.intellijfx.intellijfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.example.intellijfx.intellijfx.models.JDBC;

import java.io.IOException;
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
//                    Button
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
    }

    @FXML
    void goBack(ActionEvent event) throws IOException {

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

    }

    @FXML
    void saveAction() {

    }
}
