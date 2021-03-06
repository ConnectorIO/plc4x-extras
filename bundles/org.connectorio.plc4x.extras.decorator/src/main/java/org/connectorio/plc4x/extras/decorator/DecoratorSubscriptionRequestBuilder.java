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
import org.apache.plc4x.java.api.messages.PlcSubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcSubscriptionRequest.Builder;

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
  public Builder addCyclicField(String name, String fieldQuery, Duration pollingInterval) {
    delegate.addCyclicField(name, fieldQuery, pollingInterval);
    return this;
  }

  @Override
  public Builder addChangeOfStateField(String name, String fieldQuery) {
    delegate.addChangeOfStateField(name, fieldQuery);
    return this;
  }

  @Override
  public Builder addEventField(String name, String fieldQuery) {
    delegate.addEventField(name, fieldQuery);
    return this;
  }
}
