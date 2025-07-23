package co.za.kasi.model;

import java.io.Serializable;

public class ErrorResponse implements Serializable {
    private String businessKey;
    private Integer statusCode;
    private String message;
    private String link;

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "businessKey='" + businessKey + '\'' +
                ", statusCode=" + statusCode +
                ", message='" + message + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
