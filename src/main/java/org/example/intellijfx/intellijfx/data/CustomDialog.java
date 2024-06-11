package org.example.intellijfx.intellijfx.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Stream;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.intellijfx.intellijfx.models.JDBC;

public class CustomDialog {

    private static LinkedList<Integer> OPTION_IDS;
    private static LinkedList<String> OPTIONS;

    private Stage stage = new Stage();

    private String selectedOption = null ;

    public CustomDialog() {
        this(null);
    }

    public CustomDialog(Window parent) {
        var vbox = new VBox();
        // Real app should use an external style sheet:
        vbox.setStyle("-fx-padding: 12px; -fx-spacing: 5px;");

        if (JDBC.getConnection().isPresent()) {
            try {
                Statement statement = JDBC.getConnection().get().createStatement();
                ResultSet rs = statement.executeQuery("SELECT product_id, name FROM product;");
                if (rs.getRow() > 0) {
                    while (rs.next()) {
                        System.out.println(rs.getString(2));
                        OPTION_IDS.add(rs.getInt(1));
                        OPTIONS.add(rs.getString(2));
                    }

                    for (int i = 0; i < OPTIONS.size(); i++) {
                        vbox.getChildren().add(createButton(OPTIONS.get(i)));
                    }

                    var scene = new Scene(vbox);
                    stage = new Stage();
                    stage.initOwner(parent);
                    stage.initModality(Modality.WINDOW_MODAL);
                    stage.setScene(scene);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Button createButton(String text) {
        var button = new Button(text);
        button.setOnAction(e -> {
            selectedOption = text ;
            stage.close();
        });
        return button ;
    }

    public Optional<String> showDialog() {
        selectedOption = null ;
        stage.showAndWait();
        return Optional.ofNullable(selectedOption);
    }
}
