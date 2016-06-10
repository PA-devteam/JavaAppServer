package sockets;

import database.DbManager;
import entities.Equation;
import entities.Role;
import java.io.*;
import java.net.Socket;
import java.sql.*;
import security.BCrypt; // http://www.mindrot.org/projects/jBCrypt/
import entities.User;
import equations.EquationsManager;
import errors.PaErrors;
import interfaces.IInitialisable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PaSocketClient extends Thread implements Runnable, IInitialisable {

    public Socket sock;
    public ObjectOutputStream objectWriter;
    private ObjectInputStream objectReader;
    private String nameTest;
    public static int loggedUserId = -1;
    private boolean isAuth = false;

    public PaSocketClient() {
        this.init();
    }

    public PaSocketClient(Socket pSocket) {
        this.init();
        this.sock = pSocket;
    }

    @Override
    public void init() {
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

                boolean sendBack = true;

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
                                        usr.setId(rs.getInt("id"));
                                        loggedUserId = rs.getInt("id");
                                        usr.setFirstname(rs.getString("firstname"));
                                        usr.setUsername(rs.getString("username"));
                                        usr.setLastname(rs.getString("lastname"));
                                        usr.setCreateDate(rs.getDate("createDate"));
                                        usr.setUpdate(rs.getDate("updatedate"));
                                        usr.setIsActive(rs.getBoolean("isActive"));
                                        usr.setIsDeleted(rs.getBoolean("isDeleted"));
                                        usr.setEmail(rs.getString("email"));
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
                                    // Close statement
                                    //stmt.close();

                                    usr.setUserRole(usrRole);

                                    response.setContent(usr);

                                    isAuth = true;
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
                    case LOGOUT:
                        isAuth = false;
                        sendBack = false;
                        break;
                    case LISTEQUATION:
                        response.setAction(PaSocketAction.LISTEQUATION);
                        ArrayList<Equation> ListEquations = EquationsManager.getEquationsList();
                        response.setContent(ListEquations);

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
                                                usr.setId(recupKey.getInt(1));
                                                loggedUserId = recupKey.getInt(1);
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

                                        //User usr = new User();
                                        usr.setFirstname(firstName);
                                        usr.setLastname(lastName);
                                        usr.setUsername(userName);
                                        isAuth = true;
                                        usr.setEmail(email);

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

                    //IN PROGRESS
                    case UPDATEEQUATION:
                        PaSocketMessageEquation updateEquation = (PaSocketMessageEquation) message;
                        Equation encours = updateEquation.getEquation();
//                        EquationsManager uneEquation = new EquationsManager();
//                        uneEquation.update(encours);
                        response.setAction(PaSocketAction.REFRESH);

                        break;

                    ////////
                    case UPDATEINFO:
                        // @TODO : optimise message by switching content from fields to a single 'User' field

                        response.setAction(PaSocketAction.REGISTER);

                        System.out.println("REGISTER");

                        // Case message to specific case message
                        register = (PaSocketMessageRegister) message;

                        firstName = register.getUserFirstName();
                        lastName = register.getUserLastName();
                        userName = register.getUserName();
                        email = register.getUserEmail();
                        pwd = register.getUserPassword();
                        confirmPwd = register.getUserConfirmPassword();
                        role = register.getRoles();

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

                                        request = "update PA.Users set firstname='" + firstName + "',lastname='" + lastName + "',username='" + userName + "',id_password='" + identifiant + "',email='" + email + "' where username='" + userName + "'";
//                                        //Insertion table User
//                                        ps = DbManager.conn.prepareStatement("insert into PA.Users (firstname,lastname,username,createdate,updatedate,isactive,isdeleted,id_password,email) values(?,?,?,?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
//                                        ps.setString(1, firstName);
//                                        ps.setString(2, lastName);
//                                        ps.setString(3, userName);
//                                        ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
//                                        ps.setDate(5, new java.sql.Date(System.currentTimeMillis()));
//                                        ps.setBoolean(6, true);
//                                        ps.setBoolean(7, false);
//                                        ps.setInt(8, identifiant);
//                                        ps.setString(9, email);
//                                        ps.executeUpdate();
                                        stmt = DbManager.conn.createStatement();
                                        stmt.executeUpdate(request);
                                        request = "select id from PA.USERS where username='" + userName + "'";
                                        recupKey = stmt.executeQuery(request);
                                        int identifiants = 0;
                                        if (recupKey.isBeforeFirst()) {

                                            while (recupKey.next()) {
                                                identifiants = recupKey.getInt("id");
                                            }
                                        } else {
                                            System.out.println("Probleme recupartion ID utilisateur");
                                        }
                                        ps.close();
                                        recupKey.close();
                                        int roleId = 0;
                                        request = "";
                                        request = "SELECT id FROM PA.ROLESTATUT WHERE label='" + role + "';";
                                        recupKey = this.executeRequete(request);
                                        if (recupKey.isBeforeFirst()) {
                                            while (recupKey.next()) {
                                                roleId = recupKey.getInt("id");
                                            }
                                        } else {
                                            System.out.println("Probleme recupartion ID role");
                                        }
                                        System.out.println("role:" + roleId);
                                        System.out.println("id user:" + identifiants);
                                        stmt.executeUpdate("update PA.USERROLE set Rol_ID='" + roleId + "' where id='" + identifiants + "'");
//                                        ps = DbManager.conn.prepareStatement("insert into PA.USERROLE values(?,?)");
//                                        ps.setInt(1, identifiant);
//                                        ps.setInt(2, roleId);
//                                        ps.executeUpdate();
//
//                                        ps.close();
                                        recupKey.close();

                                        //User usr = new User();
                                        usr.setFirstname(firstName);
                                        usr.setLastname(lastName);
                                        usr.setUsername(userName);
                                        isAuth = true;
                                        usr.setEmail(email);

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
                    ///////

                    case EQUATIONBYID:
                        System.out.println("RECEIVED CASE EQUATIONBYID");

                        PaSocketMessageEquation equationRecu = (PaSocketMessageEquation) message;

                        System.out.println("A");

                        Equation eq = equationRecu.getEquation();

                        System.out.println("B");

                        System.out.println(eq);

                        int eId = eq.getId();
                        System.out.println("ID RECEIVED : " + eId);

                        Equation renvoi = EquationsManager.read(equationRecu.getEquation().getId());

                        if (renvoi != null) {
                            response.setContent(renvoi);
                            response.setAction(PaSocketAction.EQUATIONBYID);
                        } else {
                            response.addError(PaErrors.EMPTY_PARAMETERS);
                        }

                        break;

                    case DELETEEQUATION:
                        equationRecu = (PaSocketMessageEquation) message;
                        eq = equationRecu.getEquation();
                        eId = eq.getId();
                        if (!EquationsManager.delete(eId)) {
                            response.addError(PaErrors.SQL_REQUEST_FAILED);
                        }
                        sendBack = false;

                        break;
                    case CREATEEQUATION:
                        equationRecu = (PaSocketMessageEquation) message;
                        eq = equationRecu.getEquation();
                        int id = EquationsManager.create(eq);
                        response.setAction(PaSocketAction.CREATEEQUATION);
                        response.setContent(id);
                        break;

                    default:
                        System.err.println("ACTION '" + action + "' NOT SUPPORTED");
                        response.addError(PaErrors.UNSUPPORTED_ACTION);
                        break;
                }

                if (sendBack) {
                    System.out.println("SENDING RESPONSE TO CLIENT");
                    System.out.println(response);

                    // Send response to client
                    this.sendObject(response);
                }
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

    public PaSocketMessage readObject() throws IOException, ClassNotFoundException {
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

    public void sendObject(PaSocketResponse pMsg) throws IOException {
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
