package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.HashMap;

public class Databinding {

    //Location of the Excel test data file
    private static final String FILE_PATH = "src/main/resources/TestData.xlsx";

    public static HashMap<String, String> getTestData(String strTable, String strTestCase) throws DatabaseException
    {
        HashMap<String, String> TestData = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            DataFormatter formatter = new DataFormatter();


            //1. Get the Sheet by name
            Sheet sheet = workbook.getSheet(strTable);
            if (sheet == null) {
                throw new DatabaseException("Sheet not found : " + strTable);
            }


            //2. Read the header row to get the column Names
            Row headerRow = sheet.getRow(0);
            int columnCount = headerRow.getLastCellNum();


            //3. Find the row whose TestCase column matches and read its column Values
            for (int iRow = 1; iRow <= sheet.getLastRowNum(); iRow++) {
                Row currentRow = sheet.getRow(iRow);
                if (currentRow == null) continue;

                String rowTestCase = formatter.formatCellValue(currentRow.getCell(0)).trim();
                if (rowTestCase.equals(strTestCase)) {
                    for (int iColumn = 0; iColumn < columnCount; iColumn++) {
                        String strColumnName = formatter.formatCellValue(headerRow.getCell(iColumn)).trim();
                        Cell cell = currentRow.getCell(iColumn);
                        String strValue = formatter.formatCellValue(cell).trim();
                        TestData.put(strColumnName, strValue);
                    }
                    break;
                }
            }

        } catch (DatabaseException dbExp) {
            throw dbExp;
        } catch (Exception exp) {
            throw new DatabaseException(exp.getMessage());
        }
        return TestData;
    }

}