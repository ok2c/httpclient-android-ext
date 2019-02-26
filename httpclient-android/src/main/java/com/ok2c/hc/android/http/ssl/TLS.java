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

import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.CharArrayBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Supported {@code TLS} protocol versions.
 * <p>Copied from Apache HttpClient 5.0</p>
 */
public enum TLS {

    V_1_0("TLSv1",   new ProtocolVersion("TLS", 1, 0)),
    V_1_1("TLSv1.1", new ProtocolVersion("TLS", 1, 1)),
    V_1_2("TLSv1.2", new ProtocolVersion("TLS", 1, 2)),
    V_1_3("TLSv1.3", new ProtocolVersion("TLS", 1, 3));

    public final String ident;
    public final ProtocolVersion version;

    TLS(final String ident, final ProtocolVersion version) {
        this.ident = ident;
        this.version = version;
    }

    public boolean isSame(final ProtocolVersion protocolVersion) {
        return version.equals(protocolVersion);
    }

    public boolean isComparable(final ProtocolVersion protocolVersion) {
        return version.isComparable(protocolVersion);
    }

    public boolean greaterEquals(final ProtocolVersion protocolVersion) {
        return version.greaterEquals(protocolVersion);
    }

    public boolean lessEquals(final ProtocolVersion protocolVersion) {
        return version.lessEquals(protocolVersion);
    }

    public static ProtocolVersion parse(final String s) throws ParseException {
        if (s == null) {
            return null;
        }
        final ParserCursor cursor = new ParserCursor(0, s.length());
        final CharArrayBuffer buf = new CharArrayBuffer(s.length());
        buf.append(s);
        return TlsVersionParser.INSTANCE.parse(buf, cursor, null);
    }

    public static String[] excludeWeak(final String... protocols) {
        if (protocols == null) {
            return null;
        }
        final List<String> enabledProtocols = new ArrayList<>();
        for (final String protocol: protocols) {
            if (!protocol.startsWith("SSL") && !protocol.equals(V_1_0.ident) && !protocol.equals(V_1_1.ident)) {
                enabledProtocols.add(protocol);
            }
        }
        if (enabledProtocols.isEmpty()) {
            enabledProtocols.add(V_1_2.ident);
        }
        return enabledProtocols.toArray(new String[0]);
    }

}
