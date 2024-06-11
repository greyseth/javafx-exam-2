package org.example.intellijfx.intellijfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import org.example.intellijfx.intellijfx.models.Request;
import org.example.intellijfx.intellijfx.models.Tools;

import java.io.IOException;

public class RequestList {
    @FXML
    private TableColumn<Request, Button> actionsColumn;

    @FXML
    private TableColumn<Request, String> dateColumn;

    @FXML
    private TableColumn<Request, Integer> productIdColumn;

    @FXML
    private TableColumn<Request, String> reasonColumn;

    @FXML
    private TableColumn<Request, Integer> reqIdColumn;

    @FXML
    private TableColumn<Request, String> requesterColumn;

    @FXML
    private TableView<Request> requestsTable;

    @FXML
    private TableColumn<Request, String> statusColumn;

    @FXML
    void goBack(ActionEvent event) throws IOException{
        Tools.changeScene(event, "dashboard.fxml");
    }

    @FXML
    void addRequestAction(ActionEvent event) throws IOException {
        Tools.changeScene(event, "add-request.fxml");
    }
}
