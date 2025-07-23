package co.za.kasi.model.driverOwner;

import co.za.kasi.model.DataObject;

import java.io.Serializable;
import java.util.List;


public class ParcelDTO extends DataObject implements Serializable {
    List<String> waybillNo;
    List<String> collectionsWaybillNo;
    int warehouseCollections;
    int collections;
    int delivered;
    int returns;
    String vehicleRegistration;
    String route;
    String waybillDate;
    String driverFullNames;
    String type;
    double amountDue;
    String contractOwner;

    public int getCollections() {
        return collections;
    }

    public void setCollections(int collections) {
        this.collections = collections;
    }

    public int getDelivered() {
        return delivered;
    }

    public void setDelivered(int delivered) {
        this.delivered = delivered;
    }

    public int getReturns() {
        return returns;
    }

    public void setReturns(int returns) {
        this.returns = returns;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(List<String> waybillNo) {
        this.waybillNo = waybillNo;
    }

    public List<String> getCollectionsWaybillNo() {
        return collectionsWaybillNo;
    }

    public void setCollectionsWaybillNo(List<String> collectionsWaybillNo) {
        this.collectionsWaybillNo = collectionsWaybillNo;
    }

    public int getWarehouseCollections() {
        return warehouseCollections;
    }

    public void setWarehouseCollections(int warehouseCollections) {
        this.warehouseCollections = warehouseCollections;
    }

    public String getVehicleRegistration() {
        return vehicleRegistration;
    }

    public void setVehicleRegistration(String vehicleRegistration) {
        this.vehicleRegistration = vehicleRegistration;
    }

    public String getWaybillDate() {
        return waybillDate;
    }

    public void setWaybillDate(String waybillDate) {
        this.waybillDate = waybillDate;
    }

    public String getDriverFullNames() {
        return driverFullNames;
    }

    public void setDriverFullNames(String driverFullNames) {
        this.driverFullNames = driverFullNames;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }

    public String getContractOwner() {
        return contractOwner;
    }

    public void setContractOwner(String contractOwner) {
        this.contractOwner = contractOwner;
    }

    @Override
    public String toString() {
        return "ParcelDTO{" +
                "waybillNo='" + waybillNo + '\'' +
                ", collectionsWaybillNo=" + collectionsWaybillNo +
                ", warehouseCollections=" + warehouseCollections +
                ", collections=" + collections +
                ", delivered=" + delivered +
                ", returns=" + returns +
                ", vehicleRegistration='" + vehicleRegistration + '\'' +
                ", route='" + route + '\'' +
                ", waybillDate='" + waybillDate + '\'' +
                ", driverFullNames='" + driverFullNames + '\'' +
                ", type='" + type + '\'' +
                ", amountDue=" + amountDue +
                ", contractOwner='" + contractOwner + '\'' +
                '}';
    }
}
