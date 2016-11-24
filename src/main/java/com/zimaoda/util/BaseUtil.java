package com.zimaoda.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fz on 2015/12/30.
 */
public class BaseUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMM");

    private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    public static <T> T parseJson(String json, Class<T> clazz) throws IOException {
        return objectMapper.readValue(json, clazz);
    }

    public static String getExceptionStackTrace(Exception e) {

        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw, true);
            e.printStackTrace(pw);
            return sw.toString();
        } finally {
            try {
                if (sw != null)
                    sw.close();
                if (pw != null)
                    pw.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void messageDialog(String message) {

        JOptionPane.showMessageDialog(null, message);
    }

    public static void textAreaErrorDialog(String title, String content) {

        if (content.contains("HttpHostConnectException")) {
            content = "网络异常，连接不到服务器，请检查网络后重试\n" + content;
        } else if (content.contains("UnknownHostException")) {
            content = "本地网络异常，无法访问服务器，请检查网络并重试\n" + content;
        } else if (content.contains("SocketTimeoutException")) {
            content = "请求超时，请重试\n" + content;
        } else if (content.contains("NoRouteToHostException")) {
            content = "网络异常，请检查网络并重试\n" + content;
        }
        JTextArea textArea = new JTextArea(content);
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        Font font = textArea.getFont();
        float size = font.getSize();
        textArea.setFont(font.deriveFont(size));
        scrollPane.setPreferredSize(new Dimension(930, 430));
        JOptionPane.showInputDialog(null, scrollPane, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static String ymdDateFormat(Date date) {

        return sdf.format(date);
    }

    public static String ymDateFormat(Date date) {

        return sdf2.format(date);
    }

    public static String ymdHmsDateFormat(Date date) {

        return sdf3.format(date);
    }

}
