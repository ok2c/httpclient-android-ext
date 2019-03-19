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

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Factory methods for {@link org.apache.http.impl.client.CloseableHttpClient} instances.
 * optimized for Android platform.
 * <p>Copied from Apache HttpClient 5.0</p>
 */
public class AndroidHttpClients {

    /**
     * Creates {@link CloseableHttpClient} instance with default configuration.
     */
    public static CloseableHttpClient createDefault() {
        return HttpClientBuilder.create()
                .setConnectionManager(AndroidHttpClientConnectionManagerBuilder.create().build())
                .build();
    }

    /**
     * Creates {@link CloseableHttpClient} instance with default system configuration.
     */
    public static CloseableHttpClient createSystem() {
        return createDefault();
    }

}
