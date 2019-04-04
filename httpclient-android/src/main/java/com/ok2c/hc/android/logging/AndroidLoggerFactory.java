/*
 * Copyright 2019, OK2 Consulting Ltd
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
package com.ok2c.hc.android.logging;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class AndroidLoggerFactory implements ILoggerFactory {

    public static final AndroidLoggerFactory INSTANCE = new AndroidLoggerFactory();

    private static String loggerNameToTag(String loggerName) {
        if (loggerName == null) {
            return "null";
        }
        int length = loggerName.length();
        if (length <= 23) {
            return loggerName;
        }
        int lastPeriod = loggerName.lastIndexOf(".");
        return length - (lastPeriod + 1) <= 23
                ? loggerName.substring(lastPeriod + 1)
                : loggerName.substring(loggerName.length() - 23);
    }


    private final ConcurrentMap<String, Logger> loggerMap = new ConcurrentHashMap<String, Logger>();

    public Logger getLogger(String name) {
        String tag;
        if (name.equals("org.apache.hc.client5.http.wire")) {
            tag = "HttpClientWire";
        } else if (name.equals("org.apache.hc.client5.http.headers")) {
            tag = "HttpClientHeader";
        } else if (name.startsWith("org.apache.hc.")) {
            tag = "HttpClient";
        } else {
            tag = loggerNameToTag(name);
        }
        Logger logger = loggerMap.get(tag);
        if (logger == null) {
            Logger newLogger = new AndroidLogger(tag);
            logger = loggerMap.putIfAbsent(name, newLogger);
            if (logger == null) {
                logger = newLogger;
            }
        }
        return logger;
    }

}