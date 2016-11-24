package com.zimaoda.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fz on 2015/8/30.
 */
public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    private static RequestConfig requestConfig;

    public static RequestConfig longRequestConfig;

    private static final int timeout = 20 * 1000;

    private static final int longTimeout = 60 * 1000;

    static {
        requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout)
                .build();

        longRequestConfig = RequestConfig.custom()
                .setConnectTimeout(longTimeout)
                .setConnectionRequestTimeout(longTimeout)
                .setSocketTimeout(longTimeout)
                .build();
    }

    public static String httpGet(String url, Map headers) throws IOException {

        long beginTime = System.currentTimeMillis();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        for (Object o : headers.keySet()) {
            String key = o.toString();
            httpGet.setHeader(new BasicHeader(key, headers.get(key).toString()));
        }
        String result = null;
        try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    result = EntityUtils.toString(entity, "utf-8");
                }
                EntityUtils.consume(entity);
            }
        }
        long endTime = System.currentTimeMillis();
        logger.info("GET [" + url + "] 请求时长：" + (endTime - beginTime));
        return result;
    }

    public static String httpPost(String url, Map headers, Map params) throws IOException {

        long beginTime = System.currentTimeMillis();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        List<NameValuePair> pairs = new ArrayList<>();
        for (Object o : params.keySet()) {
            String key = o.toString();
            pairs.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
        UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(pairs, "utf-8");
        for (Object o : headers.keySet()) {
            String key = o.toString();
            httpPost.setHeader(new BasicHeader(key, headers.get(key).toString()));
        }
        httpPost.setEntity(uefEntity);
        String result = null;
        try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
            HttpEntity entity = httpResponse.getEntity();
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (entity != null) {
                    result = EntityUtils.toString(entity, "utf-8");
                }
                EntityUtils.consume(entity);
            }
        }
        long endTime = System.currentTimeMillis();
        logger.info("POST [" + url + "] 请求时长：" + (endTime - beginTime));
        return result;
    }

    public static String httpPost(String url, Map headers, Map params, RequestConfig customRequestConfig) throws IOException {

        long beginTime = System.currentTimeMillis();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(customRequestConfig);
        List<NameValuePair> pairs = new ArrayList<>();
        for (Object o : params.keySet()) {
            String key = o.toString();
            pairs.add(new BasicNameValuePair(key, params.get(key).toString()));
        }
        UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(pairs, "utf-8");
        for (Object o : headers.keySet()) {
            String key = o.toString();
            httpPost.setHeader(new BasicHeader(key, headers.get(key).toString()));
        }
        httpPost.setEntity(uefEntity);
        String result = null;
        try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
            HttpEntity entity = httpResponse.getEntity();
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (entity != null) {
                    result = EntityUtils.toString(entity, "utf-8");
                }
                EntityUtils.consume(entity);
            }
        }
        long endTime = System.currentTimeMillis();
        logger.info("POST [" + url + "] 请求时长：" + (endTime - beginTime));
        return result;
    }

    public static void main(String[] args) throws IOException {

        String url = "http://www.zimaoda.com";
        Map headers = new HashMap();
        Map params = new HashMap();
        String result = HttpUtil.httpPost(url, headers, params);
        System.out.println(result);
    }
}
