package com.temple.multidb;

import com.temple.multidb.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {


    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;


    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeRepository.save(employee);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            List<Employee> savedEmployees = employeeService.uploadFile(file);
            return ResponseEntity.ok().body(savedEmployees);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to upload file: " + e.getMessage());
        }
    }

    @PostMapping("/uploadV2")
    public ResponseEntity<Employee> uploadEmployee(@RequestParam("name") String name,
                                                   @RequestParam("image") MultipartFile imageFile) {
        try {
            Employee employee = new Employee();
            employee.setName(name);

            Employee savedEmployee = employeeService.saveEmployee(employee, imageFile);

            return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
