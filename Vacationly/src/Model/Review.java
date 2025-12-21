package Model;

import java.io.Serializable;

public class Review implements Serializable {
    private String authorName;
    private int rating; // 1-5
    private String comment;
    private String adminReply;

    public Review(String authorName, int rating, String comment) {
        this.authorName = authorName;
        this.rating = rating;
        this.comment = comment;
        this.adminReply = "";
    }

    public String getAuthorName() { return authorName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getAdminReply() { return adminReply; }
    public void setAdminReply(String reply) { this.adminReply = reply; }
    
    @Override
    public String toString() {
        return rating + "/5 by " + authorName + ": " + comment;
    }
}