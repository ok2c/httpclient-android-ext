# Android extensions for Apache HttpClient

## History of Apache HttpClient on Android

Google Android 1.0 was released with a pre-BETA snapshot of Apache HttpClient. To coincide with
the first Android release Apache HttpClient 4.0 APIs had to be frozen prematurely, while many of
interfaces and internal structures were still not fully worked out. 

As Apache HttpClient 4.0 was maturing the project was expecting Google to incorporate the latest 
code improvements into their code tree. Unfortunately it did not happen. Version of Apache 
HttpClient shipped with Android has effectively become a fork. 

Eventually Google decided to discontinue further development of their fork while refusing 
to upgrade to the stock version of Apache HttpClient citing compatibility concerns as a reason 
for such decision. Google completely removed their fork of Apache HttpClient from Android in 
version 8.0 (API 26) only.

## Project scope and objectives

The main objective of this project is to ensure that official Apache HttpClient releases can 
be used on Android without any alterations.

This project does not aim at creating an alternative implementation of Apache HttpClient
for Android or in any form or fashion. It only provides alternative implementations of 
those components that are incompatible with Android and add several utility classes specifically
designed for Android.

## Usage

### Dependency management

Add API or implementation dependency on `com.github.ok2c.hc4.android:httpclient-android` 
to Gradle dependencies. This will automatically introduce transitive dependency on 
Apache HttpClient.

```
dependencies {
    api 'com.github.ok2c.hc4.android:httpclient-android:0.1.0'
}

```

### Configuration and application

HttpClient 4.5 is _almost_ compatible with Android API 26 or later. The trouble-maker is 
the `org.apache.http.conn.ssl.DefaultHostnameVerifier` class that depends on `javax.naming` APIs 
unsupported by Android.

Android extensions for Apache HttpClient provide a replacement for the default `HostnameVerifier` 
implementation and provide a builder for `PoolingHttpClientConnectionManager` optimized 
for Android called `AndroidHttpClientConnectionManagerBuilder`. Users of Apache HttpClient 4.5
are strongly advised to use this builder to create connection managers in their Android 
applications.

Generally it is strongly advised to use a single instance of `CloseableHttpClient` per Android
activity or several related activities. It may also be a good idea to close out idle connections
on activity pause and evict expired connections upon activity resumption. 

Activities that have a dedicated `CloseableHttpClient` instance should close it when destroyed
in order to deallocate system resources held by persistent HTTP connections kept alive in 
the connection pool.

```java
public class HttpClientActivity extends Activity {

    private static final String LOG_TAG = "HttpClientActivity";

    private final HttpClientConnectionManager connectionManager;
    private final CloseableHttpClient httpClient;

    public HttpClientActivity() {
        this.connectionManager = AndroidHttpClientConnectionManagerBuilder.create()
                .setConnectionTimeToLive(1, TimeUnit.MINUTES)
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout(5000)
                        .build())
                .build();
        this.httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(5000)
                        .setCookieSpec(CookieSpecs.STANDARD_STRICT)
                        .build())
                .build();
    }
    
    @Override
    protected void onPause() {
        connectionManager.closeIdleConnections(0, TimeUnit.MILLISECONDS);
        super.onPause();
    }

    @Override
    protected void onResume() {
        connectionManager.closeExpiredConnections();
        super.onResume();
    }

    @Override
    protected void onStop() {
        connectionManager.closeIdleConnections(0, TimeUnit.MILLISECONDS);
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

### Asynchronous HTTP message exchange support

The `HttpExecAsyncTask` class is an extension of Android `AsyncTask` that simplifies execution 
of HTTP requests and processing of HTTP responses. This class can execute a sequence of requests 
and process corresponding response data streams using a `HttpExecAsyncTask.ResponseHandler` handler
provided by the caller asynchronously from the UI activity while pushing `ExecUpdate` messages
with status updates from the execution thread to the UI activity thread. The UI components
can be updated to reflect the progress of HTTP message exchange based on attributes of 
`ExecUpdate` message. 

```java
public class InternalHttpRequestAsyncTask extends HttpExecAsyncTask<HttpResponse> {

    private final WeakReference<Activity> activityRef;

    InternalHttpRequestAsyncTask(
            final CloseableHttpClient httpClient,
            final Activity activity,
            final HttpExecAsyncTask.ResponseHandler<HttpResponse> handler) {
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
    protected void onCancelled(final List<HttpResponse> httpResponses) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        ...
    }

    @Override
    protected void onPostExecute(final List<HttpResponse> responses) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        ...
    }

}
```
### Logging

Apache HttpClient 4.5 uses Commons Logging APIs to log messages. When running on Android 
Commons Logging redirects log messages to Java Logging backend (`java.util.logging`) backend 
at runtime. The default Java logging handler shipped with Android in its turn redirects 
log messages to `Logcat` - Android native logging backend. This three layer logging setup
is quite cumbersome. *Important* Please note one must enable logging at Java level
and at Android level in order to see HttpClient logs in the Android `Logcat`

The default `java.util.logging` log handler shipped with Android has to truncate original 
logging category names into so-called Android log tags. It usually takes the last period separated 
segment and truncates it to 23 characters. 

```
'org.apache.http.impl.execchain.MainClientExec' -> 'MainClientExec'
'org.apache.http.impl.conn.DefaultHttpClientConnectionOperator' -> 'DefaultHttpClientConn'

```

This scheme may prove quite inconsistent and inconvenient when running HttpClient with context 
logging.

This library provides an alternative logging handler that groups HttpClient log messages into 
three categories

```
'org.apache.http.wire' -> 'HttpClientWire'
'org.apache.http.headers' -> 'HttpClientHeader'
'org.apache.http.*' -> 'HttpClient'

``` 

#### HttpClient Logging configuration

1. Create `logging.properties` file in `<MODULE>/src/main/res/raw` folder and customize logging 
configuration as required by the application context.

    ```
    .level = WARN
    
    #handlers = com.android.internal.logging.AndroidHandler
    handlers = com.ok2c.hc.android.logging.HttpClientLogHandler
    
    org.apache.http.level = FINEST
    org.apache.http.wire.level = SEVERE
    
    
    ```

1. Android is not going to read this configuration by default. It is necessary to load this 
configuration into the `LogManager` manually at application start-up. 

    ```java
    public class HttpClientApplication extends Application {
    
        private static final String LOG_TAG = "HttpClientApplication";
    
        @Override
        public void onCreate() {
            super.onCreate();
            try (InputStream inputStream = getResources().openRawResource(R.raw.logging)) {
                LogManager logManager = LogManager.getLogManager();
                logManager.readConfiguration(new BufferedInputStream(inputStream));
            } catch (IOException ex) {
                Log.e(LOG_TAG, ex.getMessage(), ex);
            }
        }
    }
    ```
*Important* please update `AndroidManifest.xml` to use this custom application class.

1. Activate `Logcat` categories 

    ```
    adb shell setprop log.tag.HttpClient VERBOSE
    adb shell setprop log.tag.HttpClientHeader VERBOSE
    adb shell setprop log.tag.HttpClientWire VERBOSE
    
    ```
Skip `HttpClientWire` if wire logging is not required.    
    