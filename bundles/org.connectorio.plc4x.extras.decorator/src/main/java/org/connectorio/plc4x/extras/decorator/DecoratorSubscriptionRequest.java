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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.apache.plc4x.java.api.messages.PlcSubscriptionEvent;
import org.apache.plc4x.java.api.messages.PlcSubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcSubscriptionResponse;
import org.apache.plc4x.java.api.model.PlcSubscriptionTag;

public class DecoratorSubscriptionRequest implements PlcSubscriptionRequest {

  private final PlcSubscriptionRequest delegate;
  private final SubscribeDecorator decorator;

  public DecoratorSubscriptionRequest(PlcSubscriptionRequest delegate, SubscribeDecorator decorator) {
    this.delegate = delegate;
    this.decorator = decorator;
  }

  @Override
  public CompletableFuture<? extends PlcSubscriptionResponse> execute() {
    return decorator.decorateSubscribeResponse(this, delegate.execute());
  }

  @Override
  public int getNumberOfTags() {
    return delegate.getNumberOfTags();
  }

  @Override
  public LinkedHashSet<String> getTagNames() {
    return delegate.getTagNames();
  }

  @Override
  public PlcSubscriptionTag getTag(String name) {
    return delegate.getTag(name);
  }

  @Override
  public List<PlcSubscriptionTag> getTags() {
    return delegate.getTags();
  }

  @Override
  public Map<String, List<Consumer<PlcSubscriptionEvent>>> getPreRegisteredConsumers() {
    return delegate.getPreRegisteredConsumers();
  }

}
