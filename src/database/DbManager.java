package database;

import java.sql.*;

public final class DbManager {
 
    public static Connection conn   = null;
    public static boolean isRunning = false;
    
    public static void init(String dbUrl, String dbUsername, String dbPassword) {       
        try {
            Class.forName("org.h2.Driver");

            conn = DriverManager.getConnection(
                    dbUrl, 
                    dbUsername,
                    dbPassword
            );
        } catch(ClassNotFoundException|SQLException e) {
            System.err.println(e);
        }
    }

    public static void close() {
        try {
              
            conn.close();
            System.err.println("Connection to database has been closed !");
        } catch(SQLException e) {
            System.err.println(e);
        }
    }
}