package com.zompany.employeesystem;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<String> addEmployee(@RequestBody Employee employee) {
        try {
            Employee savedEmployee = employeeService.insertEmployee(employee);
            return ResponseEntity.status(201).body("Employee inserted successfully: " + savedEmployee);
        } catch (IllegalArgumentException e) {
            // If the exception is due to an existing employee (SSN conflict)
            if (e.getMessage().contains("SSN already exists")) {
                return ResponseEntity.status(409).body("Error: Employee with this SSN already exists");
            }
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/getAll")
    public List<Employee> list() {
        return employeeService.getAllEmployees();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Integer id) {
        boolean deleted = employeeService.deleteEmployee(id);
        if (deleted) {
            return ResponseEntity.ok("Employee deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("Employee not found with ID: " + id);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateEmployee(@PathVariable Integer id, @RequestBody Employee updatedEmployee) {
        try {
            Employee updated = employeeService.updateEmployee(id, updatedEmployee);
            return ResponseEntity.ok("Employee updated successfully with ID: " + updated.getEmpid());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Employee>> searchEmployees(
            @RequestParam(required = false) String fullname,
            @RequestParam(required = false) String ssn,
            @RequestParam(required = false) Integer empid) {
        try {
            List<Employee> results = employeeService.searchEmployees(fullname, ssn, empid);
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    @PostMapping("/increase-by-range")
    public ResponseEntity<String> increaseSalaryByRange(@RequestParam double minSalary,
                                                        @RequestParam double maxSalary,
                                                        @RequestParam double percentage) {
        int updatedCount = employeeService.increaseSalaryByRange(minSalary, maxSalary, percentage);
        return ResponseEntity.ok(updatedCount + " employees updated.");
    }

    @PostMapping("/increase-if-less-than")
    public ResponseEntity<String> increaseSalaryIfLessThan(@RequestParam double amount,
                                                           @RequestParam double percentage) {
        int updatedCount = employeeService.increaseSalaryIfLessThan(amount, percentage);
        return ResponseEntity.ok(updatedCount + " employees updated.");
    }


}
