package co.za.kasi.model;

import java.io.Serializable;

public class ApplicationVersion implements Serializable {

    private String versionCode;
    private String versionName;
    private String status;

    public ApplicationVersion() {
    }

    public ApplicationVersion(String versionCode, String versionName, String status) {
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.status = status;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
