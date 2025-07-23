package co.za.kasi.model.systemDTO;

import java.io.Serializable;

public class WhatsappAccountsDTO implements Serializable {
    private String oid;
    private String businessKey;
    private String driver;
    private String client;

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

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "AccountDTO{" +
                "oid='" + oid + '\'' +
                ", businessKey='" + businessKey + '\'' +
                ", driver='" + driver + '\'' +
                ", client='" + client + '\'' +
                '}';
    }
}
