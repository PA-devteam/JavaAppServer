/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import app.App;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.h2.tools.Server;

/**
 *
 * @author florian
 */
public class ServerManager {

    private static Server server;

    public static Server getServer() {
        return server;
    }

    public static void setServer(Server server) {
        ServerManager.server = server;
    }

    public ServerManager() {
    }

    public static void init(String port) {
        try {

            server = Server.createTcpServer("-tcpPort", port, "-tcpAllowOthers");

            //System.out.println("Ca marche !!");
        } catch (SQLException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void start() {
        if (server != null) {
            try {
                server.start();
            } catch (SQLException ex) {
                Logger.getLogger(ServerManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void StopServer() {
        if (server.isRunning(true)) {
            server.stop();
        } else {
            System.out.println("Déjà à l'arrêt");
        }
    }

    /*public static void RestartServer()
    {
        if(server.isRunning(true))
        {
            server.
        }
        else
        {

        }
    }*/
}
