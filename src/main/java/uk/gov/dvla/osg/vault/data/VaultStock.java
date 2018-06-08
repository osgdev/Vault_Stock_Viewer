
package uk.gov.dvla.osg.vault.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VaultStock {

    @SerializedName("stockTotals")
    @Expose
    private StockTotals stockTotals;

    public StockTotals getStockTotals() {
        return stockTotals;
    }

}
