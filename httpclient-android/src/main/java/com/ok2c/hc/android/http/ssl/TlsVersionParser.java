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
import org.apache.http.message.TokenParser;
import org.apache.http.util.CharArrayBuffer;

import java.util.BitSet;

/**
 * <p>Copied from Apache HttpClient 5.0</p>
 */
final class TlsVersionParser {

    public final static TlsVersionParser INSTANCE = new TlsVersionParser();

    private final TokenParser tokenParser;

    TlsVersionParser() {
        this.tokenParser = TokenParser.INSTANCE;
    }

    ProtocolVersion parse(
            final CharArrayBuffer buffer,
            final ParserCursor cursor,
            final BitSet delimiters) throws ParseException {
        int pos = cursor.getPos();
        if (pos + 4 > cursor.getUpperBound()) {
            throw new ParseException("Invalid TLS protocol version");
        }
        if (buffer.charAt(pos) != 'T' || buffer.charAt(pos + 1) != 'L' || buffer.charAt(pos + 2) != 'S'
                || buffer.charAt(pos + 3) != 'v') {
            throw new ParseException("Invalid TLS protocol version");
        }
        pos = pos + 4;
        cursor.updatePos(pos);
        if (cursor.atEnd()) {
            throw new ParseException("Invalid TLS version");
        }
        final String s = this.tokenParser.parseToken(buffer, cursor, delimiters);
        final int idx = s.indexOf('.');
        if (idx == -1) {
            final int major;
            try {
                major = Integer.parseInt(s);
            } catch (final NumberFormatException e) {
                throw new ParseException("Invalid TLS major version");
            }
            return new ProtocolVersion("TLS", major, 0);
        } else {
            final String s1 = s.substring(0, idx);
            final int major;
            try {
                major = Integer.parseInt(s1);
            } catch (final NumberFormatException e) {
                throw new ParseException("Invalid TLS major version");
            }
            final String s2 = s.substring(idx + 1);
            final int minor;
            try {
                minor = Integer.parseInt(s2);
            } catch (final NumberFormatException e) {
                throw new ParseException("Invalid TLS minor version");
            }
            return new ProtocolVersion("TLS", major, minor);
        }
    }

    ProtocolVersion parse(final String s) throws ParseException {
        if (s == null) {
            return null;
        }
        final ParserCursor cursor = new ParserCursor(0, s.length());
        final CharArrayBuffer buf = new CharArrayBuffer(s.length());
        buf.append(s);
        return parse(buf, cursor, null);
    }

}

