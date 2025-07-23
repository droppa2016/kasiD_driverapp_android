package co.za.kasi.model.superApp.a.superAppLogin;

public class TokenBody {

    private String token;
    private String expirationDate;

    public TokenBody(String token, String timestamp) {
        this.token = token;
        this.expirationDate = timestamp;
    }

    public TokenBody() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public String toString() {
        return "TokenBody{" +
                "token='" + token + '\'' +
                ", expirationDate='" + expirationDate + '\'' +
                '}';
    }
}
