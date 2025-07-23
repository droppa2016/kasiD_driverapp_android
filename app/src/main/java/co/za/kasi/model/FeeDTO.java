package co.za.kasi.model;

public class FeeDTO {

    private String type;

    private double value;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FeeDTO{" +
                "type='" + type + '\'' +
                ", value=" + value +
                '}';
    }
}
