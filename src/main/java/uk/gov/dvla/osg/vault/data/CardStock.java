
package uk.gov.dvla.osg.vault.data;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CardStock {

    @SerializedName("firstUCI")
    @Expose
    private String firstUCI;
    @SerializedName("cardTypeName")
    @Expose
    private String cardTypeName;
    @SerializedName("volumes")
    @Expose
    private List<Volume> volumes = null;
    @SerializedName("className")
    @Expose
    private String className;
    @SerializedName("location")
    @Expose
    private String location;

    public String getFirstUCI() {
        return firstUCI;
    }

    public String getCardTypeName() {
        return cardTypeName;
    }

    public List<Volume> getVolumes() {
        return volumes;
    }

    public String getClassName() {
        return className;
    }

    public String getLocation() {
        return location;
    }

}
