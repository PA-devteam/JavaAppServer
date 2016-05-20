/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;

/**
 *
 * @author sebastien
 */
public class PaSocketResponseError {
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
}
