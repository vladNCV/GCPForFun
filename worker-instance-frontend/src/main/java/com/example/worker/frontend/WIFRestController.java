package com.example.worker.frontend;

import com.vndemo.worker.instance.contract.Employee;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Vlad Nicoara
 * @since 0.0.1
 */
@RestController
@RequestMapping("/frontend/employee/crud")
@Log4j2
public class WIFRestController {

  private final EmployeeCrudRestClient employeeCrudRestClient;

  @Autowired
  @SuppressWarnings("SpringJavaAutowiringInspection")
  public WIFRestController(EmployeeCrudRestClient employeeCrudRestClient) {
    this.employeeCrudRestClient = employeeCrudRestClient;
  }

  @GetMapping("/{id}")
  Mono<Employee> findById(@PathVariable Long id) {
    log.info("Received request for employee with id " + id);
    final HttpEntity<Employee> employeeById = employeeCrudRestClient.findEmployeeById(id);
    return Mono.justOrEmpty(employeeById.getBody());
  }
}
