package com.ok2c.hc.android

import android.app.Activity
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.core5.io.CloseMode
import org.apache.hc.core5.util.TimeValue

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