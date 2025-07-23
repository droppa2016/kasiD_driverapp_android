package co.za.kasi.model.superApp.a.superAppLogin;

import co.za.kasi.model.DataObject;

public class SkynetRole extends DataObject {

    private String createdAt;
    private String updatedAt;

    private String name;

    public SkynetRole(String createdAt, String updatedAt, String name) {
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.name = name;
    }

    public SkynetRole() {
    }


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
