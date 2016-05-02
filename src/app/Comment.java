/***********************************************************************
 * Module:  Comment.java
 * Author:  Florian
 * Purpose: Defines the Class Comment
 ***********************************************************************/

import java.util.*;

/** @pdOid f9587a5c-7ca3-4509-a278-fff8206872f5 */
public class Comment {
   /** @pdOid cd7ba5c9-fa10-489a-9e7f-3e069f66f41c */
   private String comment;
   /** @pdOid 1335b83e-52a2-4285-9270-8c459cda76ad */
   private Date commentDate;
   
   /** @pdRoleInfo migr=no name=User assc=userComment mult=1..1 */
   public User userComment;
   /** @pdRoleInfo migr=no name=Equation assc=commentEquation mult=0..1 */
   public Equation commentEquation;

}