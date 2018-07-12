
package uk.gov.dvla.osg.vault.data;

import com.google.gson.annotations.SerializedName;

import uk.gov.dvla.osg.vault.enums.Status;

public class Volume {

    @SerializedName("content")
    private int content;
    @SerializedName("status")
    private String status;

    /**
     * Gets the volume of cards for the corresponding status.
     *
     * @return the content
     */
    public int getContent() {
        return content;
    }

    /**
     * Gets the status e.g. inVualt, OnCrate.
     *
     * @return the status
     */
    public Status getStatus() {
        return Status.valueOf(status.toUpperCase());
    }
}
