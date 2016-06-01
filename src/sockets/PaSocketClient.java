package sockets;

import database.DbManager;
import entities.Role;
import java.io.*;
import java.net.Socket;
import java.sql.*;
import security.BCrypt; // http://www.mindrot.org/projects/jBCrypt/
import entities.User;
import errors.PaErrors;
import java.util.ArrayList;
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

            while (true) {

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
                                String request = "SELECT * FROM PA.USERS u,PA.PASSWORD p WHERE u.ID_PASSWORD=p.ID and u.username='" + username + "';";
                                ResultSet rs = this.executeRequete(request);
                                User usr = new User();
                                String usrHashed = "";
                                if (rs.isBeforeFirst()) {
                                    while (rs.next()) {
                                        // Build new User entity
                                        usr.setFirstname(rs.getString("firstname"));
                                        usr.setUsername(rs.getString("username"));
                                        usr.setLastname(rs.getString("lastname"));
                                        usr.setCreateDate(rs.getDate("createDate"));
                                        usr.setUpdate(rs.getDate("updatedate"));
                                        usr.setIsActive(rs.getBoolean("isActive"));
                                        usr.setIsDeleted(rs.getBoolean("isDeleted"));
                                        System.out.println(rs.getString("username"));

                                        // Get hashed password from db
                                        usrHashed = rs.getString("val");
                                    }
                                    rs.close();
                                    rs = this.executeRequete("select r.id,r.label FROM PA.USERS u, PA.USERROLE ur, PA.ROLESTATUT r  WHERE u.username='" + username + "'and u.id=ur.id and ur.rol_id=r.id ");
                                        ArrayList<Role> usrRole = new ArrayList<>();
                                        if (rs.isBeforeFirst()) {
                                            while (rs.next()) {
                                                Role temp = new Role();
                                                temp.setId(rs.getInt("id"));
                                                temp.setLabel(rs.getString("label"));
                                                usrRole.add(temp);

                                            }
                                            
                                        } else {
                                            System.out.println("Probleme recupartion LISTE role");
                                        }

                                        usr.setUserRole(usrRole);

                                    // Check if the user is active and not marked as deleted
                                    // if (usr.getIsActive() && !usr.getIsDeleted()) {
                                    // Check if hashed password match the one from db
                                   // if (BCrypt.checkpw(usrHashed, hashed)) {
                                        response.setContent(usr);
                                        System.out.println(usr.getUsername());
                                        System.out.println(usr.getIsDeleted());
                                        System.out.println("USER OK");
                                        System.out.println(response);
                                    //} else {
                                    //    response.addError(PaErrors.INCORRECT_PARAMETERS);
                                  //  }

                                    // } //else {
                                    //                                            response.addError(PaErrors.LOGIN_MISMATCH);
                                    //                                        }
                                    //                                    } else {
                                    //                                        // Otherwise, user is not available
                                    //                                        response.addError(PaErrors.LOGIN_MISMATCH);
                                    //}
                                    //    }
//                                else {
//                                    // Otherwise, user is not available
//                                    response.addError(PaErrors.LOGIN_MISMATCH);
//                                }
                                } else {
                                    response.addError(PaErrors.INCORRECT_PARAMETERS);
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

                        String firstName = register.getUserFirstName();
                        String lastName = register.getUserLastName();
                        String userName = register.getUserName();
                        String email = register.getUserEmail();
                        String pwd = register.getUserPassword();
                        String confirmPwd = register.getUserConfirmPassword();
                        String role = register.getRoles();

                        if (pwd != null && confirmPwd != null && pwd.length() > 0 && confirmPwd.length() > 0) {
                            String hashedPwd = BCrypt.hashpw(pwd, BCrypt.gensalt());
                            String hashedConfirmPwd = BCrypt.hashpw(confirmPwd, BCrypt.gensalt());

                            if (pwd.equals(confirmPwd)) {
                                // @TODO : Check if a user already exist with provided userName AND/OR email
                                if (firstName.length() > 0 && lastName.length() > 0 && userName.length() > 0 && email.length() > 0) {

                                    //Temp
                                    User usr = new User();
                                    try {
                                        PreparedStatement ps;
                                        ResultSet recupKey;
                                        int identifiant = 0;

                                        //Insertion table Passwword avec recuperation de l'id
                                        ps = DbManager.conn.prepareStatement("insert into PA.Password (val) values(?)", PreparedStatement.RETURN_GENERATED_KEYS);
                                        ps.setString(1, hashedPwd);
                                        ps.executeUpdate();
                                        recupKey = ps.getGeneratedKeys();
                                        while (recupKey.next()) {
                                            identifiant = recupKey.getInt(1);
                                        }
                                        ps.close();
                                        recupKey.close();

                                        //Insertion table User
                                        ps = DbManager.conn.prepareStatement("insert into PA.Users (firstname,lastname,username,createdate,updatedate,isactive,isdeleted,id_password,email) values(?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
                                        ps.setString(1, firstName);
                                        ps.setString(2, lastName);
                                        ps.setString(3, userName);
                                        ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
                                        ps.setDate(5, new java.sql.Date(System.currentTimeMillis()));
                                        ps.setBoolean(6, true);
                                        ps.setBoolean(7, false);
                                        ps.setInt(8, identifiant);
                                        ps.setString(9, email);
                                        ps.executeUpdate();
                                        recupKey = ps.getGeneratedKeys();
                                        identifiant = 0;
                                        if (recupKey.isBeforeFirst()) {

                                            while (recupKey.next()) {
                                                identifiant = recupKey.getInt(1);
                                            }
                                        } else {
                                            System.out.println("Probleme recupartion ID utilisateur");
                                        }
                                        ps.close();
                                        recupKey.close();
                                        int roleId = 0;
                                        String request = "SELECT id FROM PA.ROLESTATUT WHERE label='" + role + "';";
                                        recupKey = this.executeRequete(request);
                                        if (recupKey.isBeforeFirst()) {
                                            while (recupKey.next()) {
                                                roleId = recupKey.getInt("id");
                                            }
                                        } else {
                                            System.out.println("Probleme recupartion ID role");
                                        }
                                        ps = DbManager.conn.prepareStatement("insert into PA.USERROLE values(?,?)");
                                        ps.setInt(1, identifiant);
                                        ps.setInt(2, roleId);
                                        ps.executeUpdate();
                                        ps.close();
                                        recupKey.close();

                                        //recuperation Liste des rôles
                                        usr.setFirstname(firstName);
                                        usr.setLastname(lastName);
                                        usr.setUsername(userName);
                                        recupKey = this.executeRequete("select r.id,r.label FROM PA.USERS u, PA.USERROLE ur, PA.ROLESTATUT r  WHERE u.username='" + userName + "'and u.id=ur.id and ur.rol_id=r.id ");
                                        ArrayList<Role> usrRole = new ArrayList<>();
                                        if (recupKey.isBeforeFirst()) {
                                            while (recupKey.next()) {
                                                Role temp = new Role();
                                                temp.setId(recupKey.getInt("id"));
                                                temp.setLabel(recupKey.getString("label"));
                                                usrRole.add(temp);

                                            }
                                        } else {
                                            System.out.println("Probleme recupartion LISTE role");
                                        }

                                        usr.setUserRole(usrRole);
                                    } catch (SQLException ex) {
                                        Logger.getLogger(PaSocketClient.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    //////

                                    // User usr = inserUser(firstName, lastName, pwd, userName, email, role);
                                    response.setContent(usr);
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
                    case INIT:
                        String request = "SELECT * FROM PA.ROLESTATUT ;";
                        Statement stmt;
                        try {
                            stmt = DbManager.conn.createStatement();
                            ResultSet rs = stmt.executeQuery(request);
                            ArrayList<String> roles = new ArrayList<>();
                            while (rs.next()) {
                                roles.add(rs.getString("label"));
                            }
                            response.setContent(roles);
                            response.setAction(PaSocketAction.INIT);
                        } catch (SQLException ex) {
                            Logger.getLogger(PaSocketClient.class.getName()).log(Level.SEVERE, null, ex);
                            response.addError(PaErrors.SQL_REQUEST_FAILED);
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

    public ResultSet executeRequete(String request) throws SQLException {
        Statement stmt = DbManager.conn.createStatement();
        ResultSet rs = stmt.executeQuery(request);
        return rs;
    }

    public User inserUser(String firstName, String lastName, String password, String userName, String email, String role) {
        User usr = new User();
        try {
            PreparedStatement ps;
            ResultSet recupKey;
            int identifiant = 0;

            //Insertion table Passwword avec recuperation de l'id
            ps = DbManager.conn.prepareStatement("insert into PA.Password (val) values(?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, password);
            ps.executeUpdate();
            recupKey = ps.getGeneratedKeys();
            while (recupKey.next()) {
                identifiant = recupKey.getInt(1);
            }
            ps.close();
            recupKey.close();

            //Insertion table User
            ps = DbManager.conn.prepareStatement("insert into PA.Users (firstname,lastname,username,createdate,updatedate,isactive,isdeleted,id_password,email) values(?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, userName);
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ps.setDate(5, new java.sql.Date(System.currentTimeMillis()));
            ps.setBoolean(6, true);
            ps.setBoolean(7, false);
            ps.setInt(8, identifiant);
            ps.setString(9, email);
            ps.executeUpdate();
            recupKey = ps.getGeneratedKeys();
            identifiant = 0;
            while (recupKey.next()) {
                identifiant = recupKey.getInt(1);
            }
            ps.close();
            recupKey.close();
            int roleId = 0;
            String request = "SELECT id FROM PA.ROLESTATUT WHERE label='" + role + "';";
            recupKey = this.executeRequete(request);
            while (recupKey.next()) {
                roleId = recupKey.getInt("id");
            }
            ps = DbManager.conn.prepareStatement("insert into PA.USERROLE values(?,?)");
            ps.setInt(1, identifiant);
            ps.setInt(2, roleId);
            ps.executeUpdate();
            ps.close();
            recupKey.close();

            //recuperation Liste des rôles
            usr.setFirstname(firstName);
            usr.setLastname(lastName);
            usr.setUsername(userName);
            recupKey = this.executeRequete("select r.id,r.label FROM PA.USERS u, PA.USERROLE ur, PA.ROLESTATUT r  WHERE u.username='" + userName + "'and u.id=ur.id and ur.rol_id=r.id ");
            ArrayList<Role> usrRole = new ArrayList<>();
            //  if (recupKey.isBeforeFirst() ) {
            while (recupKey.next()) {
                Role temp = new Role();
                temp.setId(recupKey.getInt("id"));
                temp.setLabel(recupKey.getString("label"));
                usrRole.add(temp);

            }

            // }
            usr.setUserRole(usrRole);
        } catch (SQLException ex) {
            Logger.getLogger(PaSocketClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return usr;
    }

}
