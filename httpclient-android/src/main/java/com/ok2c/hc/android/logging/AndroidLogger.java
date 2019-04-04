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

import android.util.Log;

import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

final class  AndroidLogger extends MarkerIgnoringBase {

    AndroidLogger(String tag) {
        this.name = tag;
    }

    boolean isLoggable(int priority) {
        return Log.isLoggable(name, priority);
    }

    private void log(int priority, String message, Throwable throwable) {
        Log.println(priority, name,
                throwable != null ? message + '\n' + Log.getStackTraceString(throwable) : message);
    }

    void logFormatted(int priority, String format, Object... argArray) {
        if (isLoggable(priority)) {
            FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
            log(priority, ft.getMessage(), ft.getThrowable());
        }
    }

    void logMessage(int priority, String message, Throwable throwable) {
        if (isLoggable(priority)) {
            log(priority, message, throwable);
        }
    }

    @Override
    public boolean isTraceEnabled() {
        return isLoggable(Log.VERBOSE);
    }

    @Override
    public void trace(String msg) {
        logMessage(Log.VERBOSE, msg, null);
    }

    @Override
    public void trace(String format, Object arg) {
        logFormatted(Log.VERBOSE, format, arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        logFormatted(Log.VERBOSE, format, arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        logFormatted(Log.VERBOSE, format, arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        logMessage(Log.VERBOSE, msg, t);
    }

    @Override
    public boolean isDebugEnabled() {
        return isLoggable(Log.DEBUG);
    }

    @Override
    public void debug(String msg) {
        logMessage(Log.DEBUG, msg, null);
    }

    @Override
    public void debug(String format, Object arg) {
        logFormatted(Log.DEBUG, format, arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        logFormatted(Log.DEBUG, format, arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        logFormatted(Log.DEBUG, format, arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        logMessage(Log.DEBUG, msg, t);
    }

    @Override
    public boolean isInfoEnabled() {
        return isLoggable(Log.INFO);
    }

    @Override
    public void info(String msg) {
        logMessage(Log.INFO, msg, null);
    }

    @Override
    public void info(String format, Object arg) {
        logFormatted(Log.INFO, format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        logFormatted(Log.INFO, format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        logFormatted(Log.INFO, format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        logMessage(Log.INFO, msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
        return isLoggable(Log.WARN);
    }

    @Override
    public void warn(String msg) {
        logMessage(Log.WARN, msg, null);
    }

    @Override
    public void warn(String format, Object arg) {
        logFormatted(Log.WARN, format, arg);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        logFormatted(Log.WARN, format, arg1, arg2);
    }

    @Override
    public void warn(String format, Object... arguments) {
        logFormatted(Log.WARN, format, arguments);
    }

    @Override
    public void warn(String msg, Throwable t) {
        logMessage(Log.WARN, msg, t);
    }

    @Override
    public boolean isErrorEnabled() {
        return isLoggable(Log.ERROR);
    }

    @Override
    public void error(String msg) {
        logMessage(Log.ERROR, msg, null);
    }

    @Override
    public void error(String format, Object arg) {
        logFormatted(Log.ERROR, format, arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        logFormatted(Log.ERROR, format, arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        logFormatted(Log.ERROR, format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        logMessage(Log.ERROR, msg, t);
    }

}