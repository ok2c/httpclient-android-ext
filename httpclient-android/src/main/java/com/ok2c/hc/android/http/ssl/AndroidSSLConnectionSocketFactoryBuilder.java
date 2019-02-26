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

package com.ok2c.hc.android.http.ssl;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

/**
 * Builder for {@link AndroidSSLConnectionSocketFactory} instances.
 * <p>Copied from Apache HttpClient 5.0</p>
 */
public class AndroidSSLConnectionSocketFactoryBuilder {

    public static AndroidSSLConnectionSocketFactoryBuilder create() {
        return new AndroidSSLConnectionSocketFactoryBuilder();
    }

    private SSLContext sslContext;
    private String[] tlsVersions;
    private String[] ciphers;
    private HostnameVerifier hostnameVerifier;

    /**
     * Assigns {@link SSLContext} instance.
     */
    public AndroidSSLConnectionSocketFactoryBuilder setSslContext(final SSLContext sslContext) {
        this.sslContext = sslContext;
        return this;
    }

    /**
     * Assigns enabled {@code TLS} versions.
     */
    public final AndroidSSLConnectionSocketFactoryBuilder setTlsVersions(final String... tlslVersions) {
        this.tlsVersions = tlslVersions;
        return this;
    }

    /**
     * Assigns enabled {@code TLS} versions.
     */
    public final AndroidSSLConnectionSocketFactoryBuilder setTlsVersions(final TLS... tlslVersions) {
        this.tlsVersions = new String[tlslVersions.length];
        for (int i = 0; i < tlslVersions.length; i++) {
            this.tlsVersions[i] = tlslVersions[i].ident;
        }
        return this;
    }

    /**
     * Assigns enabled ciphers.
     */
    public final AndroidSSLConnectionSocketFactoryBuilder setCiphers(final String... ciphers) {
        this.ciphers = ciphers;
        return this;
    }


    /**
     * Assigns {@link HostnameVerifier} instance.
     */
    public AndroidSSLConnectionSocketFactoryBuilder setHostnameVerifier(final HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return this;
    }

    public SSLConnectionSocketFactory build() {
        final javax.net.ssl.SSLSocketFactory socketfactory;
        if (sslContext != null) {
            socketfactory = sslContext.getSocketFactory();
        } else {
            socketfactory = (javax.net.ssl.SSLSocketFactory) javax.net.ssl.SSLSocketFactory.getDefault();
        }
        return new SSLConnectionSocketFactory(
                socketfactory,
                tlsVersions,
                ciphers,
                hostnameVerifier != null ? hostnameVerifier : AndroidSSLConnectionSocketFactory.getDefaultHostnameVerifier());
    }

}
