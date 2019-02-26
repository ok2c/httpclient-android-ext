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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * TLS cipher suite support methods
 * <p>Copied from Apache HttpClient 5.0</p>
 */
public final class TlsCiphers {

    private static final String WEAK_KEY_EXCHANGES
            = "^(TLS|SSL)_(NULL|ECDH_anon|DH_anon|DH_anon_EXPORT|DHE_RSA_EXPORT|DHE_DSS_EXPORT|"
            + "DSS_EXPORT|DH_DSS_EXPORT|DH_RSA_EXPORT|RSA_EXPORT|KRB5_EXPORT)_(.*)";
    private static final String WEAK_CIPHERS
            = "^(TLS|SSL)_(.*)_WITH_(NULL|DES_CBC|DES40_CBC|DES_CBC_40|3DES_EDE_CBC|RC4_128|RC4_40|RC2_CBC_40)_(.*)";

    private static final List<Pattern> WEAK_CIPHER_SUITE_PATTERNS = Collections.unmodifiableList(Arrays.asList(
            Pattern.compile(WEAK_KEY_EXCHANGES, Pattern.CASE_INSENSITIVE),
            Pattern.compile(WEAK_CIPHERS, Pattern.CASE_INSENSITIVE)));

    public static boolean isWeak(final String cipherSuite) {
        for (final Pattern pattern : WEAK_CIPHER_SUITE_PATTERNS) {
            if (pattern.matcher(cipherSuite).matches()) {
                return true;
            }
        }
        return false;
    }

    public static String[] excludeWeak(final String... ciphers) {
        if (ciphers == null) {
            return null;
        }
        final List<String> enabledCiphers = new ArrayList<>();
        for (final String cipher: ciphers) {
            if (!TlsCiphers.isWeak(cipher)) {
                enabledCiphers.add(cipher);
            }
        }
        return !enabledCiphers.isEmpty() ? enabledCiphers.toArray(new String[0]) : ciphers;
    }

}
