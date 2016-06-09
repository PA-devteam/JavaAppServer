/***********************************************************************
 * Module:  User.java
 * Author:  Florian
 * Purpose: Defines the Class User
 ***********************************************************************/
package entities;

import java.io.Serializable;
import java.util.*;

/** @pdOid 1a1bb685-ad54-42d1-af3b-e83a2ebc3a4a */
public class User implements Serializable {
   /** @pdOid 10f82ee1-e502-4aa1-9670-42b23dc1dac9 */
   private int id;
   /** @pdOid 22277a53-69c3-41e5-9821-8cb8e8230910 */
   private String firstname;
   /** @pdOid bef1c562-4054-4c45-a23e-7ef1581e2178 */
   private String lastname;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
   /** @pdOid 03aefccf-a7ab-4934-bc36-0eb363ad7dba */
   private String username;
   /** @pdOid ce747382-01fb-4cd2-b0f6-0a77fe1db2bb */
   private Date createDate;
   /** @pdOid b592ccb0-261c-4210-b6ff-2db095554dda */
   private Date update;
   /** @pdOid f541e781-94e7-4c84-97d2-26bb63493066 */
   private Boolean isActive;
   /** @pdOid 4b830cc4-a6b5-40f8-a6c5-9dcec616d97c */
   private Boolean isDeleted;
   
   /** @pdRoleInfo migr=no name=Password assc=userPassword mult=1..1 */
   public Password userPassword;
   /** @pdRoleInfo migr=no name=Role assc=userRole mult=0..* */
   public ArrayList<Role> userRole;
   
    private String email;

    public ArrayList<Role> getUserRole() {
        return userRole;
    }

    public void setUserRole(ArrayList<Role> userRole) {
        this.userRole = userRole;
    }

   
   /** @pdRoleInfo migr=no name=Equation assc=userEquation mult=0..* */
   public Equation[] userEquation;

    /**
     * @return the firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * @param firstname the firstname to set
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * @return the lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * @param lastname the lastname to set
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the createDate
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * @param createDate the createDate to set
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * @param isActive the isActive to set
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @return the isDeleted
     */
    public Boolean getIsDeleted() {
        return isDeleted;
    }

    /**
     * @param isDeleted the isDeleted to set
     */
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * @return the update
     */
    public Date getUpdate() {
        return update;
    }

    /**
     * @param update the update to set
     */
    public void setUpdate(Date update) {
        this.update = update;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }    

}