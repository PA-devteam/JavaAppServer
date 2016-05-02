/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.io.*;
import java.util.Properties;

/**
 *
 * @author sebastien
 */
public class ConfigManager {

    private Properties prop;

    /**
     * @return the prop
     */
    public Properties getProp() {
        return prop;
    }

    /**
     * @param prop the prop to set
     */
    public void setProp(Properties prop) {
        this.prop = prop;
    }

    /**
     * Constructeur privé
     */
    private ConfigManager() {
        this.prop = new Properties();
    }

    /**
     * Instance unique non préinitialisée
     */
    private static ConfigManager INSTANCE = null;

    /**
     * Point d'accès pour l'instance unique du singleton
     *
     * @return
     */
    public static ConfigManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConfigManager();
        }
        return INSTANCE;
    }

    public void load(String pPropFile) {
        try (InputStream input = new FileInputStream(pPropFile)) {
            // Load properties file
            prop.load(input);

            // Close input stream
            input.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public String getProperty(String pProp) {
        return prop.getProperty(pProp);
    }
}
