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

import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder
import org.apache.hc.client5.http.config.TlsConfig
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder
import org.apache.hc.client5.http.protocol.HttpClientContext
import org.apache.hc.core5.concurrent.FutureCallback
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.HttpStatus
import org.apache.hc.core5.http.HttpVersion
import org.apache.hc.core5.http2.HttpVersionPolicy
import org.apache.hc.core5.http2.config.H2Config
import org.apache.hc.core5.util.Timeout
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Before
import org.junit.Test

abstract class AbstractAsyncHttpDIntegrationTest(val protocolPolicy: HttpVersionPolicy, val target: HttpHost) {

    companion object {

        val TIMEOUT = Timeout.ofSeconds(5)

    }

    val connManager = PoolingAsyncClientConnectionManagerBuilder.create()
        .setDefaultTlsConfig(TlsConfig.custom()
            .setVersionPolicy(protocolPolicy)
            .build())
        .build();
    val client: CloseableHttpAsyncClient = HttpAsyncClientBuilder.create()
        .setConnectionManager(connManager)
        .setH2Config(H2Config.custom()
            .setPushEnabled(true)
            .build())
        .build();

    @Before
    fun startup() {
        client.start()
    }

    @After
    fun cleanup() {
        client.close()
    }

    @Test
    fun test_protocol_version() {
        val request: SimpleHttpRequest = SimpleRequestBuilder.get()
            .setHttpHost(target)
            .setPath("/aaa")
            .build();
        val context = HttpClientContext.create()
        val responseFuture = client.execute(request, context, null);
        val response = responseFuture.get()
        Assertions.assertThat(response.code).isEqualTo(HttpStatus.SC_OK)
        when (protocolPolicy) {
            HttpVersionPolicy.FORCE_HTTP_1 -> Assertions.assertThat(context.protocolVersion).isEqualTo(HttpVersion.HTTP_1_1)
            HttpVersionPolicy.FORCE_HTTP_2 -> Assertions.assertThat(context.protocolVersion).isEqualTo(HttpVersion.HTTP_2)
            else -> Assertions.fail("Unexpected protocol policy: ${protocolPolicy}")
        }
    }

    @Test
    fun test_multiple_request_execution_sequential() {
        val n = 20
        for (i in 0 until n) {
            val request: SimpleHttpRequest = SimpleRequestBuilder.get()
                .setHttpHost(target)
                .setPath("/aaa")
                .build();
            val responseFuture = client.execute(request, null)
            val response = responseFuture.get()
            Assertions.assertThat(response.code).isEqualTo(HttpStatus.SC_OK)
        }
    }

    @Test
    fun test_multiple_request_execution_concurrent() {
        val c = 10
        connManager.defaultMaxPerRoute = 10
        val n = 20 * c
        val latch = CountDownLatch(n)
        val resultQueue: Queue<Result<String>> = ConcurrentLinkedQueue()
        for (i in 0 until n) {
            val request: SimpleHttpRequest = SimpleRequestBuilder.get()
                .setHttpHost(target)
                .setPath("/aaa")
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