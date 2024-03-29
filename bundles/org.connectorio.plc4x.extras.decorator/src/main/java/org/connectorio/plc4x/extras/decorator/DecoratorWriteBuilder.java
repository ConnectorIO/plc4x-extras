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

import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.messages.PlcWriteRequest.Builder;
import org.apache.plc4x.java.api.model.PlcTag;

public class DecoratorWriteBuilder implements PlcWriteRequest.Builder {

  private final Builder delegate;
  private final WriteDecorator decorator;

  public DecoratorWriteBuilder(Builder delegate, WriteDecorator decorator) {
    this.delegate = delegate;
    this.decorator = decorator;
  }

  @Override
  public PlcWriteRequest build() {
    return decorator.decorateWriteRequest(delegate.build());
  }

  @Override
  public Builder addTagAddress(String name, String tagAddress, Object... values) {
    delegate.addTagAddress(name, tagAddress, values);
    return this;
  }

  @Override
  public Builder addTag(String name, PlcTag tag, Object... values) {
    delegate.addTag(name, tag, values);
    return this;
  }

}
