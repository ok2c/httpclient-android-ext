/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package com.ok2c.hc.android.http;

import com.ok2c.hc.android.http.ssl.AndroidSSLConnectionSocketFactory;

import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

/**
 * Builder for {@link org.apache.http.impl.conn.PoolingHttpClientConnectionManager} instances
 * optimized for Android platform.
 * <p>Copied from Apache HttpClient 5.0</p>
 */
public class AndroidHttpClientConnectionManagerBuilder {

    private HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connectionFactory;
    private LayeredConnectionSocketFactory sslSocketFactory;
    private SchemePortResolver schemePortResolver;
    private DnsResolver dnsResolver;
    private SocketConfig defaultSocketConfig;

    private int maxConnTotal = 0;
    private int maxConnPerRoute = 0;

    private long timeToLive;
    private TimeUnit timeToLiveTimeUnit;

    public static AndroidHttpClientConnectionManagerBuilder create() {
        return new AndroidHttpClientConnectionManagerBuilder();
    }

    private AndroidHttpClientConnectionManagerBuilder() {
        super();
    }

    /**
     * Assigns {@link HttpConnectionFactory} instance.
     */
    public final AndroidHttpClientConnectionManagerBuilder setConnectionFactory(
            final HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connectionFactory) {
        this.connectionFactory = connectionFactory;
        return this;
    }

    /**
     * Assigns {@link LayeredConnectionSocketFactory} instance.
     */
    public final AndroidHttpClientConnectionManagerBuilder setSSLSocketFactory(
            final LayeredConnectionSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
        return this;
    }

    /**
     * Assigns {@link DnsResolver} instance.
     */
    public final AndroidHttpClientConnectionManagerBuilder setDnsResolver(final DnsResolver dnsResolver) {
        this.dnsResolver = dnsResolver;
        return this;
    }

    /**
     * Assigns {@link SchemePortResolver} instance.
     */
    public final AndroidHttpClientConnectionManagerBuilder setSchemePortResolver(final SchemePortResolver schemePortResolver) {
        this.schemePortResolver = schemePortResolver;
        return this;
    }

    /**
     * Assigns maximum total connection value.
     */
    public final AndroidHttpClientConnectionManagerBuilder setMaxConnTotal(final int maxConnTotal) {
        this.maxConnTotal = maxConnTotal;
        return this;
    }

    /**
     * Assigns maximum connection per route value.
     */
    public final AndroidHttpClientConnectionManagerBuilder setMaxConnPerRoute(final int maxConnPerRoute) {
        this.maxConnPerRoute = maxConnPerRoute;
        return this;
    }

    /**
     * Assigns default {@link SocketConfig}.
     */
    public final AndroidHttpClientConnectionManagerBuilder setDefaultSocketConfig(final SocketConfig config) {
        this.defaultSocketConfig = config;
        return this;
    }

    /**
     * Sets maximum time to live for persistent connections
     */
    public final AndroidHttpClientConnectionManagerBuilder setConnectionTimeToLive(final long timeToLive, final TimeUnit timeToLiveTimeUnit) {
        this.timeToLive = timeToLive;
        this.timeToLiveTimeUnit = timeToLiveTimeUnit;
        return this;
    }

    public PoolingHttpClientConnectionManager build() {
        @SuppressWarnings("resource")
        final PoolingHttpClientConnectionManager poolingmgr = new PoolingHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", sslSocketFactory != null ? sslSocketFactory :
                                AndroidSSLConnectionSocketFactory.getSystemSocketFactory())
                        .build(),
                connectionFactory,
                schemePortResolver,
                dnsResolver,
                timeToLive,
                timeToLiveTimeUnit != null ? timeToLiveTimeUnit : TimeUnit.MILLISECONDS);
        if (defaultSocketConfig != null) {
            poolingmgr.setDefaultSocketConfig(defaultSocketConfig);
        }
        if (maxConnTotal > 0) {
            poolingmgr.setMaxTotal(maxConnTotal);
        }
        if (maxConnPerRoute > 0) {
            poolingmgr.setDefaultMaxPerRoute(maxConnPerRoute);
        }
        return poolingmgr;
    }

}
