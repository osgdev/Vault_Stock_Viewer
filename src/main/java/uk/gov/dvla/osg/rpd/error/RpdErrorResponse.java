
package uk.gov.dvla.osg.rpd.error;

import com.google.gson.annotations.SerializedName;

public class RpdErrorResponse {

    @SerializedName("name")
    private String name = "";
    @SerializedName("code")
    private String code = "";
    @SerializedName("time")
    private String time = "";
    @SerializedName("severity")
    private String severity = "";
    @SerializedName("message")
    private String message = "";
    @SerializedName("action")
    private String action = "";

    public RpdErrorResponse() {}
    
    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getTime() {
        return time;
    }

    public String getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "Login Error [name=" + name + ", code=" + code + ", time=" + time + ", severity=" + severity + ", message=" + message + ", action=" + action + "]";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setAction(String action) {
        this.action = action;
    }

}
