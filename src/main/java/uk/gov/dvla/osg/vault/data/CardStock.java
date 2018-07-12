
package uk.gov.dvla.osg.vault.data;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import uk.gov.dvla.osg.vault.enums.CardClass;
import uk.gov.dvla.osg.vault.enums.Site;

/**
 * The Class CardStock.
 */
public class CardStock {

    @SerializedName("firstUCI")
    private String firstUCI;
    @SerializedName("cardTypeName")
    private String cardType;
    @SerializedName("className")
    private String cardClass;
    @SerializedName("location")
    private String location;
    @SerializedName("volumes")
    private List<Volume> volumes = null;
    
    /**
     * Gets the first UCI.
     *
     * @return the first UCI
     */
    public String getFirstUCI() {
        return firstUCI;
    }

    /**
     * Gets the card type.
     *
     * @return the card type
     */
    public String getCardType() {
        return cardType;
    }

    /**
     * Gets the volumes.
     *
     * @return the volumes
     */
    public List<Volume> getVolumes() {
        return volumes;
    }

    /**
     * Gets the card class.
     *
     * @return the card class
     */
    public CardClass getCardClass() {
        return CardClass.valueOf(cardClass.toUpperCase());
    }

    /**
     * Gets the site.
     *
     * @return the site
     */
    public Site getSite() {
        return Site.valueOf(location.toUpperCase());
    }

}
