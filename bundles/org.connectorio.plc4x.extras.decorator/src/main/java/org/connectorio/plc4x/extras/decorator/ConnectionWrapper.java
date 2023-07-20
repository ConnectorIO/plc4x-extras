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
package org.connectorio.plc4x.extras.decorator;

import java.util.concurrent.CompletableFuture;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.exceptions.PlcInvalidTagException;
import org.apache.plc4x.java.api.messages.PlcBrowseRequest;
import org.apache.plc4x.java.api.messages.PlcReadRequest.Builder;
import org.apache.plc4x.java.api.messages.PlcSubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcUnsubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.metadata.PlcConnectionMetadata;
import org.apache.plc4x.java.api.model.PlcTag;
import org.connectorio.plc4x.DelegatingConnection;

public class ConnectionWrapper implements DelegatingConnection {

  private final PlcConnection connection;

  public ConnectionWrapper(PlcConnection connection) {
    this.connection = connection;
  }

  @Override
  public void connect() throws PlcConnectionException {
    connection.connect();
  }

  @Override
  public boolean isConnected() {
    return connection.isConnected();
  }

  @Override
  public void close() throws Exception {
    connection.close();
  }

  @Override
  public PlcConnectionMetadata getMetadata() {
    return connection.getMetadata();
  }

  @Override
  public PlcTag parseTagAddress(String tagAddress) throws PlcInvalidTagException {
    return connection.parseTagAddress(tagAddress);
  }

  @Override
  public CompletableFuture<Void> ping() {
    return connection.ping();
  }

  @Override
  public Builder readRequestBuilder() {
    return connection.readRequestBuilder();
  }

  @Override
  public PlcWriteRequest.Builder writeRequestBuilder() {
    return connection.writeRequestBuilder();
  }

  @Override
  public PlcSubscriptionRequest.Builder subscriptionRequestBuilder() {
    return connection.subscriptionRequestBuilder();
  }

  @Override
  public PlcUnsubscriptionRequest.Builder unsubscriptionRequestBuilder() {
    return connection.unsubscriptionRequestBuilder();
  }

  @Override
  public PlcBrowseRequest.Builder browseRequestBuilder() {
    return connection.browseRequestBuilder();
  }

  @Override
  public PlcConnection getDelegate() {
    return connection;
  }

  @Override
  public <T extends PlcConnection> T cast(Class<T> type) {
    if (type.isAssignableFrom(getClass())) {
      return type.cast(this);
    }

    if (type.isInstance(connection)) {
      return type.cast(connection);
    }

    return null;
  }

}
