/*
 * Copyright 2022, OK2 Consulting Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ok2c.hc.android

import android.support.test.runner.AndroidJUnit4
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder
import org.apache.hc.core5.concurrent.FutureCallback
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.HttpStatus
import org.apache.hc.core5.http2.HttpVersionPolicy
import org.assertj.core.api.Assertions
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Make sure the integration test services are running
 * by executing Docker Compose from <project_root>/docker/docker-compose.yml
 */
@RunWith(AndroidJUnit4::class)
class AsyncHttp1HttpDIntegrationTest: AbstractAsyncHttpDIntegrationTest(
    HttpVersionPolicy.FORCE_HTTP_1,
    HttpHost("172.20.0.3", 80))  {

    @Test
    fun test_multiple_request_execution_concurrent_non_persistent() {
        val c = 10
        connManager.defaultMaxPerRoute = 10
        val n = 20 * c
        val latch = CountDownLatch(n)
        val resultQueue: Queue<Result<String>> = ConcurrentLinkedQueue()
        for (i in 0 until n) {
            val request: SimpleHttpRequest = SimpleRequestBuilder.get()
                .setHttpHost(target)
                .setPath("/aaa")
                .addHeader("Connection", "Close")
                .build();
            client.execute(request, object : FutureCallback<SimpleHttpResponse> {

                override fun completed(response: SimpleHttpResponse) {
                    resultQueue.add(Result(request, response, response.bodyText, null))
                    latch.countDown()
                }

                override fun failed(ex: Exception) {
                    resultQueue.add(Result(request, null, null, ex))
                    latch.countDown()
                }

                override fun cancelled() {
                    latch.countDown()
                }

            });
        }

        Assertions.assertThat(latch.await(TIMEOUT.duration, TIMEOUT.timeUnit)).isTrue()
        Assertions.assertThat(resultQueue).hasSize(n)
        for (result in resultQueue) {
            if (result.isOK()) {
                Assertions.assertThat(result.response!!.code).isEqualTo(HttpStatus.SC_OK)
            } else {
                Assertions.fail(result.ex)
            }
        }
    }

}