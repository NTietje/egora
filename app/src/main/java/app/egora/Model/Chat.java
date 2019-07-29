package app.egora.Model;

import java.util.Date;

public class Chat {
    private String chatID;
    private String otherChatID;
    private String userID;
    private String otherUserID;
    private String itemTitle;
    private String lastMessageText;
    private Date lastActivity;
    private boolean otherUserHasChat;
    //private ArrayList<Message> messages;


    //Constructor for Firebase
    public Chat(){
        this.chatID = "NA";
        this.otherChatID = "NA";
        this.userID = "NA";
        this.otherUserID = "NA";
        this.itemTitle = "NA";
        this.lastMessageText = "NA";
        this.otherUserHasChat = true;
        this.lastActivity = new Date();
        //this.messages = new ArrayList<>();
    }

    //Constructor
    public Chat(String userID, String otherUserID, String itemTitle) {
        this.userID = userID;
        this.otherUserID = otherUserID;
        this.itemTitle = itemTitle;
        this.otherChatID = "";
        this.lastMessageText = "";
        this.otherUserHasChat = true;
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

    public boolean getOtherUserHasChat(){
        return otherUserHasChat;
    }

    public void setOtherUserHasChat(boolean otherUserHasChat) {
        this.otherUserHasChat = otherUserHasChat;
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

    public String getOtherChatID() {
        return otherChatID;
    }

    public void setOtherChatID(String otherChatID) {
        this.otherChatID = otherChatID;
    }
}