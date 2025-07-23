package co.za.kasi.model;

public class ResetPasswordDTO {

   private String ownerOid;
    private String target;

     private String code;


    public ResetPasswordDTO(String ownerOid, String target, String code) {
        this.ownerOid = ownerOid;
        this.target = target;
        this.code = code;
    }

    public String getOwnerOid() {
        return ownerOid;
    }

    public void setOwnerOid(String ownerOid) {
        this.ownerOid = ownerOid;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
