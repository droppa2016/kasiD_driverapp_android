package co.za.kasi.model;

public class NewPasswordDTO {

    private String username;

    private String resetToken;

    private char[] newPwd;

    public NewPasswordDTO(String username, String resetToken, char[] newPwd) {
        this.username = username;
        this.resetToken = resetToken;
        this.newPwd = newPwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public char[] getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(char[] newPwd) {
        this.newPwd = newPwd;
    }
}
