# Android extensions for Apache HttpClient 5.0.x

## Dependency management

Add API or implementation dependency on `com.github.ok2c.hc5.android:httpclient-android` 
to Gradle dependencies. This will automatically introduce transitive dependency on 
Apache HttpClient.

```
dependencies {
    api 'com.github.ok2c.hc5.android:httpclient-android:0.2.0'
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

`HttpClientActivity` provided by the library can be used as a base class for all activities that
involve HTTP communication using Apache HttpClient.

```kotlin
abstract class HttpClientActivity(builder: PoolingHttpClientConnectionManagerBuilder): Activity() {

    protected var connectionManager: PoolingHttpClientConnectionManager = builder.build()

    override fun onPause() {
        connectionManager.closeIdle(TimeValue.ZERO_MILLISECONDS)
        super.onPause()
    }

    override fun onResume() {
        connectionManager.closeExpired()
        super.onResume()
    }

    override fun onStop() {
        connectionManager.closeIdle(TimeValue.ZERO_MILLISECONDS)
        super.onStop()
    }

    override fun onDestroy() {
        connectionManager.close(CloseMode.GRACEFUL)
        super.onDestroy()
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

 ```kotlin
package org.slf4j.impl

import com.ok2c.hc.android.logging.AndroidLoggerFactory
import org.slf4j.ILoggerFactory
import org.slf4j.spi.LoggerFactoryBinder

class StaticLoggerBinder: LoggerFactoryBinder {

   companion object {
      private val INSTANCE = StaticLoggerBinder()

      @JvmStatic
      fun getSingleton(): StaticLoggerBinder? {
         return INSTANCE
      }
   }

    override fun getLoggerFactory(): ILoggerFactory? {
        return AndroidLoggerFactory.INSTANCE
    }

    override fun getLoggerFactoryClassStr(): String? {
        return AndroidLoggerFactory::class.java.name
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