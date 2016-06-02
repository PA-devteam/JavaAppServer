package equations;

import interfaces.*;
import entities.*;
import java.util.ArrayList;

public class EquationsManager implements IInitialisable, ICRUDable {
    // List of all created equations - populated on application startup
    private static ArrayList<Equation> equationsList = new ArrayList<>();

    public static ArrayList<Equation> getEquationsList() {
        return equationsList;
    }

    @Override
    public void init() {
        //@TODO : Implement SQL request here to gather all equations from DB and populate the 'equationsList' list
    }
    
    @Override
    public boolean create(Object o) {
        // Initialise an operation status indicator
        boolean opStatus = false;
        
        // Cast the received object to the awaited type
        Equation equationToCreate = (Equation) o;
        
        // @TODO : Implement SQL request here to create the equation in DB
        boolean sqlRequestStatus = true;
        
        // IF SQL request has succeeded
        if(sqlRequestStatus) {
            // Add the created equation to the list
            equationsList.add(equationToCreate);
            
            // Indicate that the whole operation has been done without errors            
            opStatus = true;
        }
        
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
