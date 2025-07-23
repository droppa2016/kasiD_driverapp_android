package co.za.kasi.model;


public class SkynetReportDTO extends DataObject{

    public String cal1;
    public String cal2;
    public String cal3;

    public SkynetReportDTO(String cal1, String cal2, String cal3) {
        this.cal1 = cal1;
        this.cal2 = cal2;
        this.cal3 = cal3;
    }

    public String getCal1() {
        return cal1;
    }

    public void setCal1(String cal1) {
        this.cal1 = cal1;
    }

    public String getCal2() {
        return cal2;
    }

    public void setCal2(String cal2) {
        this.cal2 = cal2;
    }

    public String getCal3() {
        return cal3;
    }

    public void setCal3(String cal3) {
        this.cal3 = cal3;
    }

    @Override
    public String toString() {
        return "SkynetReportDTO{" +
                "cal1='" + cal1 + '\'' +
                ", cal2='" + cal2 + '\'' +
                ", cal3='" + cal3 + '\'' +
                '}';
    }
}
