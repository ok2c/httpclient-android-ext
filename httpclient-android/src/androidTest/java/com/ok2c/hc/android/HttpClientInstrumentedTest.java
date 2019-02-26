package com.ok2c.hc.android;

import java.net.URI;

import javax.net.ssl.SSLSocketFactory;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ok2c.hc.android.http.ssl.AndroidSSLConnectionSocketFactory;

import android.support.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class HttpClientInstrumentedTest {

    @Test
    public void testHttpGet() throws Exception {
        CloseableHttpClient client = HttpClientBuilder.create()
                .setSSLSocketFactory(new AndroidSSLConnectionSocketFactory
                        ((SSLSocketFactory) SSLSocketFactory.getDefault(),
                                new DefaultHostnameVerifier()))
                .build();
        URI requestUri = URI.create("https://httpbin.org/get");
        try (CloseableHttpResponse response = client.execute(new HttpGet(requestUri))) {
            Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(200));
            EntityUtils.consume(response.getEntity());
        }
    }

}
