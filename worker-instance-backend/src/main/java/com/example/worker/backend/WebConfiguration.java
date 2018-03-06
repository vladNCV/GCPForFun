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

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

import com.google.auth.oauth2.ComputeEngineCredentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.datastore.DatastoreOptions;
import com.vndemo.worker.instance.contract.Employee;
import com.vndemo.worker.instance.contract.Employee.EmployeeDetails;
import com.vndemo.worker.instance.contract.Employee.WorkPosition;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import lombok.experimental.ExtensionMethod;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Vlad Nicoara
 * @since 0.0.1
 */
@Configuration
@ExtensionMethod({ExtensionUtils.class})
@Log4j2
public class WebConfiguration {

  private RestTemplateBuilder restTemplateBuilder;
  private GoogleCloudConfigurationBean gcConfiguration;

  public WebConfiguration(
      RestTemplateBuilder restTemplateBuilder, GoogleCloudConfigurationBean gcConfiguration) {
    this.restTemplateBuilder = restTemplateBuilder;
    this.gcConfiguration = gcConfiguration;
  }

  @Bean
  @Profile("dev")
  public DatastoreOptions datastoreOptionsBean() throws IOException {
    log.error("Initializing DatastoreOptions from file.");
    final GoogleCredentials googleCredentials = ComputeEngineCredentials.fromStream(
        new FileInputStream(".gcloud/datastore-owner_keyfile.json"));
    return DatastoreOptions.newBuilder()
        .setProjectId("careful-griffin-107207")
        .setCredentials(googleCredentials)
        .build();
  }

  /**
   * A couple of router-function based endpoints instead of using an annotation based
   * rest-controller. Just for fun. 😛
   */
  @Bean
  RouterFunction<?> routes() {
    new Employee();
    List<Employee> employeeList =
        List.of(
            // new Employee(0, "listEmployee0").capitalize(),
            buildMockEmployee(1), buildMockEmployee(2));

    final RouterFunction<ServerResponse> route =
        RouterFunctions.route(
            GET("/employees"),
            serverRequest ->
                ServerResponse.ok()
                    .body(
                        Flux.range(1, 10)
                            .map(this::buildMockEmployee)
                            .mergeWith(Flux.fromIterable(employeeList)),
                        Employee.class))
            .andRoute(
                GET("/google/function/{name}"),
                serverRequest -> {
                  final ResponseEntity<String> serverResponseHandlerFunction =
                      restTemplateBuilder
                          .build()
//                         TODO: Fix google cloud function access.
                          .postForEntity(
                              gcConfiguration.getFunctions().get("HelloWorld").getUri(),
                              buildMockEmployee(0),
                              String.class);
                  return ServerResponse.status(serverResponseHandlerFunction.getStatusCode())
                      .body(
                          Mono.justOrEmpty(serverResponseHandlerFunction.getBody()), String.class);
                });
    log.info("Done setting up /employees endpoint.");
    return route;
  }

  private Employee buildMockEmployee(long id) {
    Employee employee = new Employee();
    employee.setEmployeeId(id);
    employee.setName("Name" + id);
    employee.setTimestamp(ZonedDateTime.now());
    final EmployeeDetails employeeDetails = new EmployeeDetails();
    employeeDetails.setDob(ZonedDateTime.now().minusYears(23));
    employeeDetails.setWorkExperienceYears(3);
    employee.setEmployeeDetails(employeeDetails);
    final Collection<WorkPosition> workPositions =
        List.of(
            new WorkPosition(
                "junior developer",
                Duration.between(
                    LocalDateTime.of(2015, 8, 15, 0, 0), LocalDateTime.of(2016, 9, 20, 0, 0))),
            new WorkPosition(
                "developer",
                Duration.between(LocalDateTime.of(2015, 8, 15, 0, 0), LocalDateTime.now())));
    employee.setWorkPositions(workPositions);

    return employee;
  }
}
