package uk.gov.dvla.osg.rpd.error;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
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
        
    /**
     * Gets the name of the error.
     * @return the name
     */
    public String getName() {
        return StringUtils.defaultString(name);
    }
    

    /**
     * Sets the name of the error.
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    
    /**
     * Gets the error code.
     * @return the code
     */
    public String getCode() {
        return StringUtils.defaultString(code);
    }

    /**
     * Sets the error code.
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }
    
    /**
     * Gets the time the error occured.
     * @return the time
     */
    public String getTime() {
        return StringUtils.defaultString(time);
    }

    /**
     * Sets the time the error occured.
     * @param time the new time
     */
    public void setTime(String time) {
        this.time = time;
    }
    
    /**
     * Gets the severity of the error.
     * @return the severity
     */
    public String getSeverity() {
        return StringUtils.defaultString(severity);
    }

    /**
     * Sets the severity of the error.
     * @param severity the new severity
     */
    public void setSeverity(String severity) {
        this.severity = severity;
    }
    
    /**
     * Gets the detailed error message.
     * @return the message
     */
    public String getMessage() {
        return StringUtils.defaultString(message);
        
    }

    /**
     * Sets the detailed error message.
     * @param message the new message
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * Gets the action to be performed in response to the error.
     * @return the action
     */
    public String getAction() {
        return StringUtils.defaultString(action);
    }
    
    /**
     * Sets the action to resolve the error.
     * @param action the new action
     */
    public void setAction(String action) {
        this.action = action;
    }
    
    /**
     * Gets the full exception stack trace.
     * @return the exception
     */
    public String getException() {
        return StringUtils.defaultString(exception);
    }
    
    /**
     * Sets the exception.
     * @param ex the new exception
     */
    public void setException(Exception ex) {
        this.exception = ExceptionUtils.getStackTrace(ex);
    }
    
    /* (non-Javadoc)
     * Calls getters as Gson sets empty strings to null and we want to avoid null pointer exceptions.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", getName())
                .append("code", getCode())
                .append("time", getTime())
                .append("severity", getSeverity())
                .append("message", getMessage())
                .append("action", getAction())
                .append("exception", getException())
                .toString();
    }
}
