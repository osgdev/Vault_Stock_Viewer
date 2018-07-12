
package uk.gov.dvla.osg.vault.data;

import com.google.gson.annotations.SerializedName;

/**
 * The Class VaultStock.
 */
public class VaultStock {

    @SerializedName("stockTotals")
    private StockTotals stockTotals;

    /**
     * Gets the stock totals.
     *
     * @return the stock totals
     */
    public StockTotals getStockTotals() {
        return stockTotals;
    }

}
