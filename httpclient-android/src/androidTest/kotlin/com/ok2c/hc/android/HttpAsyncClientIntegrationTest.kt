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
import java.util.LinkedList
import java.util.concurrent.Future
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse
import org.apache.hc.client5.http.async.methods.SimpleResponseConsumer
import org.apache.hc.client5.http.auth.AuthScope
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider
import org.apache.hc.client5.http.protocol.HttpClientContext
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.nio.support.AsyncRequestBuilder
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HttpAsyncClientIntegrationTest {

    // HttpBin.org running in a docker container
    val httpbin = HttpHost("172.18.0.1", 8082)

    val client: CloseableHttpAsyncClient = HttpAsyncClientBuilder.create()
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
    fun testHttpGet() {
        val responseQueue = LinkedList<Future<SimpleHttpResponse>>()
        for (i in 1..5) {
            val request = AsyncRequestBuilder.get("/get")
                .setHttpHost(httpbin)
                .build();

            val responseFuture = client.execute(request, SimpleResponseConsumer.create(), null);
            responseQueue.add(responseFuture);
        }
        for (responseFuture in responseQueue) {
            val response = responseFuture.get()
            Assert.assertEquals(200, response.code)
        }
    }

    @Test
    fun testHttpPost() {
        val responseQueue = LinkedList<Future<SimpleHttpResponse>>()
        for (i in 1..5) {
            val request = AsyncRequestBuilder.post("/post")
                .setHttpHost(httpbin)
                .setEntity("stuff")
                .build();

            val responseFuture = client.execute(request, SimpleResponseConsumer.create(), null);
            responseQueue.add(responseFuture);
        }
        for (responseFuture in responseQueue) {
            val response = responseFuture.get()
            Assert.assertEquals(200, response.code)
        }
    }

    @Test
    fun testHttpBasicAuth() {
        val credsProvider = BasicCredentialsProvider()
        credsProvider.setCredentials(
            AuthScope(httpbin),
            UsernamePasswordCredentials("test-user", "passwd".toCharArray()))

        val responseQueue = LinkedList<Future<SimpleHttpResponse>>()
        for (i in 1..5) {
            val request = AsyncRequestBuilder.get("/basic-auth/test-user/passwd")
                .setHttpHost(httpbin)
                .build();

            val context = HttpClientContext.create()
            context.credentialsProvider = credsProvider
            val responseFuture = client.execute(request, SimpleResponseConsumer.create(),  context,null);
            responseQueue.add(responseFuture);
        }
        for (responseFuture in responseQueue) {
            val response = responseFuture.get()
            Assert.assertEquals(200, response.code)
        }
    }

    @Test
    fun testHttpDigestAuth() {
        val credsProvider = BasicCredentialsProvider()
        credsProvider.setCredentials(
            AuthScope(httpbin),
            UsernamePasswordCredentials("test-user", "passwd".toCharArray()))

        val responseQueue = LinkedList<Future<SimpleHttpResponse>>()
        for (i in 1..5) {
            val request = AsyncRequestBuilder.get("/digest-auth/auth/test-user/passwd")
                .setHttpHost(httpbin)
                .build();

            val context = HttpClientContext.create()
            context.credentialsProvider = credsProvider

            val responseFuture = client.execute(request, SimpleResponseConsumer.create(),  context,null);
            responseQueue.add(responseFuture);
        }
        for (responseFuture in responseQueue) {
            val response = responseFuture.get()
            Assert.assertEquals(200, response.code)
        }
    }

}