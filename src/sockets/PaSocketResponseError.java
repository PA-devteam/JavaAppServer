/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;

import java.io.Serializable;

/**
 *
 * @author sebastien
 */
public class PaSocketResponseError implements Serializable {
    private int code;
    private String message;
    private PaSocketResponseErrorCriticity criticity;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PaSocketResponseErrorCriticity getCriticity() {
        return criticity;
    }

    public void setCriticity(PaSocketResponseErrorCriticity criticity) {
        this.criticity = criticity;
    }
    
    public PaSocketResponseError() {
        
    }
    
    public PaSocketResponseError(int code, String message, PaSocketResponseErrorCriticity criticity) {
        this.code      = code;
        this.message   = message;
        this.criticity = criticity;
    }
    
    @Override
    public String toString() {
        return this.getCode() + " | " + this.getCriticity() + " | " + this.getMessage();
    }    
}
