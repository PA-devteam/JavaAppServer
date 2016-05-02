/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sockets;

/**
 *
 * @author florian
 */
public class PaSocketMessageLogin extends PaSocketMessage {

    private String userName;
    private String userPassword;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public PaSocketMessageLogin() {
       super(PaSocketAction.LOGIN);
    }

    public PaSocketMessageLogin(String userName,String userPassword) {
        super(PaSocketAction.LOGIN);
        this.userName=userName;
        this.userPassword=userPassword;
    }
    

}
