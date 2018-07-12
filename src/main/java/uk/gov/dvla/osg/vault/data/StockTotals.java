package uk.gov.dvla.osg.vault.data;

import com.google.gson.annotations.SerializedName;

public class StockTotals {

    @SerializedName("test")
    private Environment test;
    @SerializedName("production")
    private Environment production;
    
    /**
     * Gets the test environment.
     *
     * @return the test environment
     */
    public Environment getTest() {
        return test;
    }

    /**
     * Gets the production production environment.
     *
     * @return the production environment
     */
    public Environment getProduction() {
        return production;
    }

}
