/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.worker.backend;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import com.vndemo.worker.instance.contract.Employee;
import com.vndemo.worker.instance.contract.Employee.EmployeeDetails;
import com.vndemo.worker.instance.contract.EmployeeCrudRest;
import java.util.Optional;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Vlad Nicoara
 * @since 0.0.1
 */
@RestController
public class EmployeeCrudRestController implements EmployeeCrudRest {

  private EmployeeRepository employeeRepository;

  public EmployeeCrudRestController(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  @Override
  public void save(@RequestBody Employee employee) {
    employeeRepository.save(employee);
  }

  @Override
  public HttpEntity<Employee> findEmployeeById(@PathVariable Long id) {

    final Optional<Employee> employeeOptional = employeeRepository.findById(id);
    if (employeeOptional.isPresent()) {
      final Employee employee = employeeOptional.get();
      employee.add(
          linkTo(methodOn(EmployeeCrudRestController.class).findEmployeeById(id)).withSelfRel());
      employee.add(linkTo(methodOn(EmployeeCrudRestController.class).findEmployeeDetailsById(id))
          .withRel("EmployeeDetails"));
      return ResponseEntity.ok().body(employee);
    } else {
      return ResponseEntity.ok().build();
    }
  }

  @Override
  public HttpEntity<EmployeeDetails> findEmployeeDetailsById(@PathVariable Long id) {

    final Optional<Employee> employeeOptional = employeeRepository.findById(id);
    if (employeeOptional.isPresent()) {
      final EmployeeDetails employeeDetails = employeeOptional.get().getEmployeeDetails();
      employeeDetails.add(
          linkTo(methodOn(EmployeeCrudRestController.class).findEmployeeDetailsById(id))
              .withSelfRel());
      return ResponseEntity.ok().body(employeeDetails);
    } else {
      return ResponseEntity.ok().build();
    }
  }
}
