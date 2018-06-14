
package uk.gov.dvla.osg.rpd.error;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GeneralError {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("severity")
    @Expose
    private String severity;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("action")
    @Expose
    private String action;
    
    public GeneralError(String message, String action) {
        this.message = message;
        this.action = action;
        this.code = "";
    }
    
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

}
