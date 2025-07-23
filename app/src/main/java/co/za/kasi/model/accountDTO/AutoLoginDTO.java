package co.za.kasi.model.accountDTO;

import java.io.Serializable;

public class AutoLoginDTO implements Serializable {
    private String username;
    private String pwd;
    private String token;

    // Add getters and setters here


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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    @Override
    public String toString() {
        return "UserDTO{" +
                ", username='" + username + '\'' +
                ", pwd='" + pwd + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}