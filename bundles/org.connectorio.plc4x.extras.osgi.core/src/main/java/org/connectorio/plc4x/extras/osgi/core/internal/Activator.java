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

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.plc4x.java.api.PlcDriver;
import org.apache.plc4x.java.spi.transport.Transport;
import org.connectorio.plc4x.extras.osgi.PlcDriverManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Activator to start tracking of plc drivers and propagate them to driver manager who can utilize them.
 */
public class Activator implements BundleActivator, ServiceFactory<PlcDriverManager> {

  private ServiceRegistration<?> registration;
  private ServiceTracker<PlcDriver, PlcDriver> drivers;
  private final List<PlcDriver> driverList = new CopyOnWriteArrayList<>();

  @Override
  public void start(BundleContext context) throws Exception {
    drivers = new ServiceTracker<>(context, PlcDriver.class, new DriverTracker(context, driverList));
    drivers.open();

    registration = context.registerService(new String[] {
      PlcDriverManager.class.getName(),
      org.apache.plc4x.java.api.PlcDriverManager.class.getName()
    }, new OsgiDriverManager(driverList), new Hashtable<>());
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    registration.unregister();

    drivers.close();
    driverList.clear();
  }

  @Override
  public PlcDriverManager getService(Bundle bundle, ServiceRegistration<PlcDriverManager> registration) {
    return new OsgiDriverManager(driverList);
  }

  @Override
  public void ungetService(Bundle bundle, ServiceRegistration<PlcDriverManager> registration, PlcDriverManager service) {
    if (service instanceof OsgiDriverManager) {
      ((OsgiDriverManager) service).close();
    }
  }
}
