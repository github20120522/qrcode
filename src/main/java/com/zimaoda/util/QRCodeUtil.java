package com.zimaoda.util;

import com.google.common.collect.Lists;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by fz on 2016/3/31.
 */
@SuppressWarnings("unchecked")
public class QRCodeUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(QRCodeUtil.class);

    // 单张图片宽度
    private static final int width = 61;
    // 单张图片高度
    private static final int height = 61;
    // 单图向左裁剪大小
    private static final int imageOffsetX = 0;
    // 单图向右增加的大小
    private static final int newImageAddWidth = 34;
    // 列数
    private static final int columnNum = 3;
    // 图片足够几张时开始拼接图片
    private static final int countUnit = 3;
    // 合并图片宽度
    private static final int mergeWidth = columnNum * (width + newImageAddWidth);
    // 合并图片高度
    private static final int mergeHeight = countUnit / columnNum * height;
    // x轴起点
    private static final int x = 0;
    // y轴起点
    private static final int y = 0;
    // 图片不够countUnit时的重复尝试次数
    private static final int reTryTime = 2;
    // 当前重复尝试次数
    private static int currentTryTime = 1;
    // 每次尝试的等待时间
    private static final int waitTime = 2 * 1000;

    private static ExecutorService pool = Executors.newFixedThreadPool(7);

    private static ArrayBlockingQueue<BufferedImage> qrImagesQueue = new ArrayBlockingQueue<>(100);

    private static ArrayBlockingQueue<BufferedImage> qrMergeImagesQueue = new ArrayBlockingQueue<>(100);

    private static AtomicInteger printAtomicInteger = new AtomicInteger(1);

    private static AtomicInteger mergeAtomicInteger = new AtomicInteger(1);

    private static Hashtable ht = new Hashtable();

    static {
        ht.put(EncodeHintType.CHARACTER_SET, "utf-8");
        ht.put(EncodeHintType.MARGIN, 1);
    }

    private static BufferedImage createQRCodePngImg(String url, int width, int height) throws Exception {

        BitMatrix bitMatrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height, ht);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

    private static void easyQRCodeSave(String url) throws Exception {

        BufferedImage image = createQRCodePngImg(url, width, height);

        BufferedImage newImage = new BufferedImage(image.getWidth() + newImageAddWidth, image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) newImage.getGraphics();
        g.setColor(Color.WHITE);
        g.fill(new Rectangle2D.Double(0, 0, image.getWidth() + newImageAddWidth, image.getHeight()));
        g.drawImage(image, imageOffsetX, 0, null);

        g.setColor(Color.BLACK);
        Font font = new Font("宋体", Font.PLAIN, 12);
        g.setFont(font);

        int fontOffset = 1;

        g.drawString("谢", image.getWidth() - 11 * fontOffset, 27);
        g.drawString("谢", image.getWidth() + 4 * fontOffset, 27);
        g.drawString("惠", image.getWidth() - 11 * fontOffset, 42);
        g.drawString("顾", image.getWidth() + 4 * fontOffset, 42);

        qrImagesQueue.put(newImage);
    }

    public static void easyQRCodeBatchSave(List<String> urls) throws Exception {

        for (String url : urls) {
            BufferedImage image = createQRCodePngImg(url, width, height);

            BufferedImage newImage = new BufferedImage(image.getWidth() + newImageAddWidth, image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = (Graphics2D) newImage.getGraphics();
            g.setColor(Color.WHITE);
            g.fill(new Rectangle2D.Double(0, 0, image.getWidth() + newImageAddWidth, image.getHeight()));
            g.drawImage(image, imageOffsetX, 0, null);

            g.setColor(Color.BLACK);
            Font font = new Font("宋体", Font.PLAIN, 12);
            g.setFont(font);

            int fontOffset = 1;

            g.drawString("谢", image.getWidth() - 11 * fontOffset, 27);
            g.drawString("谢", image.getWidth() + 4 * fontOffset, 27);
            g.drawString("惠", image.getWidth() - 11 * fontOffset, 42);
            g.drawString("顾", image.getWidth() + 4 * fontOffset, 42);

            logger.debug("[生成]二维码生成：" + url);
            qrImagesQueue.put(newImage);
        }
    }

    private static void qrCodeMerge() throws InterruptedException {

        while (true) {

            if (qrImagesQueue.size() == 0) {
                // logger.debug("[合并]没有发现新的二维码，等待新二维码生成");
                Thread.sleep(waitTime);
                continue;
            }

            if (qrImagesQueue.size() < countUnit && currentTryTime <= reTryTime) {
                logger.debug("[合并]二维码合并，第" + currentTryTime + "次重复尝试");
                currentTryTime++;
                Thread.sleep(waitTime);
                continue;
            }

            // 重置尝试次数
            currentTryTime = 1;
            logger.debug("[合并]尝试次数重置为1");

            // done 根据图片数量，计算图片行数
            int imageCount = qrImagesQueue.size() > countUnit ? countUnit : qrImagesQueue.size();
            // 取出imageCount个图片，放入一个单独的数组
            List<BufferedImage> bufferedImages = Lists.newArrayList();
            for (int i=0; i<imageCount; i++) {
                bufferedImages.add(qrImagesQueue.take());
            }

            int rowNum = imageCount / columnNum;
            if (imageCount % columnNum > 0) {
                rowNum += 1;
            }

            BufferedImage combined = new BufferedImage(mergeWidth, mergeHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics g = combined.getGraphics();

            boolean breakFlag = false;
            for (int i=0; i<rowNum; i++) {
                for (int j=0; j<columnNum; j++) {
                    if (breakFlag) {
                        break;
                    }
                    if ((i * columnNum + j) >= bufferedImages.size()) {
                        breakFlag = true;
                    } else {
                        BufferedImage image = bufferedImages.get(j);
                        g.drawImage(image, j * image.getWidth(), i * image.getHeight(), null);
                    }
                }
                if (breakFlag) {
                    break;
                }
            }
            qrMergeImagesQueue.put(combined);
            logger.debug("[合并]" + imageCount + "张二维码成功合并，第" + mergeAtomicInteger.getAndIncrement() + "张");
            Thread.sleep(50);
        }
    }

    private static void mergeImgPrint() throws InterruptedException {

        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        pras.add(new MediaPrintableArea(0f, 0f, mergeWidth, mergeHeight, MediaPrintableArea.INCH));
        while (true) {
            BufferedImage mergeBufferedImage = qrMergeImagesQueue.take();
            PngPrintable pngPrintable = new PngPrintable(mergeBufferedImage, x, y, mergeWidth, mergeHeight);
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        logger.debug("[打印]合并二维码打印，第" + printAtomicInteger.getAndIncrement() + "张");
                        PrintUtil.printPrintable(pras, pngPrintable);
                    } catch (Exception e) {
                        logger.debug("[打印]打印出现异常，错误信息：" + e.getMessage());
                        logger.error(BaseUtil.getExceptionStackTrace(e));
                    }
                }
            });
            Thread.sleep(100);
        }
    }

    public static void startServer() {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    qrCodeMerge();
                } catch (Exception e) {
                    String error = BaseUtil.getExceptionStackTrace(e);
                    logger.error(error);
                    BaseUtil.textAreaErrorDialog("严重错误", error);
                    System.exit(0);
                }
            }
        });
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mergeImgPrint();
                } catch (InterruptedException e) {
                    String error = BaseUtil.getExceptionStackTrace(e);
                    logger.error(error);
                    BaseUtil.textAreaErrorDialog("严重错误", error);
                    System.exit(0);
                }
            }
        });
    }

    public static int nowQueueSize() {
        return qrImagesQueue.size();
    }

    public static void main(String[] args) throws Exception {

        // 单存合并
        for (int i=0; i<100; i++) {
            easyQRCodeSave("http://zimaoda.cn/webAPI/1859_20170630/1AC8" + i);
        }
        Runnable merge = new Runnable() {
            @Override
            public void run() {
                try {
                    qrCodeMerge();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(merge).start();
        // 合并图打印
        Runnable mergePrint = new Runnable() {
            @Override
            public void run() {
                try {
                    mergeImgPrint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(mergePrint).start();

    }
}
