package co.za.kasi.model.superApp.a.superAppLogin;

import co.za.kasi.model.DataObject;

public class SkynetDriverAppLoginBody extends DataObject {

    private String driverIdNumber;
    private String password;

    public SkynetDriverAppLoginBody(String driverIdNumber, String password) {
        this.driverIdNumber = driverIdNumber;
        this.password = password;
    }

    public SkynetDriverAppLoginBody() {
    }

    public String getDriverIdNumber() {
        return driverIdNumber;
    }

    public void setDriverIdNumber(String driverIdNumber) {
        this.driverIdNumber = driverIdNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
