package equations;

import database.DbManager;
import interfaces.*;
import entities.*;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import sockets.PaSocketClient;
import sockets.PaSocketServer;

public class EquationsManager extends Thread {

    // List of all created equations - populated on application startup
    private static ArrayList<Equation> equationsList = new ArrayList<>();

    public static ArrayList<Equation> getEquationsList() {
        equationsList = new ArrayList<>();
        init();
        return equationsList;
    }

    public static void init() {
        //@TODO : Implement SQL request here to gather all equations from DB and populate the 'equationsList' list
        String request = "Select * from PA.EQUATION";
        Statement stmts;
        try {
            stmts = DbManager.conn.createStatement();
            ResultSet recupKeys = stmts.executeQuery(request);
            if (recupKeys.isBeforeFirst()) {
                while (recupKeys.next()) {
                    Equation a = new Equation();
                    a.setId(recupKeys.getInt("id"));
                    a.setIsValid(recupKeys.getBoolean("isvalid"));
                    a.setLabel(recupKeys.getString("label"));
                    a.setDescription(recupKeys.getString("description"));
                    a.setValidationDate(recupKeys.getDate("validationdate"));
                    int idUser = recupKeys.getInt("createdby");
                    String request2 = "Select * from PA.USERS where id='" + idUser + "'";
                    Statement stmt2 = DbManager.conn.createStatement();
                    ResultSet recupUser = stmt2.executeQuery(request2);
                    User valide = new User();
                    while (recupUser.next()) {
                        valide.setUsername(recupUser.getString("username"));
                        valide.setId(recupUser.getInt("id"));
                    }
                    a.setValidatedBy(valide);
                    a.setCreatedBy(valide);
                    String request3 = "Select * from PA.ELEMENT el,PA.EQUATIONELEMENT lien where lien.id='" + recupKeys.getInt("id") + "' and lien.ele_id=lien.id";
                    Statement stmt3 = DbManager.conn.createStatement();
                    ResultSet recupElement = stmt3.executeQuery(request3);
                    ArrayList<Element> equationElement = new ArrayList<>();
                    while (recupElement.next()) {

                        Element e = new Element();
                        e.setId(recupElement.getInt("id"));
                        e.setLabel(recupElement.getString("label"));
                        e.setDescription(recupElement.getString("description"));
                        e.setVal(recupElement.getInt("val"));

                        equationElement.add(e);

                    }
                    a.setEquationElement(equationElement);
                    equationsList.add(a);

                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(EquationsManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static int create(Object o) {
        // Initialise an operation status indicator
        int opStatus = -1;

        // Cast the received object to the awaited type
        Equation equationToCreate = (Equation) o;
        try {
            // @TODO : Implement SQL request here to create the equation in DB
            Statement stmt = DbManager.conn.createStatement();
            ResultSet rs;
            PreparedStatement ps;
            String label = equationToCreate.getLabel();
            String description = equationToCreate.getDescription();
            boolean isvalid = true;
            System.out.println(PaSocketClient.loggedUserId);
            int validatedby = PaSocketClient.loggedUserId;
            int createdby = PaSocketClient.loggedUserId;

            java.util.Date utilDate = new java.util.Date();

            java.sql.Date dateToday = new java.sql.Date(utilDate.getTime());
            ps = DbManager.conn.prepareStatement("insert into PA.EQUATION(label,description,isvalid,validatedby,validationdate,createdby) values(?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, label);
            ps.setString(2, description);
            ps.setBoolean(3, isvalid);
            ps.setInt(4, validatedby);
            ps.setDate(5, dateToday);
            ps.setInt(6, createdby);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            int idequation = 0;
            while (rs.next()) {
                idequation = rs.getInt(1);
            }
            opStatus = idequation;
            //int idequation=equationToCreate.getId();
            if (equationToCreate.getEquationElement() != null && equationToCreate.getEquationElement().size() > 0) {
                for (int i = 0; i < equationToCreate.getEquationElement().size(); i++) {
                    int id = equationToCreate.getEquationElement().get(i).getId();

                    String request = ("insert into PA.EQUATIONELEMENT (id,ele_id) values('" + idequation + "','" + id + "'");
                    stmt.executeQuery(request);

                }
            }
            boolean sqlRequestStatus = true;
            if (sqlRequestStatus) {
                // Add the created equation to the list
                equationToCreate.setId(opStatus);
                equationsList.add(equationToCreate);

                // Indicate that the whole operation has been done without errors            
            }
        } catch (SQLException ex) {
            Logger.getLogger(EquationsManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        PaSocketServer.broadcast();
        
        // IF SQL request has succeeded
        // Return the operation status
        return opStatus;
    }

    public static Equation read(int id) {
        System.out.println("READING" + id);

        return getEquationById(id);
    }

    public static boolean update(Object o) {
        // Initialise an operation status indicator
        boolean opStatus = false;

        // @TODO : Implement SQL request here to update the equation in DB
        boolean sqlRequestStatus = true;

        // IF SQL request has succeeded
        if (sqlRequestStatus) {
            // Cast the received object to the awaited type
            Equation updateEquation = (Equation) o;

            // Get existing equation
            Equation existingEquation = getEquationById(updateEquation.getId());

            // Check if there is an existing equation
            if (existingEquation != null) {
                // Get index of existing equation
                Statement stmt;
                try {
                    stmt = DbManager.conn.createStatement();
                    ResultSet rs;
                    String request = ("delete from PA.EQUATIONELEMENT where id='" + updateEquation.getId() + "';");
                    stmt.executeUpdate(request);
                    System.out.println("NB ELEMENTS" + updateEquation.getEquationElement().size());

                    for (int i = 0; i < updateEquation.getEquationElement().size(); i++) {
                        int id = updateEquation.getEquationElement().get(i).getId();

                        request = ("insert into PA.EQUATIONELEMENT values('" + updateEquation.getId() + "','" + id + "');");

                        stmt.executeUpdate(request);
                    }
                    String label = updateEquation.getLabel();
                    String description = updateEquation.getDescription();
                    boolean isvalid = updateEquation.getIsValid();
                    User validatedby = updateEquation.getValidatedBy();
                    int valby = validatedby.getId();
                    User createdby = updateEquation.getCreatedBy();
                    int creatby = createdby.getId();
                    Date creationdate = (Date) updateEquation.getValidationDate();

                    int id = updateEquation.getId();
                    request = ("UPDATE PA.EQUATION set label='" + label + "',description='hi',isvalid='" + isvalid + "',validatedby='" + valby + "',createdby='" + creatby + "',validationdate='" + creationdate + "' where id='" + updateEquation.getId() + "'");

                    stmt.executeUpdate(request);

                } catch (SQLException ex) {
                    Logger.getLogger(EquationsManager.class.getName()).log(Level.SEVERE, null, ex);
                }

                int idxOf = equationsList.indexOf(existingEquation);

                // Replace the existing equation by the received one
                equationsList.set(idxOf, updateEquation);

                // Indicate that the whole operation has been done without errors
                opStatus = true;
            }
        }
        
        PaSocketServer.broadcast();

        // Return the operation status
        return opStatus;
    }

    public static boolean delete(int id) {
        // Initialise an operation status indicator
        boolean opStatus = false;

        // @TODO : Implement SQL request here to delete the equation in DB
        boolean sqlRequestStatus = true;

        // IF SQL request has succeeded
        if (sqlRequestStatus) {
            // Get existing equation
            Equation existingEquation = getEquationById(id);

            // Check if there is an existing equation
            if (existingEquation != null) {
                // Remove the equation from the list
                try {
                    Statement stmt = DbManager.conn.createStatement();
                    String request = ("delete  from PA.EQUATIONELEMENT where id='" + existingEquation.getId() + "'");
                    stmt.executeUpdate(request);
                    request = ("delete  from PA.EQUATION where id='" + existingEquation.getId() + "'");
                    stmt.executeUpdate(request);
//                    request=("delete from PA.EQAUATIONELEMENT where id='"+existingEquation.getId()+"'");
//                    stmt.executeQuery(request);
                } catch (SQLException ex) {
                    Logger.getLogger(EquationsManager.class.getName()).log(Level.SEVERE, null, ex);
                }

                equationsList.remove(getEquationIndexById(existingEquation.getId()));
            }

            // Indicate that the whole operation has been done without errors
            opStatus = true;
        }

        PaSocketServer.broadcast();
        
        // Return the operation status
        return opStatus;
    }

    private static Equation getEquationById(int id) {
        // Initialise value to return
        Equation equationToReturn = null;

        // Loop over equations in the equations list
        for (Equation e : equationsList) {
            // Check if the given id match the current iterated equation id
            if (id == e.getId()) {
                // Set the current equation as returned value
                equationToReturn = e;
                // Break the loop
                break;
            }
        }

        System.out.println("getEquationById" + equationToReturn.getId());

        // Return the result
        return equationToReturn;
    }

    private static int getEquationIndexById(int id) {
        // Initialise value to return
        int equationToReturn = -1;
        int compteur = 0;
        // Loop over equations in the equations list
        for (Equation e : equationsList) {

            // Check if the given id match the current iterated equation id
            if (id == e.getId()) {

                // Set the current equation as returned value
                equationToReturn = compteur;
                // Break the loop
                break;
            }
            compteur++;
        }

        // Return the result
        return equationToReturn;
    }
}
