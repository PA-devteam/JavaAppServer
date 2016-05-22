package sockets;

import database.DbManager;
import java.io.*;
import java.net.Socket;
import java.sql.*;
import security.BCrypt; // http://www.mindrot.org/projects/jBCrypt/
import entities.User;
import errors.PaErrors;
import java.util.logging.Level;
import java.util.logging.Logger;

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

            while(true) {

                // Waiting message from client
                PaSocketMessage message = (PaSocketMessage) this.readObject();

                // Get action from client message
                PaSocketAction action = message.getAction();

                // Initialise new socket reponse
                PaSocketResponse response = new PaSocketResponse();

                // Handle action
                switch (action) {
                    case LOGIN:
                        response.setAction(PaSocketAction.LOGIN);

                        // Case message to specific case message
                        PaSocketMessageLogin login = (PaSocketMessageLogin) message;

                        // Get username from message
                        String username = login.getUserName();
                        // Get password from message
                        String password = login.getUserPassword();

                        // Check message parameters
                        if (username.length() > 0 && password.length() > 0) {
                            // Hash received password
                            String hashed = BCrypt.hashpw(password, BCrypt.gensalt());

                            try {
                                // Create SQL request
                                String request = "SELECT u.firstname,u.lastname,u.username,u.createDate,u.updatedate,u.isActive,u.isDeleted,p.val FROM PA.USERS u,PA.PASSWORD p WHERE u.username='" + username + "' and u.ID_PASSWORD=p.ID";

                                // Initialise SQL statement
                                Statement stmt = DbManager.conn.createStatement();

                                // Execute SQL query from SQL statement
                                ResultSet rs = stmt.executeQuery(request);

                                User usr = new User();
                                String usrHashed = "";

                                if (rs.next()) {
                                    // Process data from SQL result set
                                    while (rs.next()) {
                                        // Build new User entity
                                        usr.setUsername(rs.getString("u.firstname"));
                                        usr.setUsername(rs.getString("u.lastname"));
                                        usr.setCreateDate(rs.getDate("u.createDate"));
                                        usr.setUpdate(rs.getDate("u.updatedate"));
                                        usr.setIsActive(rs.getBoolean("u.isActive"));
                                        usr.setIsDeleted(rs.getBoolean("u.isDeleted"));

                                        // Get hashed password from db
                                        usrHashed = rs.getString("p.val");
                                    }

                                    // Close statement
                                    stmt.close();

                                    // Check if the user is active and not marked as deleted
                                    if (usr.getIsActive() && !usr.getIsDeleted()) {
                                        // Check if hashed password match the one from db
                                        if (BCrypt.checkpw(usrHashed, hashed)) {
                                            response.setContent(usr);
                                            System.out.println("USER OK");
                                            System.out.println(response);
                                        } else {
                                            response.addError(PaErrors.LOGIN_MISMATCH);
                                        }
                                    } else {
                                        // Otherwise, user is not available
                                        response.addError(PaErrors.LOGIN_MISMATCH);
                                    }
                                } else {
                                    // Otherwise, user is not available
                                    response.addError(PaErrors.LOGIN_MISMATCH);
                                }
                            } catch (SQLException e) {
                                System.err.println(e);
                                response.addError(PaErrors.SQL_REQUEST_FAILED);
                            }
                        } else {
                            response.addError(PaErrors.INCORRECT_PARAMETERS);
                        }
                        break;
                    case REGISTER:
                        // @TODO : optimise message by switching content from fields to a single 'User' field

                        response.setAction(PaSocketAction.REGISTER);

                        System.out.println("REGISTER");

                        // Case message to specific case message
                        PaSocketMessageRegister register = (PaSocketMessageRegister) message;
                        
                        String firstName  = register.getUserFirstName();
                        String lastName   = register.getUserLastName();
                        String userName   = register.getUserName();
                        String email      = register.getUserEmail();
                        String pwd        = register.getUserPassword();
                        String confirmPwd = register.getUserConfirmPassword();
                        int idpassword = 0;
                        
                        if (pwd != null && confirmPwd != null && pwd.length() > 0 && confirmPwd.length() > 0) {
                            if (pwd.equals(confirmPwd)) {
                                // @TODO : Check if a user already exist with provided userName AND/OR email

                                if (firstName.length() > 0 && lastName.length() > 0 && userName.length() > 0 && email.length() > 0) {
                                    try {
                                        PreparedStatement ps = DbManager.conn.prepareStatement("insert into PA.Password (val) values(?)", PreparedStatement.RETURN_GENERATED_KEYS);
                                        ps.setString(1, pwd);
                                     
                                        ps.executeUpdate();
                                        String request = "Select id from PA.PASSWORD where val='" + pwd + "';";
                                        Statement stmt = DbManager.conn.createStatement();
                                        ResultSet rs = stmt.executeQuery(request);
                                        if (rs.next()) {
                                            idpassword = rs.getInt("id");
                                        }

                                        ps = DbManager.conn.prepareStatement("insert into PA.Users (firstname,lastname,username,createdate,updatedate,isactive,isdeleted,id_password) values(?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
                                        ps.setString(1, "Romain");
                                        ps.setString(2, "S");
                                        ps.setString(3, "RS");
                                        ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
                                        ps.setDate(5, new java.sql.Date(System.currentTimeMillis()));
                                        ps.setBoolean(6, true);
                                        ps.setBoolean(7, false);
                                        ps.setInt(8, idpassword);
                                        ps.executeUpdate();
                                        request="SELECT * FROM PA.USERS;";
                                        String name="";
                                        rs=stmt.executeQuery(request);
                                         if (rs.next()) {
                                          name = rs.getString("firstname");
                                          System.out.println(name);
                                        }
                                        request="script drop TO 'src/database/bdd.sql' schema pa;";
                                        stmt.executeQuery(request);

                                        User usr = new User();
                                        usr.setFirstname(firstName);
                                        usr.setLastname(lastName);
                                        usr.setUsername(userName);
                                        
                                        response.setContent(usr);
                                    } catch (SQLException ex) {
                                        Logger.getLogger(PaSocketClient.class.getName()).log(Level.SEVERE, null, ex);
                                        response.addError(PaErrors.SQL_REQUEST_FAILED);
                                    }
                                } else {
                                    response.addError(PaErrors.EMPTY_PARAMETERS);
                                }
                            } else {
                                response.addError(PaErrors.PASSWORD_CONFIRM_MISMATCH);
                            }
                        } else {
                            response.addError(PaErrors.INCORRECT_PARAMETERS);
                        }
                        break;
                    default:
                        System.err.println("ACTION '" + action + "' NOT SUPPORTED");
                        response.addError(PaErrors.UNSUPPORTED_ACTION);
                        break;
                }

                System.out.println("SENDING RESPONSE TO CLIENT");
                System.out.println(response);
                
                // Send response to client
                this.sendObject(response);
            }
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

    private void sendObject(PaSocketResponse pMsg) throws IOException {
        // Write object message ton send into object writer
        this.objectWriter.writeObject(pMsg);
        // Flush the object writer to send the message
        this.objectWriter.flush();
    }
}
