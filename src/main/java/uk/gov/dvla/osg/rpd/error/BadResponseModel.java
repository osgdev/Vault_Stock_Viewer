
package uk.gov.dvla.osg.rpd.error;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BadResponseModel {

    @SerializedName("generalErrors")
    @Expose
    private List<GeneralError> generalErrors = new ArrayList<>();

    public BadResponseModel(String message, String action) {
        
        generalErrors.add(new GeneralError(message, action));
    }
    
    
    public String getName() {
        return generalErrors.get(0).getName();
    }

    public String getCode() {
        return generalErrors.get(0).getCode();
    }

    public String getTime() {
        return generalErrors.get(0).getTime();
    }

    public String getSeverity() {
        return generalErrors.get(0).getSeverity();
    }

    public String getMessage() {
        return generalErrors.get(0).getMessage();
    }

    public String getAction() {
        return generalErrors.get(0).getAction();
    }

}
