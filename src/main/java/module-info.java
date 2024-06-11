module org.example.intellijfx.intellijfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;


    opens org.example.intellijfx.intellijfx to javafx.fxml;
    exports org.example.intellijfx.intellijfx;
    exports org.example.intellijfx.intellijfx.models;
}