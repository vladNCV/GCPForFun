package com.vndemo.worker.instance.contract;

import com.vndemo.worker.instance.contract.Employee.EmployeeDetails;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Vlad Nicoara
 * @since 0.0.1
 *
 * PathVariable names are required for netflix-feign reflection.
 */
@RequestMapping("/employee/crud")
public interface EmployeeCrudRest {

  @PostMapping
  void save(@RequestBody Employee employee);

  @GetMapping("/Employee/{id}")
  HttpEntity<Employee> findEmployeeById(@PathVariable("id") Long id);

  @GetMapping("/EmployeeDetails/{id}")
  HttpEntity<EmployeeDetails> findEmployeeDetailsById(@PathVariable("id") Long id);
}
