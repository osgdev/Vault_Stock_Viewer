package uk.gov.dvla.osg.vault.output;

import java.awt.print.PrinterAbortException;
import java.io.FileInputStream;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.OrientationRequested;

public class PrintImage {

    /**
     * Sends an image to the default printer.
     * @param imageFile the image to print.
     * @param copies the number of print copies required.
     * @param orientation the orientation of the printed image
     * @return <i>true</i> if image was printed, <i>false</i> if print was cancelled.
     * @throws Exception if the document is not a valid image file.
     */
    public boolean sendToDefault(String imageFile, int copies, OrientationRequested orientation) throws Exception {
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        pras.add(new Copies(copies));
        pras.add(orientation);
        
        PrintService ps = PrintServiceLookup.lookupDefaultPrintService();
        
        DocPrintJob job = ps.createPrintJob();
        
        try (FileInputStream fin = new FileInputStream(imageFile)) {
            Doc doc = new SimpleDoc(fin, DocFlavor.INPUT_STREAM.GIF, null);
            job.print(doc, pras);
            // Image was printed successfully
            return true;
        } catch (PrintException ex) {
            if (ex.getCause() instanceof PrinterAbortException) {
                // User cancelled the operation, e.g. if XPS Writer chosen as default printer
                return false; 
              }
            else {
                throw ex;
            }
        }

    }
}
