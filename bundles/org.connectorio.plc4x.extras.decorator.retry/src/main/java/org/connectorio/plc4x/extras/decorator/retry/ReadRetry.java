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
package org.connectorio.plc4x.extras.decorator.retry;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadRetry implements BiConsumer<PlcReadResponse, Throwable> {

  private final Logger logger = LoggerFactory.getLogger(ReadRetry.class);

  private final PlcReadRequest request;
  private final int limit;
  private final AtomicInteger retries;
  private final CompletableFuture<PlcReadResponse> answer;

  public ReadRetry(PlcReadRequest readRequest, int limit, AtomicInteger retries, CompletableFuture<PlcReadResponse> answer) {
    this.request = readRequest;
    this.limit = limit;
    this.retries = retries;
    this.answer = answer;
  }

  @Override
  public void accept(PlcReadResponse response, Throwable error) {
    if (error != null) {
      logger.trace("Detected failure response {}, counter {}, limit {}, re-attempting: {}", response, retries, limit, error);
      if (retries.getAndIncrement() < limit) {
        request.execute().whenComplete(this);
        return;
      }

      logger.trace("All attempts failed, reporting error for execution", error);
      answer.completeExceptionally(error);
      return;
    }
    answer.complete(response);
  }

}