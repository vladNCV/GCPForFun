package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class WebConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(WebConfiguration.class);

    @Bean
    RouterFunction<?> routes() {
        final RouterFunction<ServerResponse> route = RouterFunctions
                .route(GET("/employees"),
                        serverRequest ->
                                ServerResponse
                                        .ok()
                                        .body(Flux.just(
                                                new Employee(0, "Vlad0"),
                                                new Employee(1, "Vlad1"),
                                                new Employee(2, "Vlad2"),
                                                new Employee(3, "Vlad3"),
                                                new Employee(4, "Vlad4"),
                                                new Employee(5, "Vlad5"),
                                                new Employee(6, "Vlad6"),
                                                new Employee(7, "Vlad7"),
                                                new Employee(8, "Vlad8")
                                        ), Employee.class));
        logger.debug("Done setting up /employees endpoint.");
        return route;
    }
}

class Employee{
    private long id;

    private String name;

    public Employee() {
    }

    public Employee(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
