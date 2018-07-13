package uk.gov.dvla.osg.vault.mainform;

import javafx.css.PseudoClass;
import javafx.scene.control.TableRow;
import uk.gov.dvla.osg.vault.data.CardData;

/**
 * The Class HighlightTotalRow assigns the "totalRow" CSS Class to the final row in a table. 
 * This enables us to control the CSS styling for this individual row.
 */
final class HighlightTotalRow extends TableRow<CardData> {
    private final PseudoClass totalRowPseudoClass;

    HighlightTotalRow(PseudoClass totalRowPseudoClass) {
        this.totalRowPseudoClass = totalRowPseudoClass;
    }

    @Override
    protected void updateItem(CardData data, boolean b) {
        super.updateItem(data, b);
        
        if (data == null) { return; }
        
        boolean isTotalRow = data.getCardType().equals("TOTAL") ;
        pseudoClassStateChanged(totalRowPseudoClass, isTotalRow);
    }
}
