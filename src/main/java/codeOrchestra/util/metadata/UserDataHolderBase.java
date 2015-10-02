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
package codeOrchestra.util.metadata;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UserDataHolderBase implements UserDataHolderEx, Cloneable {
  
  private static final Object MAP_LOCK = new Object();
  private static final String COPYABLE_USER_MAP_KEY = "COPYABLE_USER_MAP_KEY";

  private volatile ConcurrentMap<String, Object> myUserMap = null;

  protected Object clone() throws CloneNotSupportedException {
    try {
      UserDataHolderBase clone = (UserDataHolderBase)super.clone();
      clone.myUserMap = null;
      copyCopyableDataTo(clone);
      return clone;
    }
    catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

  public <T> T getUserData(String key) {
    final Map<String, Object> map = myUserMap;
    return map == null ? null : (T)map.get(key);
  }

  public <T> void putUserData(String key, T value) {
    Map<String, Object> map = getOrCreateMap();

    if (value == null) {
      map.remove(key);
      if (map.isEmpty()) {
        synchronized (MAP_LOCK) {
          if (myUserMap != null && myUserMap.isEmpty()) {
            myUserMap = null;
          }
        }
      }
    }
    else {
      map.put(key, value);
    }
  }

  protected ConcurrentMap<String, Object> createDataMap(int initialCapacity) {
    return new ConcurrentHashMap<>(initialCapacity);
  }

  private ConcurrentMap<String, Object> getOrCreateMap() {
    ConcurrentMap<String, Object> map = myUserMap;
    if (map == null) {
      synchronized (MAP_LOCK) {
        map = myUserMap;
        if (map == null) {
          myUserMap = map = createDataMap(2);
        }
      }
    }
    return map;
  }

  public <T> boolean replace(String key, T oldValue, T newValue) {
    ConcurrentMap<String, Object> map = getOrCreateMap();
    if (oldValue == null) {
      return newValue == null || map.putIfAbsent(key, newValue) == null;
    }
    if (newValue == null) {
      return map.remove(key, oldValue);
    }
    return map.replace(key, oldValue, newValue);
  }

  public <T> T putUserDataIfAbsent(final String key, final T value) {
    T prev = (T)getOrCreateMap().putIfAbsent(key, value);
    return prev == null ? value : prev;
  }

  public void copyCopyableDataTo(UserDataHolderBase clone) {
    Map<String, Object> copyableMap = getUserData(COPYABLE_USER_MAP_KEY);
    if (copyableMap != null) {
      ConcurrentMap<String, Object> copy = createDataMap(copyableMap.size());
      copy.putAll(copyableMap);
      copyableMap = copy;
    }
    clone.putUserData(COPYABLE_USER_MAP_KEY, copyableMap);
  }

}