
package uk.gov.dvla.osg.vault.data;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import uk.gov.dvla.osg.vault.enums.CardClass;
import uk.gov.dvla.osg.vault.enums.Site;

public class CardStock {

    @SerializedName("firstUCI")
    @Expose
    private String firstUCI;
    @SerializedName("cardTypeName")
    @Expose
    private String cardType;
    @SerializedName("volumes")
    @Expose
    private List<Volume> volumes = null;
    @SerializedName("className")
    @Expose
    private String cardClass;
    @SerializedName("location")
    @Expose
    private String location;

    public String getFirstUCI() {
        return firstUCI;
    }

    public String getCardType() {
        return cardType;
    }

    public List<Volume> getVolumes() {
        return volumes;
    }

    public CardClass getCardClass() {
        return CardClass.valueOf(cardClass.toUpperCase());
    }

    public Site getSite() {
        return Site.valueOf(location.toUpperCase());
    }

}
