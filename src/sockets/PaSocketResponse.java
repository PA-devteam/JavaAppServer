package sockets;

import java.io.Serializable;
import java.util.ArrayList;

public class PaSocketResponse extends PaSocketMessage implements Serializable {

    private ArrayList errors;
    private Object content;

    /**
     * @return the errors
     */
    public ArrayList getErrors() {
        return errors;
    }

    /**
     * @param errors the errors to set
     */
    public void setErrors(ArrayList errors) {
        this.errors = errors;
    }

    public void addError(Object error) {
        this.errors.add(error);
    }

    /**
     * @return the content
     */
    public Object getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(Object content) {
        this.content = content;
    }

    public PaSocketResponse() {
        this.errors  = new ArrayList();
        this.content = null;
    }

    public PaSocketResponse(PaSocketAction pAction) {
        super(pAction);
        this.errors  = new ArrayList();
        this.content = null;
    }
}
