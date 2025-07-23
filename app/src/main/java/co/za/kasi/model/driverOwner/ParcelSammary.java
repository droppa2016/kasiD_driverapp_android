package co.za.kasi.model.driverOwner;

import co.za.kasi.model.DataObject;

import java.io.Serializable;
import java.util.List;


public class ParcelSammary extends DataObject implements Serializable {
    List<ParcelDTO> parcels;
    boolean isOwner;


    public List<ParcelDTO> getParcels() {
        return parcels;
    }

    public void setParcels(List<ParcelDTO> parcels) {
        this.parcels = parcels;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    @Override
    public String toString() {
        return "ParcelSammary{" +
                "parcels=" + parcels +
                ", isOwner=" + isOwner +
                '}';
    }
}
