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
package com.ok2c.hc.android.http;

import org.apache.hc.core5.http.message.RequestLine;
import org.apache.hc.core5.http.message.StatusLine;

import java.util.Objects;

/**
 * Execution status messages published by {@link HttpExecAsyncTask}
 * to update the UI activity about the progress of HTTP message exchange.
 */
public final class ExecUpdate {

    enum State { REQUEST, RESPONSE, ERROR }

    static public ExecUpdate request(RequestLine requestLine) {
        return new ExecUpdate(State.REQUEST, requestLine, null, 0, -1, null);
    }

    static public ExecUpdate response(RequestLine requestLine, StatusLine statusLine, long current, long total) {
        return new ExecUpdate(State.RESPONSE, requestLine, statusLine, current, total, null);
    }

    static public ExecUpdate error(RequestLine requestLine, Exception exception) {
        return new ExecUpdate(State.ERROR, requestLine, null, 0, -1, exception);
    }

    private final State state;
    private final RequestLine requestLine;
    private final StatusLine statusLine;
    private final long current;
    private final long total;
    private final Exception exception;

    private ExecUpdate(State state, RequestLine requestLine, StatusLine statusLine, long current, long total, Exception exception) {
        this.state = state;
        this.requestLine = requestLine;
        this.statusLine = statusLine;
        this.current = current;
        this.total = total;
        this.exception = exception;
    }

    public State getState() {
        return state;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Exception getException() {
        return exception;
    }

    public long getCurrent() {
        return current;
    }

    public long getTotal() {
        return total;
    }

    @Override
    public String toString() {
        switch (state) {
            case REQUEST:
                return "REQUEST " + requestLine;
            case RESPONSE:
                return "RESPONSE " + statusLine + " (" + current + " of " + total + ")";
            case ERROR:
                return "ERROR " + exception;
            default:
                return Objects.toString(requestLine);
        }
    }

}
