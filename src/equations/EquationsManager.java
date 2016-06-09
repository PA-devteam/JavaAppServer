package equations;

import database.DbManager;
import interfaces.*;
import entities.*;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EquationsManager implements IInitialisable, ICRUDable {
    // List of all created equations - populated on application startup
    private static ArrayList<Equation> equationsList = new ArrayList<>();

    public static ArrayList<Equation> getEquationsList() {
        return equationsList;
    }

    @Override
    public  void init() {
        //@TODO : Implement SQL request here to gather all equations from DB and populate the 'equationsList' list
        String request = "Select * from PA.EQUATION";
         Statement stmts;
        try {
            stmts = DbManager.conn.createStatement();
             ResultSet recupKeys =stmts.executeQuery(request);
             if (recupKeys.isBeforeFirst()) {
            while (recupKeys.next()) {
                Equation a=new Equation();
                a.setId(recupKeys.getInt("id"));
                a.setIsValid(recupKeys.getBoolean("isvalid"));
                a.setLabel(recupKeys.getString("label"));
                a.setDescription(recupKeys.getString("description"));
                a.setValidationDate(recupKeys.getDate("validationdate"));
                int idUser=recupKeys.getInt("createdby");
                String request2 = "Select * from PA.USERS where id='"+idUser+"'";
                Statement stmt2 = DbManager.conn.createStatement();
                ResultSet recupUser =stmt2.executeQuery(request2);
                User valide=new User();
                while (recupUser.next()) {
                valide.setUsername(recupUser.getString("username"));
                valide.setId(recupUser.getInt("id"));
                }
                a.setValidatedBy(valide);
                a.setCreatedBy(valide);
                String request3 = "Select * from PA.ELEMENT el,PA.EQUATIONELEMENT lien where lien.id='"+recupKeys.getInt("id")+"' and lien.ele_id=lien.id";
                Statement stmt3 = DbManager.conn.createStatement();
                ResultSet recupElement =stmt3.executeQuery(request3);
                 ArrayList <Element> equationElement=new ArrayList<>();
                while (recupElement.next()) {
                   
                    Element e=new Element();
                    e.setId(recupElement.getInt("id"));
                    e.setLabel(recupElement.getString("label"));
                    e.setDescription(recupElement.getString("description"));
                    e.setVal(recupElement.getInt("val"));
                    
                    equationElement.add(e);
                
                }
                a.setEquationElement(equationElement);
                equationsList.add(a);
               
                
            }}
        } catch (SQLException ex) {
            Logger.getLogger(EquationsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
         
          
    }
    
    @Override
    public boolean create(Object o) {
        // Initialise an operation status indicator
        boolean opStatus = false;
        
        // Cast the received object to the awaited type
        Equation equationToCreate = (Equation) o;
        try {
        // @TODO : Implement SQL request here to create the equation in DB
         Statement stmt = DbManager.conn.createStatement();
         ResultSet rs ;
         PreparedStatement ps;
         String label=equationToCreate.getLabel();
         String description=equationToCreate.getDescription();
         boolean isvalid=true;
         int validatedby=1;
         int createdby=1;
       
        java.util.Date utilDate =new java.util.Date();

java.sql.Date dateToday = new java.sql.Date(utilDate.getTime());
          ps = DbManager.conn.prepareStatement("insert into PA.EQUATION(label,description,isvalid,validatedby,validationdate,createdby) values(?,?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, label);
            ps.setString(1, label);
            ps.setBoolean(3, isvalid);
            ps.setInt(4,validatedby);
            ps.setDate(5,dateToday);
            ps.setInt(6,createdby);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            int idequation=0 ;
            while (rs.next()) {
               idequation = rs.getInt(1);
            }
         //int idequation=equationToCreate.getId();
        for(int i=0;i<equationToCreate.getEquationElement().size();i++){
            int id=equationToCreate.getEquationElement().get(i).getId();
            
            String request=("insert into PA.EQUATIONELEMENT (id,ele_id) values('"+idequation+"','"+id+"'");
          stmt.executeQuery(request);
       
        
        }
         boolean sqlRequestStatus = true;
          if(sqlRequestStatus) {
            // Add the created equation to the list
            equationsList.add(equationToCreate);
            
            // Indicate that the whole operation has been done without errors            
            opStatus = true;
        }
        } catch (SQLException ex) {
            Logger.getLogger(EquationsManager.class.getName()).log(Level.SEVERE, null, ex);
        }
      
        
        // IF SQL request has succeeded
        
        
        // Return the operation status
        return opStatus;
    }

    @Override
    public Object read(int id) {
        return getEquationById(id);
    }

    @Override
    public boolean update(Object o) {
        // Initialise an operation status indicator
        boolean opStatus = false;
        
        // @TODO : Implement SQL request here to update the equation in DB
        boolean sqlRequestStatus = true;
        
        // IF SQL request has succeeded
        if(sqlRequestStatus) {
            // Cast the received object to the awaited type
            Equation updateEquation = (Equation) o;

            // Get existing equation
            Equation existingEquation = getEquationById(updateEquation.getId());

            // Check if there is an existing equation
            if(existingEquation != null) {
                // Get index of existing equation
                Statement stmt;
                try {
                    stmt = DbManager.conn.createStatement();
                      ResultSet rs ;
                String request=("delete from PA.EQUATIONELEMENT where id='"+updateEquation.getId()+"';");
          stmt.executeUpdate(request);
          System.out.println("NB ELEMENTS"+updateEquation.getEquationElement().size());
         
          
          for(int i=0;i<updateEquation.getEquationElement().size();i++){
            int id=updateEquation.getEquationElement().get(i).getId();
            
            request=("insert into PA.EQUATIONELEMENT values('"+updateEquation.getId()+"','"+id+"');");
            
          stmt.executeUpdate(request);
          }
           String label=updateEquation.getLabel();
         String description=updateEquation.getDescription();
         boolean isvalid=updateEquation.getIsValid();
         User validatedby=updateEquation.getValidatedBy();
         int valby=validatedby.getId();
         User createdby=updateEquation.getCreatedBy();
         int creatby=createdby.getId();
         Date creationdate=(Date) updateEquation.getValidationDate();
       
          int id=updateEquation.getId();
       request=("UPDATE PA.EQUATION set label='"+label+"',description='hi',isvalid='"+isvalid+"',validatedby='"+valby+"',createdby='"+creatby+"',validationdate='"+creationdate+"' where id='"+updateEquation.getId()+"'");
         

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
        
        // Return the operation status
        return opStatus;
    }

    @Override
    public boolean delete(int id) {
        // Initialise an operation status indicator
        boolean opStatus = false;
        
        // @TODO : Implement SQL request here to delete the equation in DB
        boolean sqlRequestStatus = true;
        
        // IF SQL request has succeeded
        if(sqlRequestStatus) {
            // Get existing equation
            Equation existingEquation = getEquationById(id);
            
            // Check if there is an existing equation
            if(existingEquation != null) {
                // Remove the equation from the list
                
                try {
                     Statement stmt=DbManager.conn.createStatement();;
                 String request=("delete from PA.EQAUATION where id='"+existingEquation.getId()+"'");
                    stmt.executeQuery(request);
//                    request=("delete from PA.EQAUATIONELEMENT where id='"+existingEquation.getId()+"'");
//                    stmt.executeQuery(request);
                } catch (SQLException ex) {
                    Logger.getLogger(EquationsManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                equationsList.remove(existingEquation);
            }
            
            // Indicate that the whole operation has been done without errors
            opStatus = true;            
        }
        
        // Return the operation status
        return opStatus;
    }
    
    private Equation getEquationById(int id) {
        // Initialise value to return
        Equation equationToReturn = null;
        
        // Loop over equations in the equations list
        for(Equation e : equationsList) {
            // Check if the given id match the current iterated equation id
            if(id == e.getId()) {
                // Set the current equation as returned value
                equationToReturn = e;
                // Break the loop
                break;
            }
        }
        
        // Return the result
        return equationToReturn;
    }
}
