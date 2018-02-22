package com.kthcorp.cmts.util;

import java.io.*;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class Excel1 {
    public static void xls(File inputFile, File outputFile)
    {
        // For storing data into CSV files
        StringBuffer data = new StringBuffer();
        try
        {
            FileOutputStream fos = new FileOutputStream(outputFile);

            // Get the workbook object for XLS file
            //HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(inputFile));

            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(inputFile));

            // Get first sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);
            //HSSFSheet sheet = workbook.getSheetAt(0);
            Cell cell;
            Row row;

            // Iterate through each rows from first sheet
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext())
            {
                row = rowIterator.next();
                // For each row, iterate through each columns
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext())
                {
                    cell = cellIterator.next();

                    switch (cell.getCellType())
                    {
                        case Cell.CELL_TYPE_BOOLEAN:
                            data.append(cell.getBooleanCellValue() + ",");
                            break;

                        case Cell.CELL_TYPE_NUMERIC:
                            data.append(cell.getNumericCellValue() + ",");
                            break;

                        case Cell.CELL_TYPE_STRING:
                            data.append(cell.getStringCellValue() + ",");
                            break;

                        case Cell.CELL_TYPE_BLANK:
                            data.append("" + ",");
                            break;

                        default:
                            data.append(cell + ",");
                    }


                }
                data.append('\n');
            }

            fos.write(data.toString().getBytes());
            fos.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        File inputFile = new File("E:\\tagged_1st\\메타확장_태깅_yj03_2000_180119_31_50편_완료_180125_이아름_검수.xlsx");
        File outputFile = new File("E:\\tagged_1st\\1.csv");
        xls(inputFile, outputFile);
    }
}
