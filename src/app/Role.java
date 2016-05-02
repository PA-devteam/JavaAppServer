/***********************************************************************
 * Module:  Role.java
 * Author:  Florian
 * Purpose: Defines the Class Role
 ***********************************************************************/

import java.util.*;

/** @pdOid 611004f9-f257-4c30-87fb-07493384c0a0 */
public class Role {
   /** @pdOid 68f42dc0-a2a0-427a-92a1-49d392344a56 */
   private int id;
   /** @pdOid 28a19393-ba39-4a30-bc9d-5cc008d8cff3 */
   private String label;
   
   /** @pdRoleInfo migr=no name=Authorisation assc=authorisationRole mult=0..* */
   public Authorisation[] authorisationRole;

}