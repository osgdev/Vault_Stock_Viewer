
package uk.gov.dvla.osg.vault.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import uk.gov.dvla.osg.vault.enums.Status;

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

    public Status getStatus() {
        return Status.valueOf(status.toUpperCase());
    }
}
