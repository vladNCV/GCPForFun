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

import java.lang.annotation.Annotation;
import org.springframework.boot.autoconfigure.data.AbstractRepositoryConfigurationSourceSupport;
import org.springframework.data.repository.config.RepositoryConfigurationExtension;
import org.springframework.lang.NonNull;

/**
 * @author Vlad Nicoara
 * @since 2/22/2018 TODO: Replace date with version number.
 */
public class GcloudDatastoreRepositoryAutoConfigurationRegistrar
    extends AbstractRepositoryConfigurationSourceSupport {

  @Override
  @NonNull
  protected Class<? extends Annotation> getAnnotation() {
    return EnableGcloudDatastoreRepositories.class;
  }

  @Override
  protected Class<?> getConfiguration() {
    return EnableGcloudDatastoreRepositoriesConfiguration.class;
  }

  @Override
  protected RepositoryConfigurationExtension getRepositoryConfigurationExtension() {
    return new GcloudDatastoreRepositoryConfigurationExtension();
  }

  @EnableGcloudDatastoreRepositories
  private static class EnableGcloudDatastoreRepositoriesConfiguration {

  }
}
