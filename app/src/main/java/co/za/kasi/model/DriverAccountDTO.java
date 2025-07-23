package co.za.kasi.model;

public class DriverAccountDTO extends DataObject{

    private DriverPersonalDetails owner;

    private String username;

    private char[] pwd;

    public DriverAccountDTO( DriverPersonalDetails driverPersonalDetails, String username, char[] pwd) {
        this.owner = driverPersonalDetails;
        this.username = username;
        this.pwd = pwd;
    }

    public DriverPersonalDetails getDriverPersonalDetails() {
        return owner;
    }

    public void setDriverPersonalDetails(DriverPersonalDetails driverPersonalDetails) {
        this.owner = driverPersonalDetails;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPwd() {
        return pwd;
    }

    public void setPwd(char[] pwd) {
        this.pwd = pwd;
    }
}
