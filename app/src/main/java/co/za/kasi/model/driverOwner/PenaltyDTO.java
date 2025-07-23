package co.za.kasi.model.driverOwner;

import co.za.kasi.model.DataObject;

public class PenaltyDTO extends DataObject {

    private String startDate;
    private String endDate;
    private String waybilNumber;
    private String status;
    private String penaltyRef;

    private String oid;
    private String businessKey;
    private String transactionKey;
    private String driverKey;
    private String transactionDate;
    private String transactionTypeKey;
    private String transactionType;
    private String transactionItemKey;
    private String transactionDescription;
    private String debitAmount;
    private String creditAmount;
    private String transactionRef1;
    private String transactionRef2;
    private String transactionRef3;
    private String transactionRef4;


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

    @Override
    public String getOid() {
        return oid;
    }

    @Override
    public void setOid(String oid) {
        this.oid = oid;
    }

    @Override
    public String getBusinessKey() {
        return businessKey;
    }

    @Override
    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getTransactionKey() {
        return transactionKey;
    }

    public void setTransactionKey(String transactionKey) {
        this.transactionKey = transactionKey;
    }

    public String getDriverKey() {
        return driverKey;
    }

    public void setDriverKey(String driverKey) {
        this.driverKey = driverKey;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionTypeKey() {
        return transactionTypeKey;
    }

    public void setTransactionTypeKey(String transactionTypeKey) {
        this.transactionTypeKey = transactionTypeKey;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionItemKey() {
        return transactionItemKey;
    }

    public void setTransactionItemKey(String transactionItemKey) {
        this.transactionItemKey = transactionItemKey;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    public String getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(String debitAmount) {
        this.debitAmount = debitAmount;
    }

    public String getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(String creditAmount) {
        this.creditAmount = creditAmount;
    }

    public String getTransactionRef1() {
        return transactionRef1;
    }

    public void setTransactionRef1(String transactionRef1) {
        this.transactionRef1 = transactionRef1;
    }

    public String getTransactionRef2() {
        return transactionRef2;
    }

    public void setTransactionRef2(String transactionRef2) {
        this.transactionRef2 = transactionRef2;
    }

    public String getTransactionRef3() {
        return transactionRef3;
    }

    public void setTransactionRef3(String transactionRef3) {
        this.transactionRef3 = transactionRef3;
    }

    public String getTransactionRef4() {
        return transactionRef4;
    }

    public void setTransactionRef4(String transactionRef4) {
        this.transactionRef4 = transactionRef4;
    }

    @Override
    public String toString() {
        return "PenaltyDTO{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", waybilNumber='" + waybilNumber + '\'' +
                ", status='" + status + '\'' +
                ", penaltyRef='" + penaltyRef + '\'' +
                ", oid='" + oid + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", transactionKey='" + transactionKey + '\'' +
                ", driverKey='" + driverKey + '\'' +
                ", transactionDate='" + transactionDate + '\'' +
                ", transactionTypeKey='" + transactionTypeKey + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", transactionItemKey='" + transactionItemKey + '\'' +
                ", transactionDescription='" + transactionDescription + '\'' +
                ", debitAmount='" + debitAmount + '\'' +
                ", creditAmount='" + creditAmount + '\'' +
                ", transactionRef1='" + transactionRef1 + '\'' +
                ", transactionRef2='" + transactionRef2 + '\'' +
                ", transactionRef3='" + transactionRef3 + '\'' +
                ", transactionRef4='" + transactionRef4 + '\'' +
                '}';
    }
}
