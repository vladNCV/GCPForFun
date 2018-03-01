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

package org.springframework.data.gclouddatastore.repository;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.data.gclouddatastore.repository.query.GcloudDatastoreQueryCreator;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.EvaluationContextProvider;
import org.springframework.data.repository.query.ParametersParameterAccessor;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.ResultProcessor;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.lang.NonNull;

/**
 * @author tkob (https://github.com/tkob)
 * @author Vlad Nicoara (minor contributions)
 * @since 2/22/2018 TODO: Replace date with version number.
 */
public class GcloudDatastoreRepositoryFactory extends RepositoryFactorySupport {

  private DatastoreOptions datastoreOptions;

  public GcloudDatastoreRepositoryFactory(DatastoreOptions datastoreOptions) {
    this.datastoreOptions = datastoreOptions;
  }

  @Override
  @NonNull
  public Class<?> getRepositoryBaseClass(@NonNull RepositoryMetadata metadata) {
    return SimpleGcloudDatastoreRepository.class;
  }

  @Override
  @NonNull
  public <T, ID> EntityInformation<T, ID> getEntityInformation(@NonNull Class<T> domainClass) {
    return new GcloudDatastoreEntityInformation<>(domainClass);
  }

  @Override
  @NonNull
  public Object getTargetRepository(@NonNull RepositoryInformation information) {
    EntityInformation<?, Serializable> entityInformation =
        getEntityInformation(information.getDomainType());
    return getTargetRepositoryViaReflection(information, entityInformation, this.datastoreOptions);
  }

  @Override
  @NonNull
  public Optional<QueryLookupStrategy> getQueryLookupStrategy(
      Key key, EvaluationContextProvider evaluationContextProvider) {

    return Optional.of(
        (method, metadata, factory, namedQueries) -> {
          QueryMethod queryMethod = new QueryMethod(method, metadata, factory);

          ResultProcessor resultProcessor = queryMethod.getResultProcessor();
          Class<?> domainType = resultProcessor.getReturnedType().getDomainType();
          PartTree tree = new PartTree(method.getName(), domainType);
          return new RepositoryQuery() {
            @Override
            public Object execute(@NonNull Object[] parameters) {
              GcloudDatastoreQueryCreator queryCreator =
                  new GcloudDatastoreQueryCreator(
                      tree,
                      new ParametersParameterAccessor(queryMethod.getParameters(), parameters),
                      datastoreOptions);
              StructuredQuery.Builder<Entity> queryBuilder = queryCreator.createQuery();
              queryBuilder.setKind(domainType.getSimpleName());

              Unmarshaller unmarshaller = new Unmarshaller();
              Datastore datastore = datastoreOptions.getService();
              QueryResults<Entity> results = datastore.run(queryBuilder.build());

              try {
                if (queryMethod.isCollectionQuery()) {
                  List<Object> result = new ArrayList<>();
                  while (results.hasNext()) {
                    Object entity = domainType.getConstructor().newInstance();
                    unmarshaller.unmarshalToObject(results.next(), entity);
                    result.add(entity);
                  }
                  return resultProcessor.processResult(result);
                } else if (queryMethod.isStreamQuery()) {

                  Iterable<Object> iterable =
                      () ->
                          new Iterator<>() {
                            @Override
                            public boolean hasNext() {
                              return results.hasNext();
                            }

                            @Override
                            public Object next() {
                              try {
                                Object entity = domainType.getConstructor().newInstance();
                                unmarshaller.unmarshalToObject(results.next(), entity);
                                return entity;
                              } catch (InstantiationException
                                  | IllegalAccessException
                                  | NoSuchMethodException
                                  | InvocationTargetException e) {
                                throw new IllegalStateException(e);
                              }
                            }
                          };
                  Stream<Object> result = StreamSupport.stream(iterable.spliterator(), false);
                  return resultProcessor.processResult(result);
                } else if (queryMethod.isQueryForEntity()) {
                  Object result;
                  if (!results.hasNext()) {
                    result = null;
                  } else {
                    result = domainType.getConstructor().newInstance();
                    unmarshaller.unmarshalToObject(results.next(), result);
                  }
                  return resultProcessor.processResult(result);
                }
                throw new UnsupportedOperationException("Query method not supported.");
              } catch (InstantiationException
                  | IllegalAccessException
                  | NoSuchMethodException
                  | InvocationTargetException e) {
                throw new IllegalStateException(e);
              }
            }

            @Override
            @NonNull
            public QueryMethod getQueryMethod() {
              return queryMethod;
            }
          };
        });
  }
}
