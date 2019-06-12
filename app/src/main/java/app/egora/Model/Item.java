package app.egora.Model;

public class Item {

    private String name;
    private String description;
    private String downloadUrl;
    private String ownerId;
    private String itemId;

    public Item(){

        this.name = "NA";
        this.description = "NA";
        this.downloadUrl = "NA";
        this.ownerId = "NA";
        this.itemId = "NA";

    }



    public Item(String name, String description, String downloadUrl, String ownerId, String itemId){
        this.itemId = itemId;
        this.name = name;
        this.description = description;
        this.downloadUrl = downloadUrl;
        this.ownerId = ownerId;
    }

    public Item(String itemId, String ownerId){
        this.itemId = itemId;
        this.ownerId = ownerId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}