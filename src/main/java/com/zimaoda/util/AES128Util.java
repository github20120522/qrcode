package com.zimaoda.util;

import com.zimaoda.client.ZmdClient;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by fz on 2015/11/18.
 */
public class AES128Util {

    private static final String DEFAULT_CHARSET = "utf-8";

    private static final String ALGORITHM = "AES";

    private static final String TRANSFORMATION = "AES";

    private static final int BITS = 128;

    private static final Map<String, SecretKeySpec> secretKeySpecCache = new HashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(AES128Util.class);

    static {
        init();
    }

    private static void init() {
        try (InputStream inputStream = ZmdClient.class.getClassLoader().getResourceAsStream("system.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
            String aesKey = properties.getProperty("aesKey");
            secretKeySpecCache.put(aesKey, getAESSecretKey(aesKey));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SecretKeySpec getAESSecretKey(String base64KeyWord) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        // 防止linux下随机生成key
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(Base64.decodeBase64(base64KeyWord));
        // 根据密钥初始化密钥生成器
        keyGen.init(BITS, secureRandom);
        SecretKey secretKey = keyGen.generateKey();
        return new SecretKeySpec(secretKey.getEncoded(), ALGORITHM);
    }

    public static String encrypt(String base64KeyWord, String content) {
        try {
            long beginTime = System.currentTimeMillis();
            SecretKeySpec key;
            if (secretKeySpecCache.containsKey(base64KeyWord)) {
                key = secretKeySpecCache.get(base64KeyWord);
            } else {
                key = getAESSecretKey(base64KeyWord);
                secretKeySpecCache.put(base64KeyWord, key);
            }
            byte[] byteContent = content.getBytes(DEFAULT_CHARSET);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(byteContent);
            byte[] encode = Base64.encodeBase64(result);
            long endTime = System.currentTimeMillis();
            logger.debug("加密时长：" + (endTime - beginTime));
            return new String(encode, DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("加密出错", e);
        }
    }

    public static String decrypt(String base64KeyWord, String content) {
        try {
            long beginTime = System.currentTimeMillis();
            SecretKeySpec key;
            if (secretKeySpecCache.containsKey(base64KeyWord)) {
                key = secretKeySpecCache.get(base64KeyWord);
            } else {
                key = getAESSecretKey(base64KeyWord);
                secretKeySpecCache.put(base64KeyWord, key);
            }
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(Base64.decodeBase64(content));
            long endTime = System.currentTimeMillis();
            logger.debug("解密时长：" + (endTime - beginTime));
            return new String(result, DEFAULT_CHARSET);
        } catch (Exception e) {
            throw new RuntimeException("解密出错", e);
        }
    }

    public static void main(String[] args) throws UnsupportedEncodingException {

        String keyWord = "today_is_a_sunny_day";
        byte[] base64KeyBytes = Base64.encodeBase64(keyWord.getBytes(DEFAULT_CHARSET));

        String base64KeyWord = new String(base64KeyBytes, DEFAULT_CHARSET);
        System.out.println("加密密钥：" + base64KeyWord);

        String content = "abc";
        String encryptContent = encrypt(base64KeyWord, content);
        System.out.println("加密后内容：" + encryptContent);
        String decryptContent = decrypt(base64KeyWord, encryptContent);
        System.out.println("解密后内容：" + decryptContent);

        String anotherContent = "123";
        String cacheEncryptContent = encrypt(base64KeyWord, anotherContent);
        System.out.println("缓存key加密后内容：" + cacheEncryptContent);
        String cacheDecryptContent = decrypt(base64KeyWord, cacheEncryptContent);
        System.out.println("缓存key解密后内容：" + cacheDecryptContent);
    }
}
