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
import org.apache.hc.client5.http.auth.AuthScope
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.client5.http.protocol.HttpClientContext
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.HttpStatus
import org.apache.hc.core5.http.Message
import org.apache.hc.core5.http.Method
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Make sure the integration test services are running
 * by executing Docker Compose from <project_root>/docker/docker-compose.yml
 */
@RunWith(AndroidJUnit4::class)
class ClassicHttpBinIntegrationTest {

    // HttpBin.org running in a docker container
    val httpbin = HttpHost("172.20.0.2", 80)

    val client: CloseableHttpClient = HttpClientBuilder.create()
        .build();

    @After
    fun cleanup() {
        client.close()
    }

    @Test
    fun test_get() {
        for (i in 1..5) {
            val request = ClassicRequestBuilder.get()
                .setHttpHost(httpbin)
                .setPath("/get")
                .build();
            val message = client.execute(request) { response ->
                Message(response, EntityUtils.toString(response.entity))
            }
            Assertions.assertThat(message.head.code).isEqualTo(HttpStatus.SC_OK)
        }
    }

    @Test
    fun test_post() {
        for (i in 1..5) {
            val request = ClassicRequestBuilder.post()
                .setHttpHost(httpbin)
                .setPath("/post")
                .setEntity("stuff")
                .build();
            val message = client.execute(request) { response ->
                Message(response, EntityUtils.toString(response.entity))
            }
            Assertions.assertThat(message.head.code).isEqualTo(HttpStatus.SC_OK)
        }
    }

    @Test
    fun test_http_post_anything() {
        for (method in setOf(Method.POST, Method.PUT)) {
            val request = ClassicRequestBuilder.create(method.name)
                .setHttpHost(httpbin)
                .setPath("/anything")
                .setEntity(StringEntity("some important message", ContentType.TEXT_PLAIN))
                .build();
            val message = client.execute(request) { response ->
                Message(response, EntityUtils.toString(response.entity))
            }
            Assertions.assertThat(message.head.code).isEqualTo(HttpStatus.SC_OK)
        }
    }

    @Test
    fun test_dripping() {
        val request = ClassicRequestBuilder.get()
            .setHttpHost(httpbin)
            .setPath("/drip")
            .build();
        val message = client.execute(request) { response ->
            Message(response, EntityUtils.toString(response.entity))
        }
        Assertions.assertThat(message.head.code).isEqualTo(HttpStatus.SC_OK)
    }

    @Test
    fun test_bytes() {
        val request = ClassicRequestBuilder.get()
            .setHttpHost(httpbin)
            .setPath("/bytes/20000")
            .build();
        val message = client.execute(request) { response ->
            Message(response, EntityUtils.toByteArray(response.entity))
        }
        Assertions.assertThat(message.head.code).isEqualTo(HttpStatus.SC_OK)
        Assertions.assertThat(message.body).hasSize(20000)
    }

    @Test
    fun test_delay() {
        for (method in setOf(Method.POST, Method.PUT)) {
            val request = ClassicRequestBuilder.create(method.name)
                .setHttpHost(httpbin)
                .setPath("/delay/2")
                .setEntity(StringEntity("some important message", ContentType.TEXT_PLAIN))
                .build();
            val message = client.execute(request) { response ->
                Message(response, EntityUtils.toString(response.entity))
            }
            Assertions.assertThat(message.head.code).isEqualTo(HttpStatus.SC_OK)
        }
    }

    @Test
    fun test_basic_auth() {
        val credsProvider = BasicCredentialsProvider()
        credsProvider.setCredentials(
            AuthScope(httpbin),
            UsernamePasswordCredentials("test-user", "passwd".toCharArray()))
        val context = HttpClientContext.create()
        context.credentialsProvider = credsProvider
        for (i in 1..5) {
            val request = ClassicRequestBuilder.get()
                .setHttpHost(httpbin)
                .setPath("/basic-auth/test-user/passwd")
                .build();
            val message = client.execute(request, context) { response ->
                Message(response, EntityUtils.toString(response.entity))
            }
            Assertions.assertThat(message.head.code).isEqualTo(HttpStatus.SC_OK)
        }
    }

    @Test
    fun test_digest_auth() {
        val credsProvider = BasicCredentialsProvider()
        credsProvider.setCredentials(
            AuthScope(httpbin),
            UsernamePasswordCredentials("test-user", "passwd".toCharArray()))
        val context = HttpClientContext.create()
        context.credentialsProvider = credsProvider
        for (i in 1..5) {
            val request = ClassicRequestBuilder.get()
                .setHttpHost(httpbin)
                .setPath("/digest-auth/auth/test-user/passwd")
                .build();
            val message = client.execute(request, context) { response ->
                Message(response, EntityUtils.toString(response.entity))
            }
            Assertions.assertThat(message.head.code).isEqualTo(HttpStatus.SC_OK)
        }
    }

}