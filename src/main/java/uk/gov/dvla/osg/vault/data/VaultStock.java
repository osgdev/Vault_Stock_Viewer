
package uk.gov.dvla.osg.vault.data;

import com.google.gson.annotations.SerializedName;

public class VaultStock {

    @SerializedName("stockTotals")
    private StockTotals stockTotals;

    public StockTotals getStockTotals() {
        return stockTotals;
    }

}
