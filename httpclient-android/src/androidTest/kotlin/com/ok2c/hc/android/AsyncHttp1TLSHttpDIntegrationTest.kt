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
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http2.HttpVersionPolicy
import org.junit.runner.RunWith

/**
 * Make sure the integration test services are running
 * by executing Docker Compose from <project_root>/docker/docker-compose.yml
 */
@RunWith(AndroidJUnit4::class)
class AsyncHttp1TLSHttpDIntegrationTest: AsyncHttp1HttpDIntegrationTest(
    HttpVersionPolicy.FORCE_HTTP_1,
    HttpHost("https", "172.20.0.3", 443))  {
}