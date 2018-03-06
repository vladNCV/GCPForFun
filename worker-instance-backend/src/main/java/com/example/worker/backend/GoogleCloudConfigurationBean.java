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

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Vlad Nicoara
 * @since 0.0.1
 */
@Data
@ConfigurationProperties(prefix = "google-cloud")
@Configuration
class GoogleCloudConfigurationBean {

  private Map<String, Function> functions;

  @Data
  @SuppressWarnings("WeakerAccess")
  public class Function {

    private String uri;
  }
}
