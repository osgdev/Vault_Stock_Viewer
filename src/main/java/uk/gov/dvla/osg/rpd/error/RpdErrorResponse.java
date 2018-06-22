
package uk.gov.dvla.osg.rpd.error;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

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
    // For logging Java Exception Stack Traces
    private String exception = "";
    
    public RpdErrorResponse() {}
    
    public String getName() {
        return StringUtils.defaultString(name);
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCode() {
        return StringUtils.defaultString(code);
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    public String getTime() {
        return StringUtils.defaultString(time);
    }

    public void setTime(String time) {
        this.time = time;
    }
    
    public String getSeverity() {
        return StringUtils.defaultString(severity);
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    public String getMessage() {
        return StringUtils.defaultString(message);
        
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getAction() {
        return StringUtils.defaultString(action);
    }
    
    public void setAction(String action) {
        this.action = action;
    }
    
    public String getException() {
        return StringUtils.defaultString(exception);
    }
    
    public void setException(Exception ex) {
        this.exception = ExceptionUtils.getStackTrace(ex);
    }
    
    /* (non-Javadoc)
     * Calls getters as Gson sets empty strings to null and we want to avoid null pointer exceptions.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RPD Rest Client Error [name=" + getName() + ", code=" + getCode() + ", time=" + getTime() + ", severity=" + getSeverity() 
        + ", message=" + getMessage() + ", action=" + getAction() + ", exception=" + getException() +"]";
    }

}
