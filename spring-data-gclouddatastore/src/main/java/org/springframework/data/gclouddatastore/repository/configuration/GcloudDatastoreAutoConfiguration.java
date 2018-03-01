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

package org.springframework.data.gclouddatastore.repository.configuration;

import com.google.cloud.datastore.DatastoreOptions;
import java.net.UnknownHostException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.ConditionalOnRepositoryType;
import org.springframework.boot.autoconfigure.data.RepositoryType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Vlad Nicoara
 * @since 2/22/2018 TODO: Replace date with version number.
 */
@Configuration
@ConditionalOnRepositoryType(store = "gcloudds", type = RepositoryType.IMPERATIVE)
@Import(GcloudDatastoreRepositoryAutoConfigurationRegistrar.class)
public class GcloudDatastoreAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(DatastoreOptions.class)
  public DatastoreOptions datastoreOptions() throws UnknownHostException {
    return DatastoreOptions.getDefaultInstance();
  }
}
