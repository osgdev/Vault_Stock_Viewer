
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
        return name != null ? name : "";
    }

    public String getCode() {
        return code != null ? code : "";
    }

    public String getTime() {
        return time != null ? time : "";
    }

    public String getSeverity() {
        return severity != null ? severity : "";
    }

    public String getMessage() {
        return message != null ? message : "";
    }

    public String getAction() {
        return action != null ? action : "";
    }

    @Override
    public String toString() {
        return "Login Error [name=" + getName() + ", code=" + getCode() + ", time=" + getTime() + ", severity=" + getSeverity() + ", message=" + getMessage() + ", action=" + getAction() + "]";
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
