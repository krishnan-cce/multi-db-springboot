package com.temple.multidb;
import com.temple.multidb.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> uploadFile(MultipartFile file) throws Exception {
        List<Employee> employees = parseExcelFile(file.getInputStream());
        return employeeRepository.saveAll(employees);
    }


    private List<Employee> parseExcelFile(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                System.out.println("Sheet name: " + workbook.getSheetName(i));
            }

            //Sheet sheet = workbook.getSheet("Employees");
            Sheet sheet = workbook.getSheetAt(0);

            if (sheet == null) {
                throw new IllegalArgumentException("Sheet 'Employees' does not exist in the uploaded file.");
            }

            Iterator<Row> rows = sheet.iterator();
            List<Employee> employeeList = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // Skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();
                Employee emp = new Employee();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    if (cellIdx == 1) {
                        emp.setName(currentCell.getStringCellValue());
                    }
                    cellIdx++;
                }

                employeeList.add(emp);
            }
            workbook.close();
            return employeeList;
        } catch (Exception e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
