package co.za.kasi.model.superApp.a.superAppLogin;

import java.util.ArrayList;

import co.za.kasi.model.DriverDeviceDTO;

public class ActiveDriver {

    private SkynetDriver driver;
    private String date;
    private String vehicleReg;
    private ArrayList<SkynetEvent> events;

    private ArrayList<DriverDeviceDTO> registeredDevices;

    public ActiveDriver(SkynetDriver driver, String date, String vehicleReg, ArrayList<SkynetEvent> events) {
        this.driver = driver;
        this.date = date;
        this.vehicleReg = vehicleReg;
        this.events = events;
    }

    public ActiveDriver() {
    }

    public SkynetDriver getDriver() {
        return driver;
    }

    public void setDriver(SkynetDriver driver) {
        this.driver = driver;
    }

    public ArrayList<DriverDeviceDTO> getRegisteredDevices() {
        return registeredDevices;
    }

    public void setRegisteredDevices(ArrayList<DriverDeviceDTO> registeredDevices) {
        this.registeredDevices = registeredDevices;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVehicleReg() {
        return vehicleReg;
    }

    public void setVehicleReg(String vehicleReg) {
        this.vehicleReg = vehicleReg;
    }

    public ArrayList<SkynetEvent> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<SkynetEvent> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "ActiveDriver{" +
                "driver=" + driver +
                ", date='" + date + '\'' +
                ", vehicleReg='" + vehicleReg + '\'' +
                ", events=" + events +
                '}';
    }
}
