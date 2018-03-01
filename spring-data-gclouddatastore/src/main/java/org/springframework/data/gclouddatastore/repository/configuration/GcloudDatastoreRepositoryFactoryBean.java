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
import java.io.Serializable;
import org.springframework.data.gclouddatastore.repository.GcloudDatastoreRepositoryFactory;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * @author tkob (https://github.com/tkob)
 * @author Vlad Nicoara (minor contributions)
 * @since 2/22/2018 TODO: Replace date with version number.
 */
public class GcloudDatastoreRepositoryFactoryBean<
    T extends Repository<S, ID>, S, ID extends Serializable>
    extends RepositoryFactoryBeanSupport<T, S, ID> {

  private DatastoreOptions datastoreOptions;

  public GcloudDatastoreRepositoryFactoryBean(
      Class<? extends T> repositoryInterface, DatastoreOptions datastoreOptions) {
    super(repositoryInterface);
    this.datastoreOptions = datastoreOptions;
  }

  @Override
  @NonNull
  protected RepositoryFactorySupport createRepositoryFactory() {
    return new GcloudDatastoreRepositoryFactory(datastoreOptions);
  }

  @Override
  public void afterPropertiesSet() {
    super.afterPropertiesSet();
    Assert.state(datastoreOptions != null, "DatastoreOptions must not be null!");
  }
}
