package sockets;

import database.DbManager;
import java.io.*;
import java.net.Socket;
import java.sql.*;
import security.BCrypt; // http://www.mindrot.org/projects/jBCrypt/
import app.User;

public class PaSocketClient extends Thread implements Runnable {

    private Socket sock;
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
        // Init writer and reader for Object communication
        this.objectWriter = null;
        this.objectReader = null;
    }

    @Override
    public void run() {
        try {
            // Initialise stream handlers for read & send actions
            initStreamHandlers();

            // Waiting message from client
            PaSocketMessage message = (PaSocketMessage) this.readObject();

            // Get action from client message
            PaSocketAction action = message.getAction();

            // Initialise new socket reponse
            PaSocketResponse response = new PaSocketResponse();

            // Handle action
            switch (action) {
                case LOGIN:
                    // Case message to specific case message
                    PaSocketMessageLogin login = (PaSocketMessageLogin) message;

                    // Get username from message
                    String username = login.getUserName();
                    // Get password from message
                    String password = login.getUserPassword();

                    // Check message parameters
                    if(username.length() > 0 && password.length() > 0) {
                        // Hash received password
                        String hashed = BCrypt.hashpw(password, BCrypt.gensalt());

                        try {
                            // Create SQL request
                            String request = "SELECT firstname,lastname,username,createDate,update,isActive,isDeleted FROM USERS WHERE username='" + username + "'";

                            // Initialise SQL statement
                            Statement stmt = DbManager.conn.createStatement();

                            // Execute SQL query from SQL statement
                            ResultSet rs = stmt.executeQuery(request);

                            User usr = new User();
                            String usrHashed = "";

                            // Process data from SQL result set
                            while (rs.next()) {
                                // Build new User entity
                                usr.setUsername(rs.getString("firstname"));
                                usr.setUsername(rs.getString("lastname"));
                                usr.setCreateDate(rs.getDate("createDate"));
                                usr.setUpdate(rs.getDate("update"));
                                usr.setIsActive(rs.getBoolean("isActive"));
                                usr.setIsDeleted(rs.getBoolean("isDeleted"));
                                
                                // Get hashed password from db
                                usrHashed = rs.getString("password");
                            }

                            // Close statement
                            stmt.close();

                            // Check if the user is active and not marked as deleted
                            if(usr.getIsActive() && !usr.getIsDeleted()) {
                                // Check if hashed password match the one from db
                                if (BCrypt.checkpw(usrHashed, hashed)) {                                
                                    response.setContent(usr);
                                } else {
                                    response.addError("Username or password does not match");
                                }
                            } else {
                                // Otherwise, user is not available
                                response.addError("Cannot find user '" + usr.getUsername() + "'");
                            }
                        } catch (SQLException e) {
                            System.err.println(e);
                            response.addError("An error has occured while gathering user from database");
                        }
                    } else {
                        response.addError("Incorrect parameters");
                    }
                    break;
                case REGISTER:
                    // @TODO : handle register process
                    break;
                default:
                    System.err.println("ACTION '" + action + "' NOT SUPPORTED");
                    response.addError("Unsupported action '" + action + "'");
                    break;
            }

            // Send response to client
            this.sendObject(response);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e);
        }
    }

    private void initStreamHandlers() throws IOException {
        // Get output stream
        OutputStream output = this.sock.getOutputStream();
        // Get input stream
        InputStream input = this.sock.getInputStream();

        // Init writer and reader for Object communication
        this.objectWriter = new ObjectOutputStream(output);
        this.objectReader = new ObjectInputStream(input);
    }

    private PaSocketMessage readObject() throws IOException, ClassNotFoundException {
        // Declare variables
        PaSocketMessage response;
        Object msg;

        // Read object from stream
        msg = this.objectReader.readObject();

        // Cast response object
        response = (PaSocketMessage) msg;

        System.out.println("\t * " + this.nameTest + " Received message " + response.getAction());

        // Return response
        return response;
    }

    private void sendObject(PaSocketMessage pMsg) throws IOException {
        // Write object message ton send into object writer
        this.objectWriter.writeObject(pMsg);
        // Flush the object writer to send the message
        this.objectWriter.flush();

        System.out.println(this.nameTest + " : " + pMsg.getAction());
    }
}
