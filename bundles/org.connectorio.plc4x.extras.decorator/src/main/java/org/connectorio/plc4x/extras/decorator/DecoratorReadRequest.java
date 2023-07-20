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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.model.PlcTag;

public class DecoratorReadRequest implements PlcReadRequest {

  private final PlcReadRequest delegate;
  private final ReadDecorator decorator;

  public DecoratorReadRequest(PlcReadRequest delegate, ReadDecorator decorator) {
    this.delegate = delegate;
    this.decorator = decorator;
  }

  @Override
  public CompletableFuture<? extends PlcReadResponse> execute() {
    return decorator.decorateReadResponse(this, delegate.execute());
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
  public PlcTag getTag(String name) {
    return delegate.getTag(name);
  }

  @Override
  public List<PlcTag> getTags() {
    return delegate.getTags();
  }

  public String toString() {
    Map<String, PlcTag> fields = new HashMap<>();
    for (String name : getTagNames()) {
      if (fields.put(name, getTag(name)) != null) {
        throw new IllegalStateException("Duplicate key");
      }
    }
    return "DecoratorReadRequest [" + delegate + ": " + fields + "]";
  }

}
