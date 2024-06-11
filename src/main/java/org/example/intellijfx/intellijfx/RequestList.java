package org.example.intellijfx.intellijfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import org.example.intellijfx.intellijfx.data.EditManager;
import org.example.intellijfx.intellijfx.models.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

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

    LinkedList<Request> requests = new LinkedList<>();

    @FXML
    void initialize() {
        if (JDBC.getConnection().isPresent()) {
            try {
                Statement s = JDBC.getConnection().get().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = s.executeQuery("SELECT request.*, users.name FROM request LEFT JOIN users ON users.user_id = request.user_id GROUP BY request.request_id;");
                boolean hasContent = false;
                if (rs.last()) {
                    if (rs.getRow() > 0) hasContent = true;
                }

                if (hasContent) {
                    rs.beforeFirst();

                    while(rs.next()) {
                        Request newR = new Request(rs.getInt("request_id"), rs.getInt("product_id"), rs.getString("reason"), rs.getDate("request_date").toString(), rs.getString("status"), rs.getString("name"));
                        requests.add(newR);
                    }

                    ObservableList<Request> ol = FXCollections.observableArrayList();
                    ol.addAll(requests);

                    reqIdColumn.setCellValueFactory(new PropertyValueFactory<>("requestId"));
                    requesterColumn.setCellValueFactory(new PropertyValueFactory<>("requester"));
                    productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
                    reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
                    dateColumn.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
                    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
                    actionsColumn.setCellFactory(ActionButtonTableCell.<Request>forTableColumn("Accept", (Request i) -> {
                        if (JDBC.getConnection().isPresent()) {
                            try {
                                PreparedStatement ps = JDBC.getConnection().get().prepareStatement("UPDATE request SET status = 'accepted' WHERE request_id = "+i.getRequestId());
                                int affectedRows = ps.executeUpdate();
                                Alert a = new Alert(Alert.AlertType.INFORMATION);
                                a.setContentText("Berhasil menerima permintaan");
                                a.showAndWait();
                                updateTable();
                            }catch(SQLException e) {
                                Alert a = new Alert(Alert.AlertType.ERROR);
                                a.setContentText("Gagal melakukan operasi");
                                a.showAndWait();
                                e.printStackTrace();
                            }
                        }else {
                            Alert a = new Alert(Alert.AlertType.ERROR);
                            a.setContentText("Tidak bisa menghubungi database");
                            a.showAndWait();
                        }
                        return i;
                    }));

                    requestsTable.setItems(ol);
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
    void goBack(ActionEvent event) throws IOException {
        Tools.changeScene(event, "dashboard.fxml");
    }

    @FXML
    void addRequestAction(ActionEvent event) throws IOException {
        Tools.changeScene(event, "add-request.fxml");
    }

    void updateTable() {
        requestsTable.getItems().clear();

        if (JDBC.getConnection().isPresent()) {
            try {
                Statement s = JDBC.getConnection().get().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = s.executeQuery("SELECT request.*, users.name FROM request LEFT JOIN users ON users.user_id = request.user_id GROUP BY request.request_id;");
                boolean hasContent = false;
                if (rs.last()) {
                    if (rs.getRow() > 0) hasContent = true;
                }

                if (hasContent) {
                    rs.beforeFirst();
                    requests.clear();

                    while(rs.next()) {
                        Request newR = new Request(rs.getInt("request_id"), rs.getInt("product_id"), rs.getString("reason"), rs.getDate("request_date").toString(), rs.getString("status"), rs.getString("name"));
                        requests.add(newR);
                    }

                    ObservableList<Request> ol = FXCollections.observableArrayList();
                    ol.addAll(requests);

                    reqIdColumn.setCellValueFactory(new PropertyValueFactory<>("requestId"));
                    requesterColumn.setCellValueFactory(new PropertyValueFactory<>("requester"));
                    productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
                    reasonColumn.setCellValueFactory(new PropertyValueFactory<>("reason"));
                    dateColumn.setCellValueFactory(new PropertyValueFactory<>("requestDate"));
                    statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
                    actionsColumn.setCellFactory(ActionButtonTableCell.<Request>forTableColumn("Accept", (Request i) -> {
                        if (JDBC.getConnection().isPresent()) {
                            try {
                                PreparedStatement ps = JDBC.getConnection().get().prepareStatement("UPDATE request SET status = 'accepted' WHERE request_id = "+i.getRequestId());
                                int affectedRows = ps.executeUpdate();
                                Alert a = new Alert(Alert.AlertType.INFORMATION);
                                a.setContentText("Berhasil menerima permintaan");
                                a.showAndWait();
                            }catch(SQLException e) {
                                Alert a = new Alert(Alert.AlertType.ERROR);
                                a.setContentText("Gagal melakukan operasi");
                                a.showAndWait();
                                e.printStackTrace();
                            }
                        }else {
                            Alert a = new Alert(Alert.AlertType.ERROR);
                            a.setContentText("Tidak bisa menghubungi database");
                            a.showAndWait();
                        }
                        return i;
                    }));

                    requestsTable.setItems(ol);
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
}
