package com.ok2c.hc.android;

import android.support.test.runner.AndroidJUnit4;

import com.ok2c.hc.android.http.AndroidHttpClients;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;

@RunWith(AndroidJUnit4.class)
public class HttpClientInstrumentedTest {

    @Test
    public void testHttpGet() throws Exception {
        CloseableHttpClient client = AndroidHttpClients.createDefault();
        URI requestUri1 = URI.create("http://httpbin.org/get");
        try (CloseableHttpResponse response = client.execute(new HttpGet(requestUri1))) {
            Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(200));
            EntityUtils.consume(response.getEntity());
        }
        URI requestUri2 = URI.create("https://httpbin.org/get");
        try (CloseableHttpResponse response = client.execute(new HttpGet(requestUri2))) {
            Assert.assertThat(response.getStatusLine().getStatusCode(), Matchers.equalTo(200));
            EntityUtils.consume(response.getEntity());
        }
    }

}
