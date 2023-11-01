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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.plc4x.java.DefaultPlcDriverManager;
import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.api.PlcDriver;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.connectorio.plc4x.extras.osgi.PlcDriverManager;

/**
 * Implementation of osgi aware driver manager service.
 */
public class OsgiDriverManager implements PlcDriverManager {

  private final PlcConnectionManager connectionManager;
  private final List<PlcDriver> drivers;

  public OsgiDriverManager() {
    this(lookup());
  }

  public OsgiDriverManager(List<PlcDriver> drivers) {
    this.connectionManager = new OsgiConnectionManager(this);
    this.drivers = drivers;
  }

  @Override
  public Set<String> listDrivers() {
    Set<String> set = new HashSet<>();
    for (PlcDriver driver : drivers) {
      set.add(driver.getProtocolCode());
    }
    return set;
  }

  @Override
  public PlcDriver getDriverForUrl(String url) throws PlcConnectionException {
    return getDriver(url);
  }

  @Override
  public PlcConnectionManager getConnectionManager() {
    return connectionManager;
  }

  @Override
  public PlcDriver getDriver(String url) throws PlcConnectionException {
    try {
      URI driverUrl = new URI(url);
      String protocol = driverUrl.getScheme();
      for (PlcDriver driver : drivers) {
        if (protocol.equals(driver.getProtocolCode())) {
          return driver;
        }
      }
    } catch (URISyntaxException e) {
      throw new PlcConnectionException("Could not determine driver", e);
    }
    throw new PlcConnectionException("Unsupported driver");
  }

  void close() {
  }

  private static List<PlcDriver> lookup() {
    DefaultPlcDriverManager driverManager = new DefaultPlcDriverManager();
    List<PlcDriver> drivers = new ArrayList<>();
    for (String driver : driverManager.listDrivers()) {
      try {
        drivers.add(driverManager.getDriver(driver));
      } catch (PlcConnectionException e) {
        throw new RuntimeException(e);
      }
    }
    return drivers;
  }

}