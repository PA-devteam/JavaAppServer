/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;

import entities.Equation;

/**
 *
 * @author Romain
 */
public class PaSocketMessageEquation extends PaSocketMessage {
    
    Equation equation;

    public Equation getEquation() {
        return equation;
    }

    public void setEquation(Equation equation) {
        this.equation = equation;
    }

    public PaSocketMessageEquation(Equation equation) {
       this.equation=equation;
    }
    
    public void setUpdateEquation(){
        this.action=PaSocketAction.UPDATEEQUATION;
    }
    
    public void setDeleteEquation(){
        this.action=PaSocketAction.DELETEEQUATION;
    }
    
    public void setCreateEquation(){
        this.action=PaSocketAction.CREATEEQUATION;
    }
    
   
}
