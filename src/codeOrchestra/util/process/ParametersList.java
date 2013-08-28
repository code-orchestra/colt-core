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
import codeOrchestra.util.StringUtils;

import java.util.*;

public class ParametersList implements Cloneable{
  
  private static final Logger LOG = Logger.getLogger("#com.intellij.execution.configurations.ParametersList");
  
  private List<String> myParameters = new ArrayList<>();
  private Map<String, String> myMacroMap = null;
  private List<ParamsGroup> myGroups = new ArrayList<>();

  public boolean hasParameter(final String param) {
    return myParameters.contains(param);
  }

  public boolean hasProperty(final String name) {
    for (String parameter : myParameters) {
      if (StringUtils.startsWithConcatenationOf(parameter, "-D" + name, "=")) return true;
    }
    return false;
  }

  public String getPropertyValue(final String name) {
    for (String parameter : myParameters) {
      String prefix = "-D" + name + "=";
      if (parameter.startsWith(prefix)) {
        return parameter.substring(prefix.length());
      }
    }
    return null;
  }

  public String getParametersString() {
    final StringBuilder buffer = new StringBuilder();
    final String separator = " ";
    for (final String param : myParameters) {
      buffer.append(separator);
      buffer.append(GeneralCommandLine.quote(param));
    }
    for (ParamsGroup paramsGroup : myGroups) {
      // params group parameters string already contains a separator
      buffer.append(paramsGroup.getParametersList().getParametersString());
    }
    return buffer.toString();
  }

  public String[] getArray() {
    List<String> list = getList();
    return list.toArray(new String[list.size()]);
  }

  public void addParametersString(final String parameters) {
    if (parameters != null) {
      final String[] parms = parse(parameters);
      for (String parm : parms) {
        add(parm);
      }
    }
  }

  public void add(final String parameter) {
    myParameters.add(parameter);
  }

  public ParamsGroup addParamsGroup(final String groupId) {
    return addParamsGroup(new ParamsGroup(groupId));
  }

  public ParamsGroup addParamsGroup(final ParamsGroup group) {
    myGroups.add(group);
    return group;
  }

  public ParamsGroup addParamsGroupAt(final int index,
                                      final ParamsGroup group) {
    myGroups.add(index, group);
    return group;
  }

  public ParamsGroup addParamsGroupAt(final int index,
                                      final String groupId) {
    final ParamsGroup group = new ParamsGroup(groupId);
    myGroups.add(index, group);
    return group;
  }

  public int getParamsGroupsCount() {
    return myGroups.size();
  }

  public List<String> getParameters() {
    return Collections.unmodifiableList(myParameters);
  }

  public List<ParamsGroup> getParamsGroups() {
    return Collections.unmodifiableList(myGroups);
  }

  public ParamsGroup getParamsGroupAt(final int index) {
    return myGroups.get(index);
  }

  public ParamsGroup getParamsGroup(final String name) {
    for (ParamsGroup group : myGroups) {
      if (name.equals(group.getId())) return group;
    }
    return null;
  }

  public ParamsGroup removeParamsGroup(final int index) {
    return myGroups.remove(index);
  }

  public void addAt(final int index, final String parameter) {
    myParameters.add(index, parameter);
  }

  public void defineProperty(final String propertyName, final String propertyValue) {
    //noinspection HardCodedStringLiteral
    myParameters.add("-D" + propertyName + "=" + propertyValue);
  }

  public void replaceOrAppend(final String parameterPrefix, final String replacement) {
    replaceOrAdd(parameterPrefix, replacement, myParameters.size());
  }

  private void replaceOrAdd(final String parameterPrefix, final String replacement, final int position) {
    for (ListIterator<String> iterator = myParameters.listIterator(); iterator.hasNext();) {
      final String param = iterator.next();
      if (param.startsWith(parameterPrefix)) {
        if ("".equals(replacement)) {
          iterator.remove();
        }
        else {
          iterator.set(replacement);
        }
        return;
      }
    }
    if(!"".equals(replacement)) {
      myParameters.add(position, replacement);
    }
  }

  public void replaceOrPrepend(final String parameter, final String replacement) {
    replaceOrAdd(parameter, replacement, 0);
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
    for (String parameter : parameters) {
      myParameters.add(parameter);
    }
  }

  public void addAll(final List<String> parameters) {
    myParameters.addAll(parameters);
  }

  public ParametersList clone() {
    try {
      final ParametersList clone = (ParametersList)super.clone();
      clone.myParameters = new ArrayList<>(myParameters);
      clone.myGroups = new ArrayList<>(myGroups.size() + 1);
      for (ParamsGroup group : myGroups) {
        clone.myGroups.add(group.clone());
      }
      return clone;
    }
    catch (CloneNotSupportedException e) {
      LOG.error(e);
      return null;
    }
  }

  public static String[] parse(final String string){
    return new ParametersTokenizer(string).execute();
  }

  private static class ParametersTokenizer {
    private final String myParamsString;
    private final List<String> myArray = new ArrayList<>();
    private final StringBuffer myBuffer = new StringBuffer(128);
    private boolean myTokenStarted = false;
    private boolean myUnquotedSlash = false;

    public ParametersTokenizer(final String parmsString) {
      myParamsString = parmsString;
    }

    public String[] execute() {
      boolean inQuotes = false;

      // \" sequence is turned to " inside ""
      boolean wasEscaped = false;

      for (int i = 0; i < myParamsString.length(); i++) {
        final char c = myParamsString.charAt(i);

        if (inQuotes) {
          assert !myUnquotedSlash;
          if (wasEscaped) {
            //if (c != '"') append('\\');
            append(c);
            wasEscaped = false;
          }
          else if (c == '"') {
            inQuotes = false;
          }
          else if (c == '\\') {
            myTokenStarted = true;
            append(c);
            wasEscaped = true;
          }
          else {
            append(c);
          }
        }
        else {
          inQuotes = processNotQuoted(c);
        }
      }
      tokenFinished();
      return myArray.toArray(new String[myArray.size()]);
    }

    private boolean processNotQuoted(final char c) {
      if (c == '"') {
        if (myUnquotedSlash) {
          append(c);
          myUnquotedSlash = false;
          return false;
        }
        myTokenStarted = true;
        return true;
      }
      else if (c == ' ') {
        tokenFinished();
      }
      else if (c == '\\') {
        myUnquotedSlash = true;
        append(c);
        return false;
      }
      else {
        append(c);
      }
      myUnquotedSlash = false;
      return false;
    }

    private void append(final char nextChar) {
      myBuffer.append(nextChar);
      myTokenStarted = true;
    }

    private void tokenFinished() {
      if (myTokenStarted) {
        final String token = myBuffer.length() == 0 ? "\"\"" : myBuffer.toString();
        myArray.add(token);
      }
      myBuffer.setLength(0);
      myTokenStarted = false;
    }
  }
}
