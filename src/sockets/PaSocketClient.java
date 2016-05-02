/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;

import app.DbManager;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author florian
 */
public class PaSocketClient extends Thread implements Runnable {
    
    private Socket sock;
    private PrintWriter writer;
    private BufferedInputStream reader;
    
    private ObjectOutputStream objectWriter;
    private ObjectInputStream objectReader;
    private String nameTest;

    public PaSocketClient() {
        this.init();        
    }
    
    public PaSocketClient(Socket pSocket) {
        this.init();
        this.sock = pSocket;
    }
    
   
    
    private void init() {
        // Init writer and reader for String communication
        this.writer = null;
        this.reader = null;
        
        // Init writer and reader for Object communication
        this.objectWriter = null;
        this.objectReader = null;
    }
    

    @Override
 public void run() {
        try {
          
            // Initialise stream handlers
            initStreamHandlers(true);
            
            // Construct message
            /*SocketMessage msg = new SocketMessage();
            msg.setMessage("Hello server");*/
            
            // Send response to server            
            //this.sendObject(msg);
            
            //On attend la réponse
            Object psm = (Object)this.readObject();
            PaSocketMessage message=(PaSocketMessage)psm;
           
           
            switch(message.getAction()){ 
                case LOGIN:
                    PaSocketMessageLogin login=(PaSocketMessageLogin) message;
                    String usrname=login.getUserName();
                    String request="SELECT username from USERS WHERE username='"+usrname+"'";
                    try {
                    // Initialise SQL statement
                    Statement stmt = DbManager.conn.createStatement();

                    // E.g. : DROP TABLE
                    //stmt.executeUpdate( "DROP TABLE table1" );
                    // E.g. : CREATE TABLE
                    //stmt.executeUpdate( "CREATE TABLE table1 ( user varchar(50) )" );
                    // E.g. : INSERT DATA
                    //stmt.executeUpdate( "INSERT INTO table1 ( user ) VALUES ( 'Claudio' )" );   

                    // Execute SQL query from SQL statement
                    ResultSet rs = stmt.executeQuery(request);
                    String responseContent="";
                    // Process data from SQL result set
                    while( rs.next() ) {
                        String name = rs.getString("username");
                        
                        if(responseContent != null) {
                            responseContent = responseContent + "," + name;
                        } else {
                            responseContent = name;
                        }

                        System.out.println( "    " + name );
                    }

                    // Close statement
                    stmt.close();                
                } catch(SQLException e) {
                    System.err.println(e);
                }
                    break;
                default:
                    System.out.println("Passe ici");
                    break;
            }
            //Envoi chez le client
            this.sendObject(message);
            // Handle server response
            //} catch(IOException|ClassNotFoundException e) {
            // System.err.println(e);
            //}
        } catch (IOException|ClassNotFoundException e) {
//            Logger.getLogger(PaSocketClient.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(e);
        }
 }
    private void initStreamHandlers(Boolean pFlushWriter) throws IOException {
        // Get output stream
        OutputStream output = this.sock.getOutputStream();
        // Get input stream
        InputStream  input  = this.sock.getInputStream();

        // Init writer and reader for String communication
        this.writer = new PrintWriter(output, pFlushWriter);
        this.reader = new BufferedInputStream(input);
        
        // Init writer and reader for Object communication
        this.objectWriter = new ObjectOutputStream(output);
        this.objectReader = new ObjectInputStream(input);
    }
    
    private String read() throws IOException {
        String response;
        int inputStream;
        byte[] b = new byte[4096];

        inputStream = this.reader.read(b);
        response    = new String(b, 0, inputStream);

        System.out.println("\t * " + this.nameTest + " Received message " + response);     
        
        return response;         
    }
    
    private PaSocketMessage readObject() throws IOException, ClassNotFoundException {
        PaSocketMessage response;
        Object msg;
        
        msg = this.objectReader.readObject();
        
        response = (PaSocketMessage) msg;
        
        System.out.println("\t * " + this.nameTest + " Received message " + response.getAction());  
        
        return response;
    }
    
    private void send(String pMsg) throws IOException {
        // Write response into the buffer
        this.writer.write(pMsg);

        // Writing buffer content to the server
        this.writer.flush();
        
        System.out.println(this.nameTest + " : " + pMsg);
    }
    
   private void sendObject(PaSocketMessage pMsg) throws IOException {
        this.objectWriter.writeObject(pMsg);
        this.objectWriter.flush();
        
        System.out.println(this.nameTest + " : " + pMsg.getAction());        
    }
    
}
