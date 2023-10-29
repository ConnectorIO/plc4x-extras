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

import java.util.List;
import org.apache.plc4x.java.api.PlcDriver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * A driver tracker which registers its class loaders as source for class lookups for PlcDriverManager.
 */
public class DriverTracker implements ServiceTrackerCustomizer<PlcDriver, PlcDriver> {

  private final BundleContext context;
  private final List<PlcDriver> drivers;

  public DriverTracker(BundleContext context, List<PlcDriver> driverList) {
    this.context = context;
    this.drivers = driverList;
  }

  @Override
  public PlcDriver addingService(ServiceReference<PlcDriver> reference) {
    PlcDriver service = context.getService(reference);
    drivers.add(service);
    return service;
  }

  @Override
  public void modifiedService(ServiceReference<PlcDriver> reference, PlcDriver service) {

  }

  @Override
  public void removedService(ServiceReference<PlcDriver> reference, PlcDriver service) {
    context.ungetService(reference);
    drivers.remove(service);
  }

}
