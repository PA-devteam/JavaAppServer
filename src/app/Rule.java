/***********************************************************************
 * Module:  Rule.java
 * Author:  Florian
 * Purpose: Defines the Class Rule
 ***********************************************************************/

import java.util.*;

/** @pdOid 76dd4f7b-5dbe-4771-9f94-c51809d86b9d */
public class Rule {
   /** @pdOid 530ca3b5-cb18-4980-9eea-6fbbd4eff65c */
   private int id;
   /** @pdOid cc699891-42bb-4b62-be9a-da1caa4a45ef */
   private String label;
   
   /** @pdRoleInfo migr=no name=Ruleelement assc=rulesRuleselement mult=0..* type=Composition */
   public Ruleelement[] rulesRuleselement;

}