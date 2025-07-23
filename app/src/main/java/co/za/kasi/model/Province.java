package co.za.kasi.model;

public class Province {

    private String title;
    private String province;

    public Province(String title, String province) {
        this.title = title;
        this.province = province;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Override
    public String toString() {
        return this.title;
    }
}