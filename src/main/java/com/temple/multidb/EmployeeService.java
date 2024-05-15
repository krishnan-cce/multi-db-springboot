package com.temple.multidb;
import com.temple.multidb.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
public class EmployeeService {

//    private static final String UPLOAD_DIR = "src/main/resources/images/";
    private static final String UPLOAD_DIR = "src/main/resources/static/images/";

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

    public Employee saveEmployee(Employee employee, MultipartFile imageFile) throws IOException {
        if (!imageFile.isEmpty()) {
            // Create the directory if it doesn't exist
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Save the image file to the directory
            String imagePath = UPLOAD_DIR + imageFile.getOriginalFilename();
            Path path = Paths.get(imagePath);
            Files.write(path, imageFile.getBytes());

            // Set the image path in the employee object
            employee.setImagePath("/images/" + imageFile.getOriginalFilename());
        }

        // Save the employee to the database
        return employeeRepository.save(employee);
    }
}
