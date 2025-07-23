package co.za.kasi.model;

public class LogInDTO {
    private String oid;
    private String businessKey;
    private String username;
    private String ownerOID;
    private String token;
    private String accountStatus;
    private boolean changePwd;
    private char[] pwd;
    public String getOID() { return oid; }
    public void setOID(String value) { this.oid = value; }
    public String getBusinessKey() { return businessKey; }
    public void setBusinessKey(String value) { this.businessKey = value; }
    public String getUsername() { return username; }
    public void setUsername(String value) { this.username = value; }
    public String getOwnerOID() { return ownerOID; }
    public void setOwnerOID(String value) { this.ownerOID = value; }
    public String getToken() { return token; }
    public void setToken(String value) { this.token = value; }
    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String value) { this.accountStatus = value; }
    public boolean getChangePwd() { return changePwd; }
    public void setChangePwd(boolean value) { this.changePwd = value; }
    public char[] getPwd() { return pwd; }
    public void setPwd(char[] value) { this.pwd = value; }
}
