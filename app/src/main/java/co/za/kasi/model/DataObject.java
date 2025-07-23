package co.za.kasi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataObject implements Serializable {
    private String oid;

    private String businessKey;

    public String getOid() {return oid;}

    public void setOid(String oid) {this.oid = oid;}

    public String getBusinessKey() {return businessKey;}

    public void setBusinessKey(String businessKey) {this.businessKey = businessKey;}

}
