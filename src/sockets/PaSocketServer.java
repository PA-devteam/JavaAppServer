/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author florian
 */
public class PaSocketServer extends Thread implements Runnable {

    private ServerSocket srv;

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
        while (true) {
            try {
                // Create client socket
                Socket client = this.srv.accept();
                System.out.println("Client received");
                
                PaSocketClient paclient=new PaSocketClient(client);
                paclient.start();

             
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}
