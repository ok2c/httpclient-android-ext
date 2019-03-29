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

import android.os.AsyncTask;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.message.RequestLine;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.util.Args;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Extension of {@link AsyncTask} that simplifies execution of {@link ClassicHttpRequest}s.
 * and processing of {@link ClassicHttpResponse}s. This class can execute a sequence
 * of requests and process corresponding response data streams using a {@link ResponseHandler}
 * provided by the caller asynchronously from the UI activity while pushing {@link ExecUpdate}
 * messages with status updates from the execution thread to the UI activity thread.
 *
 * @param <T> message exchange result type
 */
public class HttpExecAsyncTask<T> extends AsyncTask<ClassicHttpRequest, ExecUpdate, List<T>> {

    /**
     * Abstract response processing function.
     *
     * @param <T> message exchange result type
     */
    public interface ResponseHandler<T> {

        /**
         * Handles the response to the given request.
         *
         * @param request the executed request
         * @param response the response message head
         * @param contentType the content type of the response body if available
         *                    or {@code null} if the response does not enclose an entity
         * @param inputStream the content stream of the response body if available
         *                    or {@code null} if the response does not enclose an entity
         * @return the message exchange result
         */
        T handle(ClassicHttpRequest request,
                 ClassicHttpResponse response,
                 ContentType contentType,
                 InputStream inputStream) throws IOException;

    }

    private final CloseableHttpClient httpClient;
    private final ResponseHandler<T> responseHandler;

    public HttpExecAsyncTask(
            final CloseableHttpClient httpClient, final ResponseHandler<T> responseHandler) {
        this.httpClient = Args.notNull(httpClient, "HttpClient");
        this.responseHandler = Args.notNull(responseHandler, "Response handler");
    }

    @Override
    protected List<T> doInBackground(final ClassicHttpRequest... requests) {
        HttpClientContext context = HttpClientContext.create();
        List<T> results = new ArrayList<>();
        for (ClassicHttpRequest request : requests) {
            final RequestLine requestLine = new RequestLine(request);
            publishProgress(ExecUpdate.request(requestLine));
            try (ClassicHttpResponse response = httpClient.execute(request, context)) {
                final HttpEntity entity = response.getEntity();
                final StatusLine statusLine = new StatusLine(response);
                if (entity != null) {
                    final long total = entity.getContentLength();
                    try (final InputStream inputStream = entity.getContent()) {
                        publishProgress(ExecUpdate.response(requestLine, statusLine, 0, total));
                        final ContentType contentType = ContentType.parseLenient(entity.getContentType());
                        results.add(responseHandler.handle(request, response, contentType, new InputStream() {

                            long current = 0;
                            long last = 0;

                            void fireUpdate() {
                                long n = current / 2048;
                                if (n > last) {
                                    last = n;
                                    publishProgress(ExecUpdate.response(requestLine, statusLine, current, total));
                                }
                            }

                            @Override
                            public int read() throws IOException {
                                int b = inputStream.read();
                                if (b != -1) {
                                    current++;
                                    fireUpdate();
                                }
                                return b;
                            }

                            @Override
                            public int read(final byte[] b) throws IOException {
                                int bytesRead = inputStream.read(b);
                                if (bytesRead > 0) {
                                    current += bytesRead;
                                    fireUpdate();
                                }
                                return bytesRead;
                            }

                            @Override
                            public int read(final byte[] b, final int off, final int len) throws IOException {
                                int bytesRead = inputStream.read(b, off, len);
                                if (bytesRead > 0) {
                                    current += bytesRead;
                                    fireUpdate();
                                }
                                return bytesRead;
                            }

                            @Override
                            public long skip(final long n) throws IOException {
                                long skipped = inputStream.skip(n);
                                if (skipped > 0) {
                                    current += skipped;
                                }
                                return skipped;
                            }

                            @Override
                            public int available() throws IOException {
                                return inputStream.available();
                            }

                            @Override
                            public void close() throws IOException {
                                inputStream.close();
                            }

                        }));
                    }
                } else {
                    publishProgress(ExecUpdate.response(requestLine, statusLine, 0, 0));
                    results.add(responseHandler.handle(request, response, null,null));
                }
            } catch (IOException ex) {
                publishProgress(ExecUpdate.error(requestLine, ex));
                break;
            }
        }
        return results;
    }

}
