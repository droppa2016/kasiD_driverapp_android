package co.za.kasi.model.accountDTO;

import java.io.Serializable;
import java.util.List;

public class UserDTO implements Serializable {
    private String oid;
    private String businessKey;
    private String username;
    private String pwd;
    private String status;
    private OwnerDTO owner;
    private List<RoleDTO> roles;
    private String token;
    private CodeConfirmationDTO codeConfirmation;
    private String superAppId;

    // Add getters and setters here


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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OwnerDTO getOwner() {
        return owner;
    }

    public void setOwner(OwnerDTO owner) {
        this.owner = owner;
    }

    public List<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleDTO> roles) {
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CodeConfirmationDTO getCodeConfirmation() {
        return codeConfirmation;
    }

    public void setCodeConfirmation(CodeConfirmationDTO codeConfirmation) {
        this.codeConfirmation = codeConfirmation;
    }

    public String getSuperAppId() {
        return superAppId;
    }

    public void setSuperAppId(String superAppId) {
        this.superAppId = superAppId;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "oid='" + oid + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", username='" + username + '\'' +
                ", pwd='" + pwd + '\'' +
                ", status='" + status + '\'' +
                ", owner=" + owner +
                ", roles=" + roles +
                ", token='" + token + '\'' +
                ", codeConfirmation=" + codeConfirmation +
                ", superAppId='" + superAppId + '\'' +
                '}';
    }
}