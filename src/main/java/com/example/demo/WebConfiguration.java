package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class WebConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(WebConfiguration.class);

    private RestTemplateBuilder restTemplateBuilder;
    private GoogleCloudConfigurationBean gcConfiguration;

    public WebConfiguration(RestTemplateBuilder restTemplateBuilder,
                            GoogleCloudConfigurationBean gcConfiguration) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.gcConfiguration = gcConfiguration;
    }

    /**
     * A couple of router-function based endpoints instead of using an annotation based rest-controller.
     * Just for fun. 😛
     */
    @Bean
    RouterFunction<?> routes() {
        List<Employee> employeeList = List.of(new Employee(0, "ListEmployee0"),
                                              new Employee(1, "ListEmployee1"),
                                              new Employee(2, "ListEmployee2"));

        final RouterFunction<ServerResponse> route = RouterFunctions
                .route(GET("/employees"),
                       serverRequest ->
                               ServerResponse
                                       .ok()
                                       .body(Flux.fromIterable(employeeList)
                                                 .mergeWith(Flux.range(1, 10)
                                                                .map(i -> new Employee(i, "EmployeeNr" + i)))
                                               , Employee.class))
                .andRoute(GET("/gfunction/{name}"),
                          serverRequest -> {
                              final ResponseEntity<String> serverResponseHandlerFunction =
                                      restTemplateBuilder.build()
                                                         .postForEntity(gcConfiguration.getFunctions().get("HelloWorld").getUri(),
                                                                        new Employee(0, serverRequest.pathVariable("name")),
                                                                        String.class);
                              return ServerResponse
                                      .status(serverResponseHandlerFunction.getStatusCode())
                                      .body(Mono.justOrEmpty(serverResponseHandlerFunction.getBody()),
                                            String.class);
                          });
        logger.debug("Done setting up /employees endpoint.");
        return route;
    }
}

