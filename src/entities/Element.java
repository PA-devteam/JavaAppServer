package entities;

/***********************************************************************
 * Module:  Element.java
 * Author:  Florian
 * Purpose: Defines the Class Element
 ***********************************************************************/

import java.io.Serializable;
import java.util.*;

/** @pdOid cc55d337-773e-4330-b19b-69793bdf908b */
public class Element implements Serializable{
   /** @pdOid 2ba5a823-a4b3-458b-bde2-196ec19cf1aa */
   private int id;
   /** @pdOid f35931ee-1228-4872-b5b2-f04046219494 */
   private String label;
   /** @pdOid b628be65-bf1f-436b-aacc-a9f69a275f82 */
   private String description;
private int val;

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

    public int getVal() {
        return val;
    }

    public void setVal(int val) {
        this.val = val;
    }
}