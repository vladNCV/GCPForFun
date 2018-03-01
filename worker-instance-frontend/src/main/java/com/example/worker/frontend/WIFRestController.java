package com.example.worker.frontend;

import com.example.workerinstanceinterface.Employee;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

/**
 * @author Vlad Nicoara
 * @since 0.0.1
 */
@RestController
@RequestMapping("/employee/crud")
@RibbonClient("worker-instance-backend")
@Log4j2
public class WIFRestController {

//  @Autowired
//  private EmployeeCrudRestClient employeeCrudRestClient;

  @Autowired
  RestTemplate restTemplate;

  @LoadBalanced
  @Bean
  RestTemplate getRestTemplate() {
    return new RestTemplate();
  }

  @PostMapping
  void save(@RequestBody Employee employee) {
//    employeeCrudRestClient.save(employee);

  }

  @GetMapping("/{id}")
  Mono<Employee> findById(@PathVariable Long id) {
//    final HttpEntity<Employee> employeeById = employeeCrudRestClient.findEmployeeById(id);
//    return Mono.justOrEmpty(employeeById.getBody());
    log.info("Received request for empoyee with id: " + id);
    final ResponseEntity<Employee> httpEntity = this.restTemplate.getForEntity(
        "http://worker-instance-backend/employee/crud/Employee/{$id}", Employee.class, id);
    log.info(httpEntity.getStatusCode());
    log.info(httpEntity.getStatusCodeValue());
    log.info(httpEntity.getHeaders());
    log.info(httpEntity.getBody());
    log.info(httpEntity.toString());
    return Mono.justOrEmpty(httpEntity.getBody());
  }
}
