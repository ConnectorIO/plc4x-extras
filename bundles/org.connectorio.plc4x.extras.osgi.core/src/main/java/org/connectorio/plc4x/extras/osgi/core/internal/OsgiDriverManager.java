/*
 * Copyright (C) 2019-2021 ConnectorIO Sp. z o.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */
package org.connectorio.plc4x.extras.osgi.core.internal;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.plc4x.java.DefaultPlcDriverManager;
import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.api.PlcDriver;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.connectorio.plc4x.extras.osgi.PlcDriverManager;

/**
 * Implementation of osgi aware driver manager service.
 */
public class OsgiDriverManager implements PlcDriverManager {

  private final CompoundClassLoader classLoader;

  public OsgiDriverManager(List<ClassLoader> wiring) {
    classLoader = new CompoundClassLoader(wiring);
  }

  @Override
  public Set<String> listDrivers() {
    try {
      return ClassLoaderAware.call(classLoader, () -> lookupDriverManager().listDrivers());
    } catch (PlcConnectionException e) {
      return Collections.emptySet();
    }
  }

  @Override
  public PlcDriver getDriverForUrl(String url) throws PlcConnectionException {
    return ClassLoaderAware.call(classLoader, () -> lookupDriverManager().getDriverForUrl(url));
  }

  @Override
  public PlcConnectionManager getConnectionManager() {
    try {
      return ClassLoaderAware.call(classLoader, () -> lookupDriverManager().getConnectionManager());
    } catch (PlcConnectionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public PlcDriver getDriver(String url) throws PlcConnectionException {
    return ClassLoaderAware.call(classLoader, () -> lookupDriverManager().getDriver(url));
  }

  void close() {
  }

  /**
   * Drivers are looked up at the start of PlcDriverManager so its initialization must be defered to actual
   * connection opening when all drivers are installed, and not when manager is ready.
   *
   * @return Driver manager
   */
  private org.apache.plc4x.java.api.PlcDriverManager lookupDriverManager() {
    return new DefaultPlcDriverManager(classLoader);
  }

}