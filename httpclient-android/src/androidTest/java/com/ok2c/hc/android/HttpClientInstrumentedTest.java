package com.ok2c.hc.android;

import android.support.test.runner.AndroidJUnit4;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.net.URI;

@RunWith(AndroidJUnit4.class)
public class HttpClientInstrumentedTest {

    @Test
    public void testHttpGet() throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        URI requestUri1 = URI.create("http://httpbin.org/get");
        try (ClassicHttpResponse response = client.execute(new HttpGet(requestUri1))) {
            Assert.assertThat(response.getCode(), Matchers.equalTo(200));
            EntityUtils.consume(response.getEntity());
        }
        URI requestUri2 = URI.create("https://httpbin.org/get");
        try (ClassicHttpResponse response = client.execute(new HttpGet(requestUri2))) {
            Assert.assertThat(response.getCode(), Matchers.equalTo(200));
            EntityUtils.consume(response.getEntity());
        }
    }

}
