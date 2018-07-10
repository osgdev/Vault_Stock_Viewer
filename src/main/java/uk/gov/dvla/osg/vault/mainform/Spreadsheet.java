package uk.gov.dvla.osg.vault.mainform;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;

import uk.gov.dvla.osg.vault.data.CardData;
import uk.gov.dvla.osg.vault.enums.Style;
import uk.gov.dvla.osg.vault.enums.TableName;

public class Spreadsheet {

    private static final int COLUMN_WIDTH = 3000;

    private static final XSSFColor LIGHT_BLUE = new XSSFColor(new java.awt.Color(218, 227, 243));
    private static final XSSFColor DARK_BLUE = new XSSFColor(new java.awt.Color(68, 114, 196));
    private static final XSSFColor WHITE = new XSSFColor(new java.awt.Color(255, 255, 255));
    private static final XSSFColor BORDER_COLOR = new XSSFColor(new java.awt.Color(143, 170, 220));
    // Collection of styles to be used
    private Map<Style, XSSFCellStyle> styles = new HashMap<>();
    // Lookup table from a TableName enum
    private final Map<TableName, List<CardData>> dataMap;

    /**
     * Constructor
     * 
     * @param dataMap The data to be written into the spreadsheet.
     */
    Spreadsheet(Map<TableName, List<CardData>> dataMap) {
        this.dataMap = dataMap;
    }

    /**
     * Creates a workbook. Adds data to each table and applies styling to each cell.
     * Saves the workbook to the desktop.
     * 
     * @throws IOException
     */
    void save() throws IOException, RuntimeException {
        // Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        createWorkbookStyles(workbook);

        // On Shelf sheet
        XSSFSheet sheet_IVB = workbook.createSheet("On Shelf");
        createRows(sheet_IVB);
        addData(TableName.ONSHELF_TACHO, sheet_IVB, 0);
        addData(TableName.ONSHELF_BRP, sheet_IVB, 4);
        addData(TableName.ONSHELF_POL, sheet_IVB, 8);
        addData(TableName.ONSHELF_DQC, sheet_IVB, 12);
        addTotalRow(sheet_IVB);
        setColumnWidths(sheet_IVB);

        // On Crate sheet
        XSSFSheet sheet_ICB = workbook.createSheet("In Crate");
        createRows(sheet_ICB);
        addData(TableName.ONCRATE_TACHO, sheet_ICB, 0);
        addData(TableName.ONCRATE_BRP, sheet_ICB, 4);
        addData(TableName.ONCRATE_POL, sheet_ICB, 8);
        addData(TableName.ONCRATE_DQC, sheet_ICB, 12);
        addTotalRow(sheet_ICB);
        setColumnWidths(sheet_ICB);

        // All Stock sheet
        XSSFSheet sheet_ASB = workbook.createSheet("All Stock");
        createRows(sheet_ASB);
        addData(TableName.ALLSTOCK_TACHO, sheet_ASB, 0);
        addData(TableName.ALLSTOCK_BRP, sheet_ASB, 4);
        addData(TableName.ALLSTOCK_POL, sheet_ASB, 8);
        addData(TableName.ALLSTOCK_DQC, sheet_ASB, 12);
        addTotalRow(sheet_ASB);
        setColumnWidths(sheet_ASB);
        
        // First UCI sheet
        XSSFSheet sheet_UCI = workbook.createSheet("UCI's");
        createRows(sheet_UCI);
        addData(TableName.UCI_TACHO, sheet_UCI, 0);
        addData(TableName.UCI_BRP, sheet_UCI, 4);
        addData(TableName.UCI_POL, sheet_UCI, 8);
        addData(TableName.UCI_DQC, sheet_UCI, 12);
        setColumnWidths(sheet_UCI);

        setPrintArea(workbook);

        // Write the workbook in file system
        saveWorkbook(workbook);
    }

    /**
     * Creates 14 blank rows for sheet, 12 data rows plus the header and total rows.
     * 
     * @param sheet The sheet on which the rows will be added.
     */
    private void createRows(XSSFSheet sheet) {
        IntStream.range(0, 14).forEach(i -> sheet.createRow(i));
    }

