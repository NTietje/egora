package app.egora.Model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {
    private String sendingUser;
    private Date date;
    private String text;

    //Constructor for Firebase
    public Message() {
        this.sendingUser = "NA";
        this.text = "NA";
        date = new Date();
    }

    //Constructor
    public Message(String sendingUser, String text) {
        this.sendingUser = sendingUser;
        this.text = text;
        date = new Date();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSendingUser() {
        return sendingUser;
    }

    public void setSendingUser(String sendingUser) {
        this.sendingUser = sendingUser;
    }

    public static String getDateStringOfMessage(Message message) {
        Date date = message.getDate();
        SimpleDateFormat dateFormatter =
                new SimpleDateFormat("dd.MM.yy");
        String dateString = dateFormatter.format(date);
        return dateString;
    }

    public static String getDateAndTimeOfMessage(Message message) {
        Date date = message.getDate();
        SimpleDateFormat dateFormatter =
                new SimpleDateFormat("dd.MM.yy, HH:mm");
        String dateString = dateFormatter.format(date);
        return dateString;
    }

    public static String getDateStringOfDate(Date date) {
        SimpleDateFormat dateFormatter =
                new SimpleDateFormat("dd.MM.yy");
        String dateString = dateFormatter.format(date);
        return dateString;
    }
}
