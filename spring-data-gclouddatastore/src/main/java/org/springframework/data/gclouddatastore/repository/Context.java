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

import com.google.cloud.datastore.PathElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author tkob (https://github.com/tkob)
 * @author Vlad Nicoara (minor contributions)
 * @since 2/22/2018 TODO: Replace date with version number.
 */
public class Context implements AutoCloseable {

  private static ThreadLocal<Deque<PathElement>> localAncestorsStack =
      ThreadLocal.withInitial(LinkedList::new);

  private int count;

  private Context(int count) {
    this.count = count;
  }

  public static Context with(PathElement ancestor, PathElement... other) {
    List<PathElement> ancestors = new ArrayList<>(other.length + 1);
    ancestors.add(ancestor);
    ancestors.addAll(Arrays.asList(other));
    return with(ancestors);
  }

  public static Context with(Iterable<PathElement> ancestors) {
    Deque<PathElement> ancestorsStack = getAncestors();
    int count = 0;
    for (PathElement ancestor : ancestors) {
      ancestorsStack.addLast(ancestor);
      count++;
    }
    return new Context(count);
  }

  public static Deque<PathElement> getAncestors() {
    return localAncestorsStack.get();
  }

  @Override
  public void close() {
    Deque<PathElement> ancestors = getAncestors();
    for (int i = 0; i < this.count; i++) {
      ancestors.removeLast();
    }
  }
}
