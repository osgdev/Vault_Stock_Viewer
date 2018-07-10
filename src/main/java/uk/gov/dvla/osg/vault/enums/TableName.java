package uk.gov.dvla.osg.vault.enums;

public enum TableName {
    // ON SHELF
    ONSHELF_TACHO ("TACHO"),
    ONSHELF_BRP ("BRP"),
    ONSHELF_POL ("POL"),
    ONSHELF_DQC ("DQC"),

    // IN CRATE
    ONCRATE_TACHO ("TACHO"),
    ONCRATE_BRP ("BRP"),
    ONCRATE_POL ("POL"),
    ONCRATE_DQC ("DQC"),

    // ALL STOCK
    ALLSTOCK_TACHO ("TACHO"),
    ALLSTOCK_BRP ("BRP"),
    ALLSTOCK_POL ("POL"),
    ALLSTOCK_DQC ("DQC"),
    
    // FIRST UCI
    UCI_TACHO ("TACHO"),
    UCI_BRP ("BRP"),
    UCI_POL ("POL"),
    UCI_DQC ("DQC");
    
    private final String columnName;

    private TableName(String columnName) {
        this.columnName = columnName;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
}
