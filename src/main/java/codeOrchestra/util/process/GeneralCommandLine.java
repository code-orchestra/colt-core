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

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class GeneralCommandLine {
  private Map<String, String> myEnvParams;
  private String myExePath = null;
  private File myWorkDirectory = null;
  private ParametersList myProgramParams = new ParametersList();
  private Charset myCharset = getDefaultSystemCharset();

  public static Charset getDefaultSystemCharset() {
    Charset charset = null;
    try {
      charset = Charset.forName(System.getProperty("file.encoding"));
    } catch (Exception e) {
      // Null is OK here.
    }

    return charset;
  }
  
  public void setExePath(final String exePath) {
    myExePath = exePath.trim();
  }

  public void setWorkDirectory(final String path) {
    setWorkingDirectory(path != null? new File(path) : null);
  }

  public void setWorkingDirectory(final File workingDirectory) {
    myWorkDirectory = workingDirectory;
  }

  public void setCharset(Charset charset) {
    myCharset = charset;
  }

  public void addParameter(final String parameter) {
    myProgramParams.add(parameter);
  }

  public Charset getCharset() {
    return myCharset;
  }

  public GeneralCommandLine clone() {
    final GeneralCommandLine clone = new GeneralCommandLine();
    clone.myCharset = myCharset;
    clone.myExePath = myExePath;
    clone.myWorkDirectory = myWorkDirectory;
    clone.myProgramParams = myProgramParams.clone();
    clone.myEnvParams = myEnvParams != null ? new HashMap<>(myEnvParams) : null;
    return clone;
  }

}