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

package org.springframework.data.gclouddatastore.repository.query;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.StructuredQuery;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.StreamSupport;
import org.springframework.data.domain.Sort;
import org.springframework.data.gclouddatastore.repository.Context;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.repository.query.ParameterAccessor;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.lang.NonNull;

/**
 * @author tkob (https://github.com/tkob)
 * @author Vlad Nicoara (minor contributions)
 * @since 2/22/2018 TODO: Replace date with version number.
 */
public class GcloudDatastoreQueryCreator
    extends AbstractQueryCreator<StructuredQuery.Builder<Entity>, StructuredQuery.Filter> {

  private DatastoreOptions datastoreOptions;

  public GcloudDatastoreQueryCreator(
      PartTree tree, ParameterAccessor accessor, DatastoreOptions datastoreOptions) {
    super(tree, accessor);

    this.datastoreOptions = datastoreOptions;
  }

  @Override
  @NonNull
  protected StructuredQuery.Filter create(@NonNull Part part,
      @NonNull Iterator<Object> parameters) {
    if (part.getType().getKeywords().contains("Equals")) {
      List<String> segments = new ArrayList<>();
      for (PropertyPath propertyPath : part.getProperty()) {
        segments.add(propertyPath.getSegment());
      }
      String property = String.join(".", segments);

      Object value = parameters.next();
      if (value == null) {
        return StructuredQuery.PropertyFilter.isNull(property);
      } else if (value instanceof Boolean) {
        return StructuredQuery.PropertyFilter.eq(property, (Boolean) value);
      } else if (value instanceof Double || value instanceof Float) {
        return StructuredQuery.PropertyFilter.eq(property, ((Number) value).doubleValue());
      } else if (value instanceof Number) {
        return StructuredQuery.PropertyFilter.eq(property, ((Number) value).longValue());
      } else if (value instanceof CharSequence) {
        return StructuredQuery.PropertyFilter.eq(property, value.toString());
      } else {
        throw new UnsupportedOperationException(
            "Value type not supported: " + value + " : " + value.getClass());
      }
    } else {
      throw new UnsupportedOperationException("Part type not supported: " + part.getType());
    }
  }

  @Override
  @NonNull
  protected StructuredQuery.Filter and(
      @NonNull Part part, @NonNull StructuredQuery.Filter filter,
      @NonNull Iterator<Object> parameters) {

    return StructuredQuery.CompositeFilter.and(filter, create(part, parameters));
  }

  @Override
  @NonNull
  protected StructuredQuery.Filter or(
      @NonNull StructuredQuery.Filter filter1, @NonNull StructuredQuery.Filter filter2) {

    throw new UnsupportedOperationException("Or operator in query method not supported");
  }

  @Override
  @NonNull
  protected StructuredQuery.Builder<Entity> complete(StructuredQuery.Filter filter,
      @NonNull Sort sort) {

    StructuredQuery.OrderBy[] orderBy =
        StreamSupport.stream(sort.spliterator(), false)
            .map(
                order ->
                    order.isAscending()
                        ? StructuredQuery.OrderBy.asc(order.getProperty())
                        : StructuredQuery.OrderBy.desc(order.getProperty()))
            .toArray(StructuredQuery.OrderBy[]::new);
    if (orderBy.length == 0) {
      return Query.newEntityQueryBuilder().setFilter(setAncestorFilter(filter));
    } else {
      return Query.newEntityQueryBuilder()
          .addOrderBy(orderBy[0], orderBy)
          .setFilter(setAncestorFilter(filter));
    }
  }

  private StructuredQuery.Filter setAncestorFilter(StructuredQuery.Filter filter) {
    Datastore datastore = datastoreOptions.getService();

    Deque<PathElement> ancestors = Context.getAncestors();
    Deque<PathElement> init = new LinkedList<>();
    init.addAll(ancestors);
    PathElement last = init.pollLast();

    if (last == null) {
      return filter;
    } else {
      KeyFactory keyFactory = datastore.newKeyFactory();
      keyFactory.addAncestors(init).setKind(last.getKind());
      Key key = last.hasId() ? keyFactory.newKey(last.getId()) : keyFactory.newKey(last.getName());
      StructuredQuery.Filter ancestorFilter = StructuredQuery.PropertyFilter.hasAncestor(key);
      if (filter == null) {
        return ancestorFilter;
      } else {
        return StructuredQuery.CompositeFilter.and(filter, ancestorFilter);
      }
    }
  }
}
