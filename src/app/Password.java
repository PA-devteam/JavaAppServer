package app;

/***********************************************************************
 * Module:  Password.java
 * Author:  Florian
 * Purpose: Defines the Class Password
 ***********************************************************************/

import java.util.*;

/** @pdOid dd0b5ceb-29da-48fc-b997-2d4c8af24d5a */
public class Password {
   /** @pdOid baf9dcaf-4b6b-43f6-b5a7-9d1710f8d90f */
   private int id;
   /** @pdOid 7cb37af0-6d26-403e-895f-1aa9558dcf56 */
   private String value;
   
   /** @pdRoleInfo migr=no name=Salt assc=passwordSalt mult=1..1 */
   public Salt passwordSalt;

}