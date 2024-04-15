package Bonus;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class ExcelUtility {

    /**
     * Reads abilities from an Excel file and maps them to corresponding person names
     *
     * @param excelFilePath path to the Excel file to be read
     * @return a map where each key is a person's name and the value is a list of their abilities
     * @throws Exception If an error occurs while processing the Excel file
     */


    public Map<String, List<String>> readAbilitiesFromExcel(String excelFilePath) throws Exception {
        Map<String, List<String>> personAbilities = new HashMap<>();
        FileInputStream fileInputStream = new FileInputStream(new File(excelFilePath));
        Workbook workbook = new XSSFWorkbook(fileInputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            Cell nameCell = row.getCell(0);
            Cell abilityCell = row.getCell(1);

            if (nameCell != null && abilityCell != null) {
                String personName = nameCell.getStringCellValue();
                String ability = abilityCell.getStringCellValue();
                personAbilities.computeIfAbsent(personName, k -> new ArrayList<>()).add(ability);
            }
        }

        fileInputStream.close();
        workbook.close();

        return personAbilities;
    }
}

