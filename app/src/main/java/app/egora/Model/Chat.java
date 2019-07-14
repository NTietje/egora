package app.egora.Model;

import java.util.Date;

public class Chat {
    private String chatID;
    private String userID;
    private String otherUserID;
    private String otherUserName;
    private String itemTitle;
    private String lastMessageText;
    private Date lastActivity;
    //private ArrayList<Message> messages;


    //Constructor for Firebase
    public Chat(){
        this.chatID = "NA";
        this.userID = "NA";
        this.otherUserID = "NA";
        this.otherUserName = "NA";
        this.itemTitle = "NA";
        this.lastMessageText = "NA";
        this.lastActivity = new Date();
        //this.messages = new ArrayList<>();
    }

    //Constructor
    public Chat(String userID, String otherUserID, String otherUserName, String itemTitle) {
        this.userID = userID;
        this.otherUserID = otherUserID;
        this.otherUserName = otherUserName;
        this.itemTitle = itemTitle;
        this.lastMessageText = "";
        this.lastActivity = new Date();
    }

    public String getLastMessageText() {
        return lastMessageText;
    }

    public void setLastMessageText(String lastMessageText) {
        this.lastMessageText = lastMessageText;
    }

    public Date getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    /*public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }*/

    public String getOtherUserName() {
        return otherUserName;
    }

    public void setOtherUserName(String otherUser) {
        this.otherUserName = otherUser;
    }

    public String getOtherUserID() {
        return otherUserID;
    }

    public void setOtherUserID(String otherUserID) {
        this.otherUserID = otherUserID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String username) {
        this.userID = username;
    }
}