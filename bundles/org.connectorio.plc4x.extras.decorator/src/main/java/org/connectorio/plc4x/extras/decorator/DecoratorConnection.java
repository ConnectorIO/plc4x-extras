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
import org.apache.plc4x.java.api.exceptions.PlcInvalidFieldException;
import org.apache.plc4x.java.api.messages.PlcBrowseRequest;
import org.apache.plc4x.java.api.messages.PlcReadRequest.Builder;
import org.apache.plc4x.java.api.messages.PlcSubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcUnsubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.metadata.PlcConnectionMetadata;
import org.apache.plc4x.java.api.model.PlcField;
import org.connectorio.plc4x.DelegatingConnection;
import org.connectorio.plc4x.extras.decorator.noop.NoopReadDecorator;
import org.connectorio.plc4x.extras.decorator.noop.NoopSubscribeDecorator;
import org.connectorio.plc4x.extras.decorator.noop.NoopUnsubscribeDecorator;
import org.connectorio.plc4x.extras.decorator.noop.NoopWriteDecorator;

public class DecoratorConnection implements DelegatingConnection {

  private final PlcConnection delegate;

  private final ReadDecorator readDecorator;
  private final WriteDecorator writeDecorator;
  private final SubscribeDecorator subscribeDecorator;
  private final UnsubscribeDecorator unsubscribeDecorator;

  public DecoratorConnection(PlcConnection delegate, ReadDecorator readDecorator, WriteDecorator writeDecorator,
    SubscribeDecorator subscribeDecorator, UnsubscribeDecorator unsubscribeDecorator) {
    this.delegate = delegate;
    this.readDecorator = readDecorator == null ? new NoopReadDecorator() : readDecorator;
    this.writeDecorator = writeDecorator == null ? new NoopWriteDecorator() : writeDecorator;
    this.subscribeDecorator = subscribeDecorator == null ? new NoopSubscribeDecorator() : subscribeDecorator;
    this.unsubscribeDecorator = unsubscribeDecorator == null ? new NoopUnsubscribeDecorator() : unsubscribeDecorator;
  }

  @Override
  public void connect() throws PlcConnectionException {
    delegate.connect();
  }

  @Override
  public boolean isConnected() {
    return delegate.isConnected();
  }

  @Override
  public void close() throws Exception {
    delegate.close();
  }

  @Override
  @Deprecated
  public PlcField prepareField(String fieldQuery) throws PlcInvalidFieldException {
    return delegate.prepareField(fieldQuery);
  }

  @Override
  public PlcConnectionMetadata getMetadata() {
    return delegate.getMetadata();
  }

  @Override
  public CompletableFuture<Void> ping() {
    return delegate.ping();
  }

  @Override
  public Builder readRequestBuilder() {
    return readDecorator.decorateRead(delegate.readRequestBuilder());
  }

  @Override
  public PlcWriteRequest.Builder writeRequestBuilder() {
    return writeDecorator.decorateWrite(delegate.writeRequestBuilder());
  }

  @Override
  public PlcSubscriptionRequest.Builder subscriptionRequestBuilder() {
    return new DecoratorSubscriptionRequestBuilder(delegate.subscriptionRequestBuilder(), subscribeDecorator);
  }

  @Override
  public PlcUnsubscriptionRequest.Builder unsubscriptionRequestBuilder() {
    return new DecoratorUnsubscriptionRequestBuilder(delegate.unsubscriptionRequestBuilder(), unsubscribeDecorator);
  }

  @Override
  public PlcBrowseRequest.Builder browseRequestBuilder() {
    return delegate.browseRequestBuilder();
  }

  @Override
  public PlcConnection getDelegate() {
    return delegate;
  }

  @Override
  public <T extends PlcConnection> T cast(Class<T> type) {
    if (type.isInstance(delegate)) {
      return type.cast(delegate);
    }

    if (delegate instanceof DelegatingConnection) {
      return ((DelegatingConnection) delegate).cast(type);
    }

    return null;
  }
}
