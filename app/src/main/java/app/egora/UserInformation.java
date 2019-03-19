package app.egora;

public class UserInformation  {

    private String cityName;
    private String email;
    private String firstName;
    private String houseNumber;
    private String lastName;
    private String streetName;



    //Constructor
    public UserInformation(String cityName,String email, String firstName, String houseNumber, String lastName, String streetName) {
        this.cityName = cityName;
        this.email = email;
        this.firstName = firstName;
        this.houseNumber = houseNumber;
        this.lastName = lastName;
        this.streetName = streetName;
    }

    //Getter and Setter Methods
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
}