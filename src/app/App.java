package app;

import config.ConfigManager;
import database.*;
import sockets.PaSocketServer;

public class App {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Get configuration manager
        ConfigManager config = ConfigManager.getInstance();

        // Load config file
        // Note : file config.properties is not visible in Netbeans 'Projects' panel
        config.load("config.properties");

        // Check if properties has been loaded
        if (!config.getProp().isEmpty()) {
            // Get databse port
            String dbPort = config.getProperty("db_port");
            // Get database connection url
            String dbUrl = config.getProperty("db_url");
            // Get database username
            String dbUsr = config.getProperty("db_username");
            // Get database password
            String dbPwd = config.getProperty("db_password");
            
            // Initialise the TCP database server
            ServerManager.init(dbPort);
            // Start the database server
            ServerManager.start();
            
            // Initialise db connection
            DbManager.init(dbUrl, dbUsr, dbPwd);
            
            // Check if there is an established connection to a database
            if(DbManager.conn != null) {                
                // Get socket server ip
                String ip  = config.getProperty("ss_ip");
                // Get socket server port
                int port   = Integer.parseInt(config.getProperty("ss_port"));
                // Get socket server max connection allowed
                int maxCon = Integer.parseInt(config.getProperty("ss_max_con"));
                                
                // Initialise a new socket server
                PaSocketServer srv = new PaSocketServer(ip, port, maxCon);
                // Start the socket server
                srv.start();                
            } else {
                System.err.println("No database connection !");
            }
        } else {
            System.err.println("No properties found !");
        }
    }
}