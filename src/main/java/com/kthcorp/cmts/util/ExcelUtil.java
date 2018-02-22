package com.kthcorp.cmts.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

public class ExcelUtil {

    public static void echoAsCSV(Sheet sheet) {
        Row row = null;
        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
            for (int j = 0; j < row.getLastCellNum(); j++) {
                Cell tmpCell = null;
                tmpCell = row.getCell(i);
                String tmp = tmpCell.getStringCellValue();
                tmp = tmp.replace("\n","");
                tmp = tmp.replace("\r","");
                tmp = tmp.replace("\"","");
                tmp = tmp.replace("'","");

                System.out.print("\"" + tmp + "\";");
            }
            System.out.println();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        InputStream inp = null;
        try {
            inp = new FileInputStream("E:\\tagged_1st\\메타확장_태깅_yj03_2000_180119_31_50편_완료_180125_이아름_검수.xlsx");

            //Workbook wb = WorkbookFactory.create(inp);
            HSSFWorkbook wb = new HSSFWorkbook(inp);

            for(int i=0;i<wb.getNumberOfSheets();i++) {
                System.out.println(wb.getSheetAt(i).getSheetName());
                echoAsCSV(wb.getSheetAt(i));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                inp.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}