package uk.gov.dvla.osg.vault.data;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Environment {

    @SerializedName("cardStock")
    @Expose
    private List<CardStock> cardStock = null;

    public List<CardStock> getCardStock() {
        return cardStock;
    }
}