    /**
     * Adds a single table to the supplied sheet at the location specified.
     * 
     * @param tableName The table to be added to the sheet.
     * @param sheet The sheet on which the table will be added.
     * @param style A collection of styles to be applied to cells.
     * @param colStart The starting column into which the table will be inserted.
     */
    private void addData(TableName tableName, XSSFSheet sheet, int colStart) throws RuntimeException {

        Iterator<CardData> data = dataMap.get(tableName).iterator();

        sheet.rowIterator().forEachRemaining(r -> {

            // Iterator returns Row, convert to XSSFRow to apply styles
            XSSFRow row = (XSSFRow) r;
            // Switch between odd and even rows
            XSSFCellStyle cellStyle = row.getRowNum() % 2 == 0 ? styles.get(Style.EVEN_ROW_TYPE) : styles.get(Style.ODD_ROW_TYPE);
            XSSFCellStyle siteStyle = row.getRowNum() % 2 == 0 ? styles.get(Style.EVEN_ROW_SITE) : styles.get(Style.ODD_ROW_SITE);
            XSSFCellStyle volStyle = row.getRowNum() % 2 == 0 ? styles.get(Style.EVEN_ROW_VOL) : styles.get(Style.ODD_ROW_VOL);

            // Header Row
            if (row.getRowNum() == 0) {
                XSSFCell cell = row.createCell(colStart);
                cell.setCellValue(tableName.getColumnName());
                cell.setCellStyle(styles.get(Style.HEADER_ROW_TYPE));

                cell = row.createCell(colStart + 1);
                cell.setCellValue("SITE");
                cell.setCellStyle(styles.get(Style.HEADER_ROW_SITE));

                if (tableName.name().startsWith("UCI")) {
                    cell = row.createCell(colStart + 2);
                    cell.setCellValue("UCI");
                    cell.setCellStyle(styles.get(Style.HEADER_ROW_TYPE));
                } else {
                    cell = row.createCell(colStart + 2);
                    cell.setCellValue("VOLUME");
                    cell.setCellStyle(styles.get(Style.HEADER_ROW_VOL));
                }
                // Data on all but final row (Total row)
            } else if (row.getRowNum() < sheet.getLastRowNum()) {
                if (data.hasNext()) {
                    CardData card = data.next();

                    XSSFCell cell = row.createCell(colStart);
                    cell.setCellValue(card.getCardType());
                    cell.setCellStyle(cellStyle);

                    cell = row.createCell(colStart + 1);
                    cell.setCellValue(card.getSite());
                    cell.setCellStyle(siteStyle);

                    if (tableName.name().startsWith("UCI")) {
                        cell = row.createCell(colStart + 2);
                        cell.setCellValue(card.getUci());
                        cell.setCellStyle(cellStyle);
                    } else {
                        cell = row.createCell(colStart + 2);
                        cell.setCellValue(card.getVolumeInt());
                        cell.setCellStyle(volStyle);
                    }
                } else {
                    XSSFCell cell = row.createCell(colStart);
                    cell.setCellStyle(cellStyle);
                    cell = row.createCell(colStart + 1);
                    cell.setCellStyle(siteStyle);
                    cell = row.createCell(colStart + 2);
                    cell.setCellStyle(cellStyle);
                }
            }
        });

    }

