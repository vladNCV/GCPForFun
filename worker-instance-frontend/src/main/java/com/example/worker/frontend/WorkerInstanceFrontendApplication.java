package com.example.worker.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class WorkerInstanceFrontendApplication {

  public static void main(String[] args) {
    SpringApplication.run(WorkerInstanceFrontendApplication.class, args);
  }
}
