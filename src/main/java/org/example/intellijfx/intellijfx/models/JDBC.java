package org.example.intellijfx.intellijfx.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

public class JDBC {
    static Optional<Connection> connection = Optional.empty();

    public static Optional<Connection> getConnection() {
        if (connection.isEmpty()) {
            String username = "root";
            String password = "";
            String database = "db_pbo";

            String host = "jdbc:mysql://localhost:3306/"+database;

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
//                DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver);
                connection = Optional.ofNullable(DriverManager.getConnection(host, username, password));
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }

        return connection;
    }
}
