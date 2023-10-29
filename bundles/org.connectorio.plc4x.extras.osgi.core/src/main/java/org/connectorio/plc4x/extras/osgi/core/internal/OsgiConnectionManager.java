/*
 * Copyright (C) 2023-2023 ConnectorIO Sp. z o.o.
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

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.api.PlcDriver;
import org.apache.plc4x.java.api.authentication.PlcAuthentication;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.connectorio.plc4x.extras.osgi.PlcDriverManager;

/**
 * Connection manager which rely on {@link PlcDriverManager} interface to lookup available drivers.
 */
public class OsgiConnectionManager implements PlcConnectionManager {

  private final PlcDriverManager driverManager;

  public OsgiConnectionManager(PlcDriverManager driverManager) {
    this.driverManager = driverManager;
  }

  @Override
  public PlcConnection getConnection(String url) throws PlcConnectionException {
    return getConnection(url, null);
  }

  @Override
  public PlcConnection getConnection(String url, PlcAuthentication authentication) throws PlcConnectionException {
    PlcDriver driver = driverManager.getDriverForUrl(url);
    return driver.getConnection(url, authentication);
  }

}
