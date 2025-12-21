package Model;

import java.io.Serializable;

public class Message implements Serializable {
    private String senderId;
    private String senderName;
    private String recipientId; // Link to BusinessOwner ID
    private String content;
    private String response;
    private boolean isResolved;

    public Message(String senderId, String senderName, String recipientId, String content) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.recipientId = recipientId;
        this.content = content;
        this.isResolved = false;
        this.response = "";
    }

    public String getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public String getRecipientId() { return recipientId; }
    public String getContent() { return content; }
    public String getResponse() { return response; }
    public boolean isResolved() { return isResolved; }
    
    public void respond(String response) {
        this.response = response;
        this.isResolved = true;
    }
}
