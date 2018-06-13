package uk.gov.dvla.osg.vault.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StockTotals {

    @SerializedName("test")
    @Expose
    private Environment test;
    @SerializedName("production")
    @Expose
    private Environment production;
    
    public Environment getTest() {
        return test;
    }

    public Environment getProduction() {
        return production;
    }

}
