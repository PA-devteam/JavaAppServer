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
public class PaSocketMessage implements Serializable {

    private PaSocketAction action;


    /**
     * @return the action
     */
    public PaSocketAction getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(PaSocketAction action) {
        this.action = action;
    }

    public PaSocketMessage() {

    }

    public PaSocketMessage(PaSocketAction pAction) {
        this.action = pAction;

    }
}
