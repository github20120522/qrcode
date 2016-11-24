package com.zimaoda.client;

import com.zimaoda.util.BaseUtil;
import com.zimaoda.util.Constants;
import com.zimaoda.util.HttpUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class ZmdClient {

    private static Logger logger = LoggerFactory.getLogger(ZmdClient.class);

    private static String aesKey;

    private static String createQRCodeUrl;

    private static String printCallbackUrl;

    static {
        try (InputStream inputStream = ZmdClient.class.getClassLoader().getResourceAsStream("system.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            String basePath = properties.getProperty("basePath");
            aesKey = properties.getProperty("aesKey");
            createQRCodeUrl = basePath + properties.getProperty("createQRCodeUrl");
            printCallbackUrl = basePath + properties.getProperty("printCallbackUrl");
        } catch (IOException e) {
            String error = BaseUtil.getExceptionStackTrace(e);
            logger.error(error);
        }
    }

    public static Map createQRCode(String taxRegion, String artId, String batch, String createCount) throws IOException {
        Map<String, Object> params = new HashMap<>();
        String today = BaseUtil.ymdDateFormat(new Date());
        params.put("date", today);
        params.put("sign", DigestUtils.sha1Hex(today + Constants.simpleKey));
        params.put("taxRegion", taxRegion);
        params.put("artId", artId);
        params.put("batch", batch);
        params.put("createCount", createCount);
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        String resultStr = HttpUtil.httpPost(createQRCodeUrl, headers, params);
        return BaseUtil.parseJson(resultStr, Map.class);
    }

    public static Map printCallback(String successJson, String failureJson) throws IOException {
        Map<String, Object> params = new HashMap<>();
        String today = BaseUtil.ymdDateFormat(new Date());
        params.put("date", today);
        params.put("sign", DigestUtils.sha1Hex(today + Constants.simpleKey));
        params.put("successJson", successJson);
        params.put("failureJson", failureJson);
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        String resultStr = HttpUtil.httpPost(printCallbackUrl, headers, params);
        return BaseUtil.parseJson(resultStr, Map.class);
    }

}
