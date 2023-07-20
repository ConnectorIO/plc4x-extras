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

import java.time.Duration;
import java.util.function.Consumer;
import org.apache.plc4x.java.api.messages.PlcSubscriptionEvent;
import org.apache.plc4x.java.api.messages.PlcSubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcSubscriptionRequest.Builder;
import org.apache.plc4x.java.api.model.PlcTag;

public class DecoratorSubscriptionRequestBuilder implements Builder {

  private final Builder delegate;
  private final SubscribeDecorator decorator;

  public DecoratorSubscriptionRequestBuilder(Builder delegate, SubscribeDecorator decorator) {
    this.delegate = delegate;
    this.decorator = decorator;
  }

  @Override
  public PlcSubscriptionRequest build() {
    return decorator.decorateSubscribeRequest(delegate.build());
  }


  @Override
  public Builder addCyclicTagAddress(String name, String tagAddress, Duration pollingInterval) {
    delegate.addCyclicTagAddress(name, tagAddress, pollingInterval);
    return this;
  }

  @Override
  public Builder addCyclicTag(String name, PlcTag tag, Duration pollingInterval) {
    delegate.addCyclicTag(name, tag, pollingInterval);
    return this;
  }

  @Override
  public Builder addChangeOfStateTagAddress(String name, String tagAddress) {
    delegate.addChangeOfStateTagAddress(name, tagAddress);
    return this;
  }

  @Override
  public Builder addChangeOfStateTag(String name, PlcTag tag) {
    delegate.addChangeOfStateTag(name, tag);
    return this;
  }

  @Override
  public Builder addEventTagAddress(String name, String tagAddress) {
    delegate.addEventTagAddress(name, tagAddress);
    return this;
  }

  @Override
  public Builder addEventTag(String name, PlcTag tag) {
    delegate.addEventTag(name, tag);
    return this;
  }

  @Override
  public Builder addPreRegisteredConsumer(String name, Consumer<PlcSubscriptionEvent> preRegisteredConsumer) {
    delegate.addPreRegisteredConsumer(name, preRegisteredConsumer);
    return this;
  }
}
