package br.eb.ime.jwar.webapi;

public class ChatObject {

    private String user;
    private String message;
    private String type;

    public ChatObject() {
    }

    public ChatObject(String user, String message) {
        super();
        this.user = user;
        this.message = message;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
