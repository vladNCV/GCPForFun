package com.example.workerinstanceinterface;

import com.example.workerinstanceinterface.Employee.EmployeeDetails;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Vlad Nicoara
 * @since 0.0.1
 */
@RequestMapping("/employee/crud")
public interface EmployeeCrudRest {

  @PostMapping
  void save(@RequestBody Employee employee);

  @GetMapping("/Employee/{id}")
  HttpEntity<Employee> findEmployeeById(@PathVariable Long id);

  @GetMapping("/EmployeeDetails/{id}")
  HttpEntity<EmployeeDetails> findEmployeeDetailsById(@PathVariable Long id);
}
