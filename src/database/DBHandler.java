package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHandler extends Config {
    public static Connection connect(){
        String connectionString = "jdbc:mysql://"
                + dbHost
                + ":"
                + dbPort
                + "/"
                + dbName
                +"?autoReconnect=true&useSSL=false";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(connectionString, dbUser, dbPassword);
            return connection;
        }catch (ClassNotFoundException | SQLException e){
            System.out.println("Not connected");
            return null;
        }

    }
}
