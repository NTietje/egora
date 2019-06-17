package app.egora.Model;

import java.util.Date;

public class Community {
    private String name;
    private String desc;
    private String key;
    private Boolean privacyMode;
    private Date lastActivity;

    //Constructor
    public Community(String name, String desc, String key, Boolean privacyMode) {
        this.name = name;
        this.desc = desc;
        this.key = key;
        this.privacyMode = privacyMode;
        lastActivity = new Date();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return desc;
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
