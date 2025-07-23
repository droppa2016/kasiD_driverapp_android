package co.za.kasi.model.errorDTO;

import java.io.Serializable;

import co.za.kasi.model.DataObject;

public class FallBackErrorDTO extends DataObject implements Serializable {
    String title;
    int code;
    String message;
    Exception error;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Exception getError() {
        return error;
    }

    public void setError(Exception error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "fallBackErrorDTO{" +
                "title='" + title + '\'' +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", error=" + error +
                '}';
    }
}
