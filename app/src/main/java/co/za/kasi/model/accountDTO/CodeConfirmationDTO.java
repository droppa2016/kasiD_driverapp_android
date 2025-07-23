package co.za.kasi.model.accountDTO;

public class CodeConfirmationDTO {
    private String oid;
    private String businessKey;
    private String ownerOid;
    private boolean confirmed;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getOwnerOid() {
        return ownerOid;
    }

    public void setOwnerOid(String ownerOid) {
        this.ownerOid = ownerOid;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    @Override
    public String toString() {
        return "CodeConfirmationDTO{" +
                "oid='" + oid + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", ownerOid='" + ownerOid + '\'' +
                ", confirmed=" + confirmed +
                '}';
    }
}
