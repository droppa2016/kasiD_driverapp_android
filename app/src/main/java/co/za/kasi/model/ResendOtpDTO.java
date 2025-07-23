package co.za.kasi.model;

public class ResendOtpDTO extends DataObject{

    private String deviceId;

    private String mobile;

    private boolean mobileVerified;

    private String firstName;

    private String surname;

    private String rsaId;

    private String email;

    private boolean emailVerified;

    private boolean documentOid;

    private String creationStamp;

    private int retailId;

    private String userAccountOid;

    public ResendOtpDTO(String deviceId, String mobile, boolean mobileVerified, String firstName, String surname, String rsaId, String email, boolean emailVerified, boolean documentOid, String creationStamp, int retailId, String userAccountOid) {
        this.deviceId = deviceId;
        this.mobile = mobile;
        this.mobileVerified = mobileVerified;
        this.firstName = firstName;
        this.surname = surname;
        this.rsaId = rsaId;
        this.email = email;
        this.emailVerified = emailVerified;
        this.documentOid = documentOid;
        this.creationStamp = creationStamp;
        this.retailId = retailId;
        this.userAccountOid = userAccountOid;
    }
}
