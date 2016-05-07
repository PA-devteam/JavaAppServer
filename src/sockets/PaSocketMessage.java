package sockets;

import java.io.Serializable;

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
