package uk.gov.dvla.osg.vault.data;

public class CardData {

    public final String cardType;
    public Integer volume;
    public final String uci;
    public final String site;
    
    /**
     * Instantiates a new card data with blank fields.
     * This is used for empty rows in the table.
     */
    public CardData() {
        this("");
    }

    /**
     * Instantiates a new card data with just the name field filled in.
     * This is used for the TOTAL row.
     *
     * @param cardType the card type
     */
    public CardData(String cardType) {
        this(cardType, "", "");
    }
    
    /**
     * Instantiates a new card data without any volume.
     *
     * @param cardType the card type
     * @param uci the uci
     * @param site the site
     */
    public CardData(String cardType, String uci, String site) {
        this(cardType, 0, uci, site);
    }
    
    /**
     * Instantiates a new card data with all fields.
     *
     * @param cardType the card type
     * @param volume the volume
     * @param uci the uci
     * @param site the site
     */
    public CardData(String cardType, Integer volume, String uci, String site) {
        this.cardType = cardType;
        this.volume = volume;
        this.uci = uci;
        this.site = site;
    }

    /**
     * Gets the site.
     *
     * @return the site
     */
    public String getSite() {
        return this.site;
    }
    
    /**
     * Increases volume by the given amount.
     *
     * @param amount the amount
     */
    public void increaseVolume(int amount) {
        volume += amount;
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
     * Gets the volume as a string with thousands separator.
     *
     * @return the volume
     */
    public String getVolume() {
        return this.volume == 0 ? "" : String.format("%,d", volume);
    }
    
    /**
     * Gets the volume as an integer. This is used when saving to spreadsheet.
     *
     * @return the volume int
     */
    public int getVolumeInt() {
        return this.volume;
    }
    

    /**
     * Gets the first uci.
     *
     * @return the uci
     */
    public String getUci() {
        return uci;
    }
    
    
}
