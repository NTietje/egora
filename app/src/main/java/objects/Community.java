package objects;

import java.util.Date;

public class Community {
    private String name;
    private String description;
    private String postalCode;
    private String key;
    private Boolean privateMode;
    private Date lastActivity;

    /*
    public Community(String name, String description, String key, Integer postalCode) {
        this.name = name;
        this.description = description;
        this.postalCode = postalCode;
        this.key = key;
    }
    */

    //Constructor
    public Community(String name, String description, String key, String postalCode) {
        this.name = name.trim();
        privateMode = false;
        //only save if the edittextfields are filled
        if(!empty(description)) {
            this.description = description;
        }
        if(!empty(key)) {
            this.key = key.trim();
            privateMode = true;
        }
        if(!empty(postalCode)) {
            this.postalCode = postalCode.trim();
        }
    }


    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPostalCode() {
        return postalCode;
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

    public Boolean getPrivateMode() {
        return privateMode;
    }

    public static boolean empty(String s){
        return s == null || s.trim().isEmpty();
    }
}
