package app.egora.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Community {
    private String name;
    private String description;
    private String key;
    private Boolean privacyMode;
    private Date lastActivity;
    private ArrayList<String> userIDs;

    //Constructor
    public Community(String name, String desc, String key, Boolean privacyMode) {
        this.name = name;
        this.description = desc;
        this.key = key;
        this.privacyMode = privacyMode;
        lastActivity = new Date();
        userIDs = new ArrayList<>();
    }

    public void addUser(String userID) {
        userIDs.add(userID);
    }

    public void deleteUser(String userID) {
        userIDs.remove(userID);
    }

    public List<String> getUserIDs() {
        return userIDs;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return key;
    }

    public Date getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
    }

    public Boolean getPrivacyMode() {
        return privacyMode;
    }

}
