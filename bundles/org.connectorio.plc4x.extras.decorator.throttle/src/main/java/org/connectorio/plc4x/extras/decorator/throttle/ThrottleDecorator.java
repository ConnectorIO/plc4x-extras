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
package org.connectorio.plc4x.extras.decorator.throttle;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.local.LocalBucket;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadRequest.Builder;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.connectorio.plc4x.extras.decorator.DecoratorReadBuilder;
import org.connectorio.plc4x.extras.decorator.DecoratorReadRequest;
import org.connectorio.plc4x.extras.decorator.DecoratorWriteBuilder;
import org.connectorio.plc4x.extras.decorator.DecoratorWriteRequest;
import org.connectorio.plc4x.extras.decorator.ReadDecorator;
import org.connectorio.plc4x.extras.decorator.WriteDecorator;

public class ThrottleDecorator implements ReadDecorator, WriteDecorator {

  private final Bucket readLimit;
  private final Bucket writeLimit;

  public ThrottleDecorator(long rate) {
    this(rate, rate);
  }

  @SuppressWarnings("all")
  public ThrottleDecorator(long readRate, long writeRate) {
    this(readRate, writeRate, TimeUnit.SECONDS);
  }

  public ThrottleDecorator(long readRate, long writeRate, TimeUnit timeUnit) {
    this.readLimit = createBucket(readRate, timeUnit, "read");
    this.writeLimit = createBucket(writeRate, timeUnit, "write");
  }

  private static LocalBucket createBucket(long readRate, TimeUnit timeUnit, String id) {
    return Bucket.builder()
      .addLimit(Bandwidth.simple(readRate, Duration.ofMillis(timeUnit.toMillis(1))).withId(id))
      .build();
  }

  @Override
  public Builder decorateRead(Builder delegate) {
    return new DecoratorReadBuilder(delegate, this);
  }

  @Override
  public PlcReadRequest decorateReadRequest(PlcReadRequest delegate) {
    return new DecoratorReadRequest(delegate, this) {
      @Override
      public CompletableFuture<? extends PlcReadResponse> execute() {
        try {
          readLimit.asBlocking().consume(1);
          return delegate.execute();
        } catch (InterruptedException e) {
          CompletableFuture<? extends PlcReadResponse> future = new CompletableFuture<>();
          future.completeExceptionally(e);
          return future;
        }
      }
    };
  }

  @Override
  public CompletableFuture<? extends PlcReadResponse> decorateReadResponse(DecoratorReadRequest request, CompletableFuture<? extends PlcReadResponse> response) {
    return response;
  }

  @Override
  public PlcWriteRequest.Builder decorateWrite(PlcWriteRequest.Builder delegate) {
    return new DecoratorWriteBuilder(delegate, this);
  }

  @Override
  public PlcWriteRequest decorateWriteRequest(PlcWriteRequest delegate) {
    return new DecoratorWriteRequest(delegate, this) {
      @Override
      public CompletableFuture<? extends PlcWriteResponse> execute() {
        try {
          writeLimit.asBlocking().consume(1);
          return delegate.execute();
        } catch (InterruptedException e) {
          CompletableFuture<? extends PlcWriteResponse> future = new CompletableFuture<>();
          future.completeExceptionally(e);
          return future;
        }
      }
    };
  }

  @Override
  public CompletableFuture<? extends PlcWriteResponse> decorateWriteResponse(DecoratorWriteRequest request, CompletableFuture<? extends PlcWriteResponse> response) {
    return response;
  }

}
