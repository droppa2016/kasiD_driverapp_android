package co.za.kasi.model.superApp.a.superAppLogin;

import co.za.kasi.model.DataObject;

public class RolesAndPermissions extends DataObject {

    private SkynetRole roles;
    private String[] permissions;

    public RolesAndPermissions(SkynetRole roles, String[] permissions) {
        this.roles = roles;
        this.permissions = permissions;
    }

    public RolesAndPermissions() {
    }

    public SkynetRole getRoles() {
        return roles;
    }

    public void setRoles(SkynetRole roles) {
        this.roles = roles;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }
}
