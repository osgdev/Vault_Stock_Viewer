package uk.gov.dvla.osg.vault.data;

import java.util.List;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import uk.gov.dvla.osg.rpd.json.AlwaysListTypeAdapterFactory;

public class Environment {

    @SerializedName("cardStock")
    @JsonAdapter(AlwaysListTypeAdapterFactory.class)
    private List<CardStock> cardStock = null;
    
    /**
     * Gets the card stock.
     *
     * @return the card stock
     */
    public List<CardStock> getCardStock() {
        return cardStock;
    }
}
