/*
 * Copyright 2000-2009 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package codeOrchestra.util.process;

import codeOrchestra.colt.core.logging.Logger;
import codeOrchestra.util.metadata.ParamsGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ParametersList implements Cloneable {

  private static final Logger LOG = Logger.getLogger("#com.intellij.execution.configurations.ParametersList");

  private List<String> myParameters = new ArrayList<>();
  private List<ParamsGroup> myGroups = new ArrayList<>();

  public String[] getArray() {
    List<String> list = getList();
    return list.toArray(new String[list.size()]);
  }

  public void add(final String parameter) {
    myParameters.add(parameter);
  }

  public List<String> getParameters() {
    return Collections.unmodifiableList(myParameters);
  }

  public void addAt(final int index, final String parameter) {
    myParameters.add(index, parameter);
  }

  public List<String> getList() {
    if (myGroups.isEmpty()) {
      return Collections.unmodifiableList(myParameters);
    }

    final List<String> params = new ArrayList<>();

    // params
    params.addAll(myParameters);

    // recursively add groups
    for (ParamsGroup group : myGroups) {
      params.addAll(group.getParameters());
    }
    return Collections.unmodifiableList(params);
  }

  public void prepend(final String parameter) {
    addAt(0, parameter);
  }

  public void add(final String name, final String value) {
    add(name);
    add(value);
  }

  public void addAll(final String[] parameters) {
    Collections.addAll(myParameters, parameters);
  }

  public void addAll(final List<String> parameters) {
    myParameters.addAll(parameters);
  }

  public ParametersList clone() {
    try {
      final ParametersList clone = (ParametersList) super.clone();
      clone.myParameters = new ArrayList<>(myParameters);
      clone.myGroups = new ArrayList<>(myGroups.size() + 1);
      clone.myGroups.addAll(myGroups.stream().map(ParamsGroup::clone).collect(Collectors.toList()));
      return clone;
    } catch (CloneNotSupportedException e) {
      LOG.error(e);
      return null;
    }
  }
}