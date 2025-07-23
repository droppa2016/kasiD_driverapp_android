package co.za.kasi.model;

import java.util.ArrayList;

/**
 * created by {Andries Sebola} on {8/29/2022}.
 */
public class SignatureUpdate {
    String oid;
    String businessKey;
    ArrayList<String> contactid;
    String signature;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public ArrayList<String> getContactid() {
        return contactid;
    }

    public void setContactid(ArrayList<String> contactid) {
        this.contactid = contactid;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "SignatureUpdate{" +
                "oid='" + oid + '\'' +
                ", contactid=" + contactid +
                ", businessKey='" + businessKey + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
