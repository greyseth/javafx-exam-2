package org.example.intellijfx.intellijfx.models;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.intellijfx.intellijfx.HelloApplication;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Tools {
    public static boolean isMyResultSetEmpty(ResultSet rs) throws SQLException {
        return (!rs.isBeforeFirst() && rs.getRow() == 0);
    }

    public static void changeScene(ActionEvent event, String destination) throws IOException {
        Stage originalStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(destination));
        originalStage.setScene(new Scene(fxmlLoader.load(), 800, 600));
    }
}
