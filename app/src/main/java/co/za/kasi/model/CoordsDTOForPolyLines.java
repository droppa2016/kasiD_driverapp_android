package co.za.kasi.model;

import java.util.ArrayList;

public class CoordsDTOForPolyLines {

    private ArrayList<BookingPoint> pickUpPoints;

    private ArrayList<BookingPoint> droppOffPoints;

    public ArrayList<BookingPoint> getPickUpPoints() {
        return pickUpPoints;
    }

    public void setPickUpPoints(ArrayList<BookingPoint> pickUpPoints) {
        this.pickUpPoints = pickUpPoints;
    }

    public ArrayList<BookingPoint> getDroppOffPoints() {
        return droppOffPoints;
    }

    public void setDroppOffPoints(ArrayList<BookingPoint> droppOffPoints) {
        this.droppOffPoints = droppOffPoints;
    }

    @Override
    public String toString() {
        return "CoordsDTOForPolyLines{" +
                "pickUpPoints=" + pickUpPoints +
                ", droppOffPoints=" + droppOffPoints +
                '}';
    }
}