    /**
     * Adds a total row to the sheet with a Sum() formula being written to the
     * bottom of each table.
     * 
     * @param sheet The sheet to receive a Total Row
     * @param style The applied style will depend on whether the row is odd or even.
     */
    private void addTotalRow(XSSFSheet sheet) {
        // Add total row
        int last = sheet.getLastRowNum();
        XSSFRow totalRow = sheet.getRow(last);
        XSSFCellStyle typeStyle = totalRow.getRowNum() % 2 == 0 ? styles.get(Style.EVEN_TOTAL_TYPE) : styles.get(Style.ODD_TOTAL_TYPE);
        XSSFCellStyle siteStyle = totalRow.getRowNum() % 2 == 0 ? styles.get(Style.EVEN_TOTAL_SITE) : styles.get(Style.ODD_TOTAL_SITE);
        XSSFCellStyle volStyle = totalRow.getRowNum() % 2 == 0 ? styles.get(Style.EVEN_TOTAL_VOL) : styles.get(Style.ODD_TOTAL_VOL);

        XSSFCell cell = totalRow.createCell(0);
        cell.setCellValue("TOTAL");
        cell.setCellStyle(typeStyle);

        cell = totalRow.createCell(1);
        cell.setCellStyle(siteStyle);

        cell = totalRow.createCell(2);
        cell.setCellFormula("SUM(C1:C13)");
        cell.setCellStyle(volStyle);

        cell = totalRow.createCell(4);
        cell.setCellValue("TOTAL");
        cell.setCellStyle(typeStyle);

        cell = totalRow.createCell(5);
        cell.setCellStyle(siteStyle);

        cell = totalRow.createCell(6);
        cell.setCellFormula("SUM(G1:G13)");
        cell.setCellStyle(volStyle);

        cell = totalRow.createCell(8);
        cell.setCellValue("TOTAL");
        cell.setCellStyle(typeStyle);

        cell = totalRow.createCell(9);
        cell.setCellStyle(siteStyle);

        cell = totalRow.createCell(10);
        cell.setCellFormula("SUM(K1:K13)");
        cell.setCellStyle(volStyle);

        cell = totalRow.createCell(12);
        cell.setCellValue("TOTAL");
        cell.setCellStyle(typeStyle);

        cell = totalRow.createCell(13);
        cell.setCellStyle(siteStyle);

        cell = totalRow.createCell(14);
        cell.setCellFormula("SUM(O1:O13)");
        cell.setCellStyle(volStyle);
    }

    /**
     * Apply a standard width to all columns.
     * 
     * @param sheet The sheet on which column widths will be set.
     */
    private void setColumnWidths(XSSFSheet sheet) {
        IntStream.rangeClosed(0, 14).forEach(i -> sheet.setColumnWidth(i, COLUMN_WIDTH));
    }

    /**
     * Sets the print area for each sheet in the workbook and sets each sheet to
     * print one page wide by one page tall.
     * 
     * @param workbook The workbook on which the printarea will be set.
     */
    private void setPrintArea(XSSFWorkbook workbook) {
        workbook.sheetIterator().forEachRemaining(sheet -> {
            int index = sheet.getWorkbook().getSheetIndex(sheet.getSheetName());
            sheet.getWorkbook().setPrintArea(index, "$A$1:$O$14");
            sheet.getPrintSetup().setFitHeight((short) 1);
            sheet.getPrintSetup().setFitWidth((short) 1);
        });
    }

