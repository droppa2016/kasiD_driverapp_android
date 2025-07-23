package co.za.kasi.model;


public class StatementDTO extends DataObject {

    private String startDate;
    private String endDate;
    private String waybilNumber;
    private String status;
    private String penaltyRef;


    public StatementDTO(String startDate, String endDate, String waybilNumber, String status, String penaltyRef) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.waybilNumber = waybilNumber;
        this.status = status;
        this.penaltyRef = penaltyRef;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getWaybilNumber() {
        return waybilNumber;
    }

    public void setWaybilNumber(String waybilNumber) {
        this.waybilNumber = waybilNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPenaltyRef() {
        return penaltyRef;
    }

    public void setPenaltyRef(String penaltyRef) {
        this.penaltyRef = penaltyRef;
    }
}
