package uk.gov.dvla.osg.vault.data;

public class CardData {

    public final String cardType;
    public Integer volume;
    public final String uci;
    public final String site;
    
    public CardData() {
        this("", "", "");
    }

    public CardData(String cardType, String uci, String site) {
        this(cardType, 0, uci, site);
    }
    
    public CardData(String cardType, Integer volume, String uci, String site) {
        this.cardType = cardType;
        this.volume = volume;
        this.uci = uci;
        this.site = site;
    }

    public String getSite() {
        return this.site;
    }
    
    public void increaseVolume(int amount) {
        volume += amount;
    }
    
    /**
     * @return the cardType
     */
    public String getCardType() {
        return cardType;
    }
    
    /**
     * @return the volume
     */
    public String getVolume() {
        return this.volume == 0 ? "" : String.format("%,d", volume);
    }
    
    public int getVolumeInt() {
        return this.volume;
    }
    
    /**
     * @return the uci
     */
    public String getUci() {
        return uci;
    }
    
    
}
