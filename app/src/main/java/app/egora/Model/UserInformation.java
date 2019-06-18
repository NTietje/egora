package app.egora.Model;

public class UserInformation {

    private String userID;
    private String cityName;
    private String email;
    private String firstName;
    private String houseNumber;
    private String lastName;
    private String streetName;
    private String imageURL;
    private String communityName;
    private String communityID;




    public UserInformation(){
        this.cityName = "NA";
        this.email = "NA";
        this.firstName = "NA";
        this.houseNumber = "NA";
        this.lastName = "NA";
        this.streetName = "NA";
        this.userID = "NA";
        this.imageURL = "default";
    }
    //Constructor without Communityinformation
    public UserInformation(String cityName,String email, String firstName, String houseNumber, String lastName, String streetName) {
        this.cityName = cityName;
        this.email = email;
        this.firstName = firstName;
        this.houseNumber = houseNumber;
        this.lastName = lastName;
        this.streetName = streetName;
    }

    public UserInformation(String cityName,String email, String firstName, String houseNumber, String lastName, String streetName, String communityName) {
        this.cityName = cityName;
        this.email = email;
        this.firstName = firstName;
        this.houseNumber = houseNumber;
        this.lastName = lastName;
        this.streetName = streetName;
        this.communityName = communityName;
    }


    //Getter and Setter Methods

    public String getUserID(){
        return userID;
    }

    public void setUserID(String userID){
        this.userID = userID;
    }

    public String getImageURL(){
        return imageURL;
    }

    public void setImageURL(){
        this.imageURL = imageURL;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getCommunityID() {
        return communityID;
    }

    public void setCommunityID(String communityID) {
        this.communityID = communityID;
    }

    public String getFullName(){ return firstName + " " + lastName;}

    public String getAddress () {return cityName + " , " + streetName + " " + houseNumber;}

    /*
    public void setFullName (String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public  void setAddress (String cityName, String streetName, String houseNumber){
        this.cityName = cityName;
        this.streetName = streetName;
        this.houseNumber = houseNumber;
    }

*/

}