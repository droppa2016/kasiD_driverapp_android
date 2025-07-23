package co.za.kasi.model;


import com.google.gson.annotations.SerializedName;

public class UserAccount extends DataObject {

    @SerializedName("username")
    private String username; //email address

    @SerializedName("status")
    private String status;

    @SerializedName("owner")
    private Person owner;

    @SerializedName("token")
    private String token;

    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
                "username='" + username + '\'' +
                ", status='" + status + '\'' +
                ", owner=" + owner +
                ", token='" + token + '\'' +
                '}';
    }
}
