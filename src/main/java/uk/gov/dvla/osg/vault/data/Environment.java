package uk.gov.dvla.osg.vault.data;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import uk.gov.dvla.osg.rpd.json.AlwaysListTypeAdapterFactory;

public class Environment {

    @SerializedName("cardStock")
    @Expose
    @JsonAdapter(AlwaysListTypeAdapterFactory.class)
    //@JsonAdapter(AlwaysListTypeAdapterFactory.class)
    private List<CardStock> cardStock = null;
    
    public List<CardStock> getCardStock() {
        return cardStock;
    }
}
