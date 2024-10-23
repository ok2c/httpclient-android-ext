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
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.HttpStatus
import org.apache.hc.core5.http.Message
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder
import org.apache.hc.core5.http.protocol.HttpCoreContext
import org.apache.hc.core5.util.Timeout
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Make sure the integration test services are running
 * by executing Docker Compose from <project_root>/docker/docker-compose.yml
 */
@RunWith(AndroidJUnit4::class)
class ClassicHttpDIntegrationTest {

    companion object {

        val TIMEOUT = Timeout.ofSeconds(5)

    }

    // HttpBin.org running in a docker container
    val httpd = HttpHost("172.20.0.3", 80)

    val client: CloseableHttpClient = HttpClientBuilder.create()
        .build();

    @After
    fun cleanup() {
        client.close()
    }

    @Test
    fun test_sequential_requests() {
        val n = 20
        for (i in 0 until n) {
            val context = HttpCoreContext.create()
            val request = ClassicRequestBuilder.get()
                .setHttpHost(httpd)
                .setPath("/aaa")
                .build()
            val message = client.execute(request, context) { response ->
                Message(response, EntityUtils.toString(response.entity))
            }
            Assertions.assertThat(message.head.code).isEqualTo(HttpStatus.SC_OK)
            Assertions.assertThat(message.body).startsWith("aaa")
        }
    }

    @Test
    fun test_multi_threaded_requests() {
        val c = 10
        val n = AtomicInteger(20 * c)
        val latch = CountDownLatch(c)
        val resultQueue: Queue<Result<String>> = ConcurrentLinkedQueue()
        val executorService = Executors.newFixedThreadPool(c)
        try {
            for (i in 0 until c) {
                executorService.execute {
                    try {
                        while (n.decrementAndGet() > 0) {
                            val context = HttpCoreContext.create()
                            val request = ClassicRequestBuilder.get()
                                .setHttpHost(httpd)
                                .setPath("/aaa")
                                .build()
                            try {
                                resultQueue.add(client.execute(request, context) { response ->
                                    Result(request, response, EntityUtils.toString(response.entity), null)
                                })
                            } catch (ex: Exception) {
                                resultQueue.add(Result(request, null, null, ex))
                            }
                        }
                    } finally {
                        latch.countDown()
                    }
                }
            }
            Assertions.assertThat(latch.await(TIMEOUT.duration, TIMEOUT.timeUnit)).isTrue()
            for (result in resultQueue) {
                if (result.isOK()) {
                    Assertions.assertThat(result.response!!.code).isEqualTo(HttpStatus.SC_OK)
                } else {
                    Assertions.fail(result.ex)
                }
            }
        } finally {
            executorService.shutdownNow()
        }
    }

}