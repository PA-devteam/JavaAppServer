package sockets;

public class PaSocketMessageRegister extends PaSocketMessageLogin {

    protected String userFirstName;
    protected String userLastName;
    protected String userConfirmPassword;
    protected String userEmail;

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserConfirmPassword() {
        return userConfirmPassword;
    }

    public void setUserConfirmPassword(String userConfirmPassword) {
        this.userConfirmPassword = userConfirmPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public PaSocketMessageRegister() {
        super();
        this.action = PaSocketAction.REGISTER;
    }

    public PaSocketMessageRegister(String userFirstName, String userLastName, String userName, String userPassword, String userConfirmPassword, String userEmail) {
        super(userName, userPassword);
        this.action = PaSocketAction.REGISTER;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userConfirmPassword = userConfirmPassword;
        this.userEmail = userEmail;
    }
}
