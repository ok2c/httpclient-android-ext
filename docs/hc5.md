# Android extensions for Apache HttpClient 5.0.x

## Dependency management

Add API or implementation dependency on `com.github.ok2c.hc5.android:httpclient-android` 
to Gradle dependencies. This will automatically introduce transitive dependency on 
Apache HttpClient.

```
dependencies {
    api 'com.github.ok2c.hc5.android:httpclient-android:0.1.1'
}

```

## Configuration and application

HttpClient 5.0 is compatible with Android API 19 or later without any modifications 

Generally it is strongly advised to use a single instance of `CloseableHttpClient` per Android
activity or several related activities. It may also be a good idea to close out idle connections
on activity pause and evict expired connections upon activity resumption. 

Activities that have a dedicated `CloseableHttpClient` instance should close it when destroyed
in order to deallocate system resources held by persistent HTTP connections kept alive in 
the connection pool.

```java
public class HttpClientActivity extends Activity {

    private static final String LOG_TAG = "HttpClientActivity";

    private final PoolingHttpClientConnectionManager connectionManager;
    private final CloseableHttpClient httpClient;

    public HttpClientActivity() {
        this.connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setConnectionTimeToLive(TimeValue.ofMinutes(1))
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout(Timeout.ofSeconds(5))
                        .build())
                .build();
        this.httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(Timeout.ofSeconds(5))
                        .setResponseTimeout(Timeout.ofSeconds(5))
                        .setCookieSpec(CookieSpecs.STANDARD_STRICT.ident)
                        .build())
                .build();
    }

    @Override
    protected void onPause() {
        connectionManager.closeIdle(TimeValue.ZERO_MILLISECONDS);
        super.onPause();
    }

    @Override
    protected void onResume() {
        connectionManager.closeExpired();
        super.onResume();
    }

    @Override
    protected void onStop() {
        connectionManager.closeIdle(TimeValue.ZERO_MILLISECONDS);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try {
            this.httpClient.close();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        super.onDestroy();
    }

}
```

## Asynchronous HTTP message exchange support

The `HttpExecAsyncTask` class is an extension of Android `AsyncTask` that simplifies execution 
of HTTP requests and processing of HTTP responses. This class can execute a sequence of requests 
and process corresponding response data streams using a `HttpExecAsyncTask.ResponseHandler` handler
provided by the caller asynchronously from the UI activity while pushing `ExecUpdate` messages
with status updates from the execution thread to the UI activity thread. The UI components
can be updated to reflect the progress of HTTP message exchange based on attributes of 
`ExecUpdate` message. 

```java
public class InternalHttpRequestAsyncTask extends HttpExecAsyncTask<ClassicHttpResponse> {

    private final WeakReference<Activity> activityRef;

    InternalHttpRequestAsyncTask(
            final CloseableHttpClient httpClient,
            final Activity activity,
            final HttpExecAsyncTask.ResponseHandler<ClassicHttpResponse> handler) {
        super(httpClient, handler);
        this.activityRef = new WeakReference<>(activity);
    }

    private Activity getActivity() {
        Activity activity = activityRef.get();
        if (activity != null && !activity.isFinishing()) {
            return activity;
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        ...
    }

    @Override
    protected void onProgressUpdate(final ExecUpdate... values) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        ...
    }

    @Override
    protected void onCancelled(final List<ClassicHttpResponse> httpResponses) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        ...
    }

    @Override
    protected void onPostExecute(final List<ClassicHttpResponse> responses) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        ...
    }

}
```
## Logging

Apache HttpClient 5.0 uses SLF4J APIs to log messages. Any SLF4J backend compatible with Android
should work with HttpClient. 

This library however ships with its own simple SLF4J logger implementation that redirects all log 
events to Android `Logcat`. What differs this implementation from several similar ones is that 
groups all HttpClient related events into a single category with the tag `HttpClient`. 
Wire log events are redirected to `HttpClientWire` and message header events to `HttpClientHeader`.

1. Add the following class to application code. *IMPORTANT* Please note this class must
   be located in the `org.slf4j.impl` package.

    ```java
    package org.slf4j.impl;
    
    import org.slf4j.ILoggerFactory;
    import org.slf4j.spi.LoggerFactoryBinder;
    
    import com.ok2c.hc.android.logging.AndroidLoggerFactory;
    
    public final class StaticLoggerBinder implements LoggerFactoryBinder {
    
        private static final StaticLoggerBinder INSTANCE = new StaticLoggerBinder();
    
        public static StaticLoggerBinder getSingleton() {
            return INSTANCE;
        }
    
        @Override
        public ILoggerFactory getLoggerFactory() {
            return AndroidLoggerFactory.INSTANCE;
        }
    
        @Override
        public String getLoggerFactoryClassStr() {
            return AndroidLoggerFactory.class.getName();
        }
    
    }
    ```
1. Activate `Logcat` categories 

    ```
    adb shell setprop log.tag.HttpClient VERBOSE
    adb shell setprop log.tag.HttpClientHeader VERBOSE
    adb shell setprop log.tag.HttpClientWire VERBOSE
    
    ```
Skip `HttpClientWire` if wire logging is not required.