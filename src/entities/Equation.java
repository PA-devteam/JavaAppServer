package entities;

/***********************************************************************
 * Module:  Equation.java
 * Author:  Florian
 * Purpose: Defines the Class Equation
 ***********************************************************************/

import entities.User;
import java.util.*;

/** @pdOid 55edc2fe-2156-4a24-921b-f6d2edabc311 */
public class Equation {
   /** @pdOid d6984ecd-e51b-4910-bbc9-8958d902606e */
   private int id;
   /** @pdOid 2451366d-d6cb-4fbd-8791-1cf647e65a73 */
   private String label;
   /** @pdOid e09a3cfd-d839-460b-8558-60d68e8723d0 */
   private String description;
   /** @pdOid b150a21c-f86f-4c09-b0e8-c7c26886574d */
   private Boolean isValid;
   /** @pdOid 1ee83b5e-f2b2-4daa-afa1-c613e01c9ed3 */
   private User validatedBy;
   /** @pdOid 234308dd-30a6-4a46-926f-006a1029e697 */
   private Date validationDate;
   
   /** @pdRoleInfo migr=no name=Rule assc=equationRules mult=0..* */
   public Rule[] equationRules;
   /** @pdRoleInfo migr=no name=Element assc=equationElement mult=1..* type=Composition */
   public Element[] equationElement;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public User getValidatedBy() {
        return validatedBy;
    }

    public void setValidatedBy(User validatedBy) {
        this.validatedBy = validatedBy;
    }

    public Date getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(Date validationDate) {
        this.validationDate = validationDate;
    }

    public Rule[] getEquationRules() {
        return equationRules;
    }

    public void setEquationRules(Rule[] equationRules) {
        this.equationRules = equationRules;
    }

    public Element[] getEquationElement() {
        return equationElement;
    }

    public void setEquationElement(Element[] equationElement) {
        this.equationElement = equationElement;
    }

   
   
}