package Model;

import java.io.Serializable;

public class Message implements Serializable {
    private String senderId;
    private String senderName;
    private String content;
    private String response;
    private boolean isResolved;

    public Message(String senderId, String senderName, String content) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.content = content;
        this.isResolved = false;
        this.response = "";
    }

    // Getters Setters
    public String getSenderName() { return senderName; }
    public String getContent() { return content; }
    public String getResponse() { return response; }
    public boolean isResolved() { return isResolved; }
    
    public void respond(String response) {
        this.response = response;
        this.isResolved = true;
    }
}