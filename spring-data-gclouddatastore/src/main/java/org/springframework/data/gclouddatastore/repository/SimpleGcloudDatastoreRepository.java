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
import com.google.cloud.datastore.EntityQuery;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.KeyQuery;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * @author tkob (https://github.com/tkob)
 * @author Vlad Nicoara (minor contributions)
 * @since 2/22/2018 TODO: Replace date with version number.
 */
public class SimpleGcloudDatastoreRepository<T, ID extends Serializable>
    implements GcloudDatastoreRepository<T, ID> {

  private static final Logger log = LoggerFactory.getLogger(SimpleGcloudDatastoreRepository.class);

  private static final int BUFFER_SIZE = 50;
  private final String kind;
  private final EntityInformation<T, ID> entityInformation;
  private DatastoreOptions datastoreOptions;
  private Marshaller marshaller = new Marshaller();
  private Unmarshaller unmarshaller = new Unmarshaller();

  public SimpleGcloudDatastoreRepository(
      EntityInformation<T, ID> entityInformation, DatastoreOptions datastoreOptions) {
    Assert.notNull(entityInformation, "EntityInformation must not be null!");

    this.entityInformation = entityInformation;
    this.kind = entityInformation.getJavaType().getSimpleName();
    this.datastoreOptions = datastoreOptions;
  }

  @Override
  public long count() {
    Datastore datastore = this.datastoreOptions.getService();
    QueryResults<?> results = datastore.run(getAllKeyQuery());
    long count = 0;
    while (results.hasNext()) {
      results.next();
      count++;
    }
    return count;
  }

  @Override
  public void deleteById(@NonNull ID id) {
    deleteKeys(Collections.singletonList(getKey(id)));
  }

  @Override
  public void delete(@NonNull T entity) {
    deleteAll(Collections.singletonList(entity));
  }

  @Override
  public void deleteAll(@NonNull Iterable<? extends T> entities) {
    deleteKeys(
        () -> {
          Iterator<? extends T> entityIterator = entities.iterator();
          return new Iterator<>() {
            @Override
            public boolean hasNext() {
              return entityIterator.hasNext();
            }

            @Override
            public Key next() {
              T entity = entityIterator.next();
              ID id = entityInformation.getId(entity);
              return getKey(id);
            }
          };
        });
  }

  @Override
  public void deleteAll() {
    Datastore datastore = this.datastoreOptions.getService();
    KeyQuery query = getAllKeyQuery();
    deleteKeys(() -> datastore.run(query));
  }

  @Override
  public boolean existsById(@NonNull ID id) {
    return findById(id).isPresent();
  }

  @Override
  public Iterable<T> query(Query<Entity> query) {
    Datastore datastore = this.datastoreOptions.getService();
    QueryResults<Entity> results = datastore.run(query);
    return () ->
        new Iterator<>() {
          @Override
          public boolean hasNext() {
            return results.hasNext();
          }

          @Override
          public T next() {
            try {
              T entity = entityInformation.getJavaType().getConstructor().newInstance();
              unmarshaller.unmarshalToObject(results.next(), entity);
              return entity;
            } catch (InstantiationException
                | IllegalAccessException
                | NoSuchMethodException
                | InvocationTargetException e) {
              throw new IllegalStateException();
            }
          }
        };
  }

  @Override
  @NonNull
  public Iterable<T> findAll() {
    EntityQuery.Builder queryBuilder = Query.newEntityQueryBuilder().setKind(this.kind);
    setAncestorFilter(queryBuilder);
    EntityQuery query = queryBuilder.build();
    log.debug(query.toString());

    return query(query);
  }

  @Override
  @NonNull
  public Iterable<T> findAllById(@NonNull Iterable<ID> ids) {
    return StreamSupport.stream(ids.spliterator(), true)
        .map(this::findById)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }

  @Override
  @NonNull
  public Optional<T> findById(@NonNull ID id) {
    Datastore datastore = this.datastoreOptions.getService();
    Entity entity = datastore.get(getKey(id));
    if (entity == null) {
      return Optional.empty();
    } else {
      return Optional.of(this.unmarshaller.unmarshal(entity, this.entityInformation.getJavaType()));
    }
  }

  @Override
  @NonNull
  public <S extends T> S save(@NonNull S entity) {
    saveAll(Collections.singletonList(entity));
    return entity;
  }

  @Override
  @NonNull
  public <S extends T> Iterable<S> saveAll(@NonNull Iterable<S> entities) {
    Datastore datastore = this.datastoreOptions.getService();

    List<FullEntity<? extends IncompleteKey>> buffer = new ArrayList<>();

    for (S entity : entities) {
      ID id = this.entityInformation.getId(entity);
      Key key = getKey(id);

      buffer.add(this.marshaller.toEntity(entity, key));
      if (buffer.size() >= BUFFER_SIZE) {
        datastore.put(buffer.toArray(new FullEntity[buffer.size()]));
        buffer.clear();
      }
    }
    if (buffer.size() > 0) {
      datastore.put(buffer.toArray(new FullEntity[buffer.size()]));
    }

    return entities;
  }

  private void deleteKeys(Iterable<Key> keys) {
    Datastore datastore = this.datastoreOptions.getService();

    List<Key> buffer = new ArrayList<>(BUFFER_SIZE);
    for (Key key : keys) {
      buffer.add(key);

      if (buffer.size() >= BUFFER_SIZE) {
        datastore.delete(buffer.toArray(new Key[buffer.size()]));
        buffer.clear();
      }
    }
    if (buffer.size() > 0) {
      datastore.delete(buffer.toArray(new Key[buffer.size()]));
    }
  }

  private <U> void setAncestorFilter(StructuredQuery.Builder<U> queryBuilder) {
    Datastore datastore = datastoreOptions.getService();

    Deque<PathElement> ancestors = Context.getAncestors();
    Deque<PathElement> init = new LinkedList<>();
    init.addAll(ancestors);
    PathElement last = init.pollLast();

    if (last != null) {
      KeyFactory keyFactory = datastore.newKeyFactory();
      keyFactory.addAncestors(init).setKind(last.getKind());
      Key key = last.hasId() ? keyFactory.newKey(last.getId()) : keyFactory.newKey(last.getName());
      queryBuilder.setFilter(StructuredQuery.PropertyFilter.hasAncestor(key));
    }
  }

  private KeyQuery getAllKeyQuery() {
    KeyQuery.Builder queryBuilder = Query.newKeyQueryBuilder().setKind(this.kind);
    setAncestorFilter(queryBuilder);
    KeyQuery query = queryBuilder.build();
    log.debug(query.toString());

    return query;
  }

  private Key getKey(ID id) {
    Datastore datastore = this.datastoreOptions.getService();

    KeyFactory keyFactory = datastore.newKeyFactory().setKind(this.kind);
    Iterable<PathElement> ancestors = Context.getAncestors();
    keyFactory.addAncestors(ancestors);

    Key key;
    if (id instanceof Number) {
      key = keyFactory.newKey(((Number) id).longValue());
    } else {
      key = keyFactory.newKey(id.toString());
    }
    return key;
  }
}
