package org.example.intellijfx.intellijfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.example.intellijfx.intellijfx.data.EditManager;
import org.example.intellijfx.intellijfx.models.ActionButtonTableCell;
import org.example.intellijfx.intellijfx.models.Item;
import org.example.intellijfx.intellijfx.models.JDBC;
import org.example.intellijfx.intellijfx.models.Tools;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class ItemList {

    @FXML
    private TableView<Item> itemsTable;

    @FXML
    private TableColumn<Item, Button> actionsColumn;

    @FXML
    private TableColumn<Item,String> descriptionColumn;

    @FXML
    private TableColumn<Item, Integer> itemIdColumn;

    @FXML
    private TableColumn<Item, String> nameColumn;

    @FXML
    private TableColumn<Item, Integer> priceColumn;

    @FXML
    private Button returnButton;

    @FXML
    private TableColumn<Item, Integer> stockQuantityColumn;

    LinkedList<Item> items = new LinkedList<>();

    @FXML
    void initialize() {
        if (JDBC.getConnection().isPresent()) {
            try {
                Statement statement = JDBC.getConnection().get().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rs = statement.executeQuery("SELECT * FROM product");
                boolean hasContent = false;
                if (rs.last()) {
                    if (rs.getRow() > 0) hasContent = true;
                }

                if (hasContent) {
                    rs.beforeFirst();

                    while(rs.next()) {
                        Item i = new Item(rs.getInt("product_id"), rs.getString("name"), "", rs.getString("description"),rs.getInt("price"), rs.getInt("stock_quantity"));
                        items.add(i);
                    }

                    ObservableList<Item> observableList = FXCollections.observableArrayList();
                    observableList.addAll(items);

                    itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("idBarang"));
                    nameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
                    descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
                    priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
                    stockQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
                    actionsColumn.setCellFactory(ActionButtonTableCell.<Item>forTableColumn("Edit", (Item i) -> {
                        EditManager.editingItemId = i.getIdBarang();
                        try {
                            toEditItem();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return i;
                    }));

                    itemsTable.setItems(observableList);
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

    void toEditItem() throws IOException {
        Stage originalStage = (Stage) itemsTable.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("edit-item.fxml"));
        originalStage.setScene(new Scene(fxmlLoader.load(), 800, 600));
    }

    @FXML
    void goBack(ActionEvent event) throws IOException {
        Tools.changeScene(event, "dashboard.fxml");
    }

    @FXML
    void addItemAction(ActionEvent event) throws IOException {
        Tools.changeScene(event, "add-item.fxml");
    }
}
