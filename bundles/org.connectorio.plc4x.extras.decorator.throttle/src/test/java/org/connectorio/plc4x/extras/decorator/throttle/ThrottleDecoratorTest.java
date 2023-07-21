/*
 * Copyright (C) 2023-2023 ConnectorIO Sp. z o.o.
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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadRequest.Builder;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.apache.plc4x.java.simulated.connection.SimulatedConnection;
import org.apache.plc4x.java.simulated.connection.SimulatedDevice;
import org.connectorio.plc4x.extras.decorator.DecoratorConnection;
import org.connectorio.plc4x.extras.test.SimulatedProtocolLogic;
import org.junit.jupiter.api.Test;

class ThrottleDecoratorTest {

  public static final String TEST_FIELD_NAME = "test";
  private final ExecutorService executor = Executors.newFixedThreadPool(2);
  private CountDownLatch latch = new CountDownLatch(1);

  @Test
  void check() throws Exception {
    SimulatedDevice device = new SimulatedDevice("fo");
    SimulatedConnection connection = new SimulatedConnection(device) {
      @Override
      public CompletableFuture<PlcReadResponse> read(PlcReadRequest readRequest) {
        try {
          latch.await();
          return super.read(readRequest);
        } catch (InterruptedException e) {
          CompletableFuture<PlcReadResponse> future = new CompletableFuture<>();
          future.completeExceptionally(e);
          return future;
        }
      }
    };

    DecoratorConnection delegate = new DecoratorConnection(connection, new ThrottleDecorator(1), null, null, null);
    connection.setProtocol(new SimulatedProtocolLogic<>(connection));
    delegate.connect();

    write(delegate.writeRequestBuilder().addTagAddress(TEST_FIELD_NAME,"STATE/test:UINT", 10));

    CompletableFuture<PlcReadResponse> r1 = bridge(createRequest(delegate).build());
    CompletableFuture<PlcReadResponse> r2 = bridge(createRequest(delegate).build());
    assertThat(r1.isDone()).isFalse();
    assertThat(r2.isDone()).isFalse();

    latch.countDown();

    Thread.sleep(100);
    assertThat(r1.isDone() || r2.isDone()).isTrue();
    assertThat(!r1.isDone() || !r2.isDone()).isTrue();
  }

  private Builder createRequest(DecoratorConnection delegate) {
    return delegate.readRequestBuilder().addTagAddress(TEST_FIELD_NAME, "STATE/test:UINT");
  }

  private PlcWriteResponse write(PlcWriteRequest.Builder writeBuilder) throws Exception {
    return writeBuilder.build().execute().get();
  }

  private CompletableFuture<PlcReadResponse> bridge(PlcReadRequest source) {
    CompletableFuture<PlcReadResponse> future = new CompletableFuture<>();
    executor.submit(new Runnable() {
      @Override
      public void run() {
        source.execute().whenComplete((result, error) -> {
          if (error != null) {
            future.completeExceptionally(error);
            return;
          }
          future.complete(result);
        });
      }
    });
    return future;
  }

}