package app;

/***********************************************************************
 * Module:  Equation.java
 * Author:  Florian
 * Purpose: Defines the Class Equation
 ***********************************************************************/

import app.User;
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

}