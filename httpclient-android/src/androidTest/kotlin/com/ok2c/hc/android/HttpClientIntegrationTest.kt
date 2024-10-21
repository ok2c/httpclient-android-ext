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
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.HttpStatus
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder
import org.assertj.core.api.Assertions
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HttpClientIntegrationTest {

    // HttpBin.org running in a docker container
    val httpbin = HttpHost("172.19.0.2", 80)

    val client: CloseableHttpClient = HttpClientBuilder.create()
        .build();

    @After
    fun cleanup() {
        client.close()
    }

    @Test
    fun testHttpGet() {
        for (i in 1..5) {
            val request = ClassicRequestBuilder.get("/get")
                .setHttpHost(httpbin)
                .build();
            Assertions.assertThat(client.execute(request) { response ->
                EntityUtils.consume(response.entity);
                response.code
            }).isEqualTo(HttpStatus.SC_OK)
        }
    }

    @Test
    fun testHttpPost() {
        for (i in 1..5) {
            val request = ClassicRequestBuilder.post("/post")
                .setHttpHost(httpbin)
                .setEntity("stuff")
                .build();
            Assertions.assertThat(client.execute(request) { response ->
                EntityUtils.consume(response.entity);
                response.code
            }).isEqualTo(HttpStatus.SC_OK)
        }
    }

    @Test
    fun testHttpBasicAuth() {
        val credsProvider = BasicCredentialsProvider()
        credsProvider.setCredentials(
            AuthScope(httpbin),
            UsernamePasswordCredentials("test-user", "passwd".toCharArray()))
        val context = HttpClientContext.create()
        context.credentialsProvider = credsProvider
        for (i in 1..5) {
            val request = ClassicRequestBuilder.get("/basic-auth/test-user/passwd")
                .setHttpHost(httpbin)
                .build();
            Assertions.assertThat(client.execute(request, context) { response ->
                EntityUtils.consume(response.entity);
                response.code
            }).isEqualTo(HttpStatus.SC_OK)
        }
    }

    @Test
    fun testHttpDigestAuth() {
        val credsProvider = BasicCredentialsProvider()
        credsProvider.setCredentials(
            AuthScope(httpbin),
            UsernamePasswordCredentials("test-user", "passwd".toCharArray()))
        val context = HttpClientContext.create()
        context.credentialsProvider = credsProvider
        for (i in 1..5) {
            val request = ClassicRequestBuilder.get("/digest-auth/auth/test-user/passwd")
                .setHttpHost(httpbin)
                .build();
            Assertions.assertThat(client.execute(request, context) { response ->
                EntityUtils.consume(response.entity);
                response.code
            }).isEqualTo(HttpStatus.SC_OK)
        }
    }

}