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

import org.springframework.data.gclouddatastore.repository.GcloudDatastoreRepositoryFactory;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.lang.NonNull;

/**
 * @author tkob (https://github.com/tkob)
 * @author Vlad Nicoara (minor contributions)
 * @since 2/22/2018 TODO: Replace date with version number.
 */
public class GcloudDatastoreRepositoryConfigurationExtension
    extends RepositoryConfigurationExtensionSupport {

  @Override
  @NonNull
  public String getModulePrefix() {
    return "gcloudds";
  }

  @Override
  @NonNull
  public String getRepositoryFactoryBeanClassName() {
    return GcloudDatastoreRepositoryFactory.class.getName();
  }
}
