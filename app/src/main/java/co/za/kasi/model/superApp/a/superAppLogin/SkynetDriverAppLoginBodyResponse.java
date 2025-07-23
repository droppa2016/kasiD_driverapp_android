package co.za.kasi.model.superApp.a.superAppLogin;

import co.za.kasi.model.DataObject;
import co.za.kasi.model.superApp.a.vehicle.SkynetVehicle;

public class SkynetDriverAppLoginBodyResponse extends DataObject {

    private TokenBody tokenResponse;
    private SkynetUserAccount userAccount;

    private SkynetDriver driver;

    private ActiveDriver activeDriver;

    private SkynetVehicle vehicle;

    public SkynetDriverAppLoginBodyResponse() {
    }

    public SkynetDriverAppLoginBodyResponse(TokenBody tokenBody, SkynetUserAccount userAccount, SkynetDriver driver) {
        this.tokenResponse = tokenBody;
        this.userAccount = userAccount;
        this.driver = driver;
    }

    public TokenBody getTokenResponse() {
        return tokenResponse;
    }

    public void setTokenResponse(TokenBody tokenResponse) {
        this.tokenResponse = tokenResponse;
    }

    public SkynetUserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(SkynetUserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public SkynetDriver getDriver() {
        return driver;
    }

    public void setDriver(SkynetDriver driver) {
        this.driver = driver;
    }

    public SkynetVehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(SkynetVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public ActiveDriver getActiveDriver() {
        return activeDriver;
    }

    public void setActiveDriver(ActiveDriver activeDriver) {
        this.activeDriver = activeDriver;
    }

    @Override
    public String toString() {
        return "SkynetDriverAppLoginBodyResponse{" +
                "tokenResponse=" + tokenResponse +
                ", userAccount=" + userAccount +
                ", driver=" + driver +
                ", activeDriver=" + activeDriver +
                '}';
    }
}
