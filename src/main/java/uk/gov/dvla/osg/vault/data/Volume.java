
package uk.gov.dvla.osg.vault.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Volume {

    @SerializedName("content")
    @Expose
    private int content;
    @SerializedName("status")
    @Expose
    private String status;

    public int getContent() {
        return content;
    }

    public String getStatus() {
        return status;
    }

}
