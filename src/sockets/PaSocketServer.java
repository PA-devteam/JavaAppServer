package sockets;

import equations.EquationsManager;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaSocketServer extends Thread implements Runnable {

    private ServerSocket srv;
    private static ArrayList<PaSocketClient> clients = new ArrayList<>();

    public PaSocketServer(String pIp, int pPort, int pMaxCon) {
        try {
            // Get internet address by host name
            InetAddress host = InetAddress.getByName(pIp);

            // Initialise socket server
            this.srv = new ServerSocket(pPort, pMaxCon, host);

            System.out.println("SocketServer has been intialised on " + pIp + ":" + pPort);
        } catch (IOException e) {
            if (e instanceof BindException) {
                System.err.println("Unable to initialise socket server, socket port is already in use !");
            } else {
                System.err.println(e);
            }
        }
    }

    @Override
    public void run() {
        EquationsManager.init();
        
        while (true) {
            try {
                
                // Create client socket
                Socket client = this.srv.accept();
                System.out.println("Client received");

                // Intialise socket client handler
                PaSocketClient paClient = new PaSocketClient(client);
                // Start socket client handler
                paClient.start();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
    
    public static void broadcast() {
        for(PaSocketClient soc : clients) {
            try {
                PaSocketResponse res = new PaSocketResponse();
                res.setAction(PaSocketAction.MISC);
                soc.sendObject(res);
            } catch (IOException ex) {
                Logger.getLogger(PaSocketServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