    /**
     * Creates different styles to be applied to cells within the workbook.
     * 
     * @param workbook The workbook in which the styles will be applied.
     */
    private void createWorkbookStyles(XSSFWorkbook workbook) {

        XSSFFont bold = workbook.createFont();
        bold.setBold(true);
        
        // Base common to all three styles
        XSSFCellStyle baseStyle = workbook.createCellStyle();

        baseStyle.setBorderBottom(BorderStyle.THIN);
        baseStyle.setBottomBorderColor(BORDER_COLOR);
        baseStyle.setBorderLeft(BorderStyle.THIN);
        baseStyle.setLeftBorderColor(BORDER_COLOR);
        baseStyle.setBorderRight(BorderStyle.THIN);
        baseStyle.setRightBorderColor(BORDER_COLOR);
        baseStyle.setBorderTop(BorderStyle.THIN);
        baseStyle.setTopBorderColor(BORDER_COLOR);
        baseStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // EVEN ROW
        XSSFCellStyle even_row_type = workbook.createCellStyle();
        even_row_type.cloneStyleFrom(baseStyle);
        even_row_type.setFillForegroundColor(LIGHT_BLUE);
        styles.put(Style.EVEN_ROW_TYPE, even_row_type);
        
        XSSFCellStyle even_row_site = workbook.createCellStyle();
        even_row_site.cloneStyleFrom(even_row_type);
        even_row_site.setAlignment(HorizontalAlignment.CENTER);
        styles.put(Style.EVEN_ROW_SITE, even_row_site);
        
        XSSFCellStyle even_row_vol = workbook.createCellStyle();
        even_row_vol.cloneStyleFrom(even_row_type);
        even_row_vol.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
        styles.put(Style.EVEN_ROW_VOL, even_row_vol);
        
        XSSFCellStyle even_total_type = workbook.createCellStyle();
        even_total_type.cloneStyleFrom(even_row_type);
        even_total_type.setFont(bold);
        styles.put(Style.EVEN_TOTAL_TYPE, even_total_type);
        
        XSSFCellStyle even_total_site = workbook.createCellStyle();
        even_total_site.cloneStyleFrom(even_row_site);
        even_total_site.setFont(bold);
        styles.put(Style.EVEN_TOTAL_SITE, even_total_site);
        
        XSSFCellStyle even_total_vol = workbook.createCellStyle();
        even_total_vol.cloneStyleFrom(even_row_vol);
        even_total_vol.setFont(bold);
        styles.put(Style.EVEN_TOTAL_VOL, even_total_vol);
        
        // ODD ROW
        XSSFCellStyle odd_row_type = workbook.createCellStyle();
        odd_row_type.cloneStyleFrom(baseStyle);
        odd_row_type.setFillForegroundColor(WHITE);
        styles.put(Style.ODD_ROW_TYPE, odd_row_type);
        
        XSSFCellStyle odd_row_site = workbook.createCellStyle();
        odd_row_site.cloneStyleFrom(odd_row_type);
        odd_row_site.setAlignment(HorizontalAlignment.CENTER);
        styles.put(Style.ODD_ROW_SITE, odd_row_site);
        
        XSSFCellStyle odd_row_vol = workbook.createCellStyle();
        odd_row_vol.cloneStyleFrom(odd_row_type);
        odd_row_vol.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));
        styles.put(Style.ODD_ROW_VOL, odd_row_vol);

        XSSFCellStyle odd_total_type = workbook.createCellStyle();
        odd_total_type.cloneStyleFrom(odd_row_type);
        odd_total_type.setFont(bold);
        styles.put(Style.ODD_TOTAL_TYPE, odd_total_type);
        
        XSSFCellStyle odd_total_site = workbook.createCellStyle();
        odd_total_site.cloneStyleFrom(odd_row_site);
        odd_total_site.setFont(bold);
        styles.put(Style.ODD_TOTAL_SITE, odd_total_site);
        
        XSSFCellStyle odd_total_vol = workbook.createCellStyle();
        odd_total_vol.cloneStyleFrom(odd_row_vol);
        odd_total_vol.setFont(bold);
        styles.put(Style.ODD_TOTAL_VOL, odd_total_vol);
        
        // HEADER ROW
        XSSFFont font = workbook.createFont();
        font.setColor(WHITE);
        font.setBold(true);
        XSSFCellStyle header_row_type = workbook.createCellStyle();
        header_row_type.cloneStyleFrom(baseStyle);
        header_row_type.setFont(font);
        header_row_type.setFillForegroundColor(DARK_BLUE);
        styles.put(Style.HEADER_ROW_TYPE, header_row_type);

        XSSFCellStyle header_row_site = workbook.createCellStyle();
        header_row_site.cloneStyleFrom(header_row_type);
        header_row_site.setAlignment(HorizontalAlignment.CENTER);
        styles.put(Style.HEADER_ROW_SITE, header_row_site);
        
        XSSFCellStyle header_row_vol = workbook.createCellStyle();
        header_row_vol.cloneStyleFrom(header_row_type);
        header_row_vol.setAlignment(HorizontalAlignment.RIGHT);
        styles.put(Style.HEADER_ROW_VOL, header_row_vol);
    }

    /**
     * Save the workbook to the file system.
     * 
     * @param workbook The workbook to save.
     * @throws FileNotFoundException if Workbook is currently open.
     * @throws IOException if unable to save Workbook to file system.
     */
    private void saveWorkbook(XSSFWorkbook workbook) throws FileNotFoundException, IOException {
        try (FileOutputStream out = new FileOutputStream(new File("cardManagement.xlsx"))) {
            workbook.write(out);
            workbook.close();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException ex) {
            throw ex;
        }
    }
}
