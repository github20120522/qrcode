package com.zimaoda.service;

import com.zimaoda.client.ZmdClient;
import com.zimaoda.context.QRCodeContext;
import com.zimaoda.util.BaseUtil;
import com.zimaoda.util.PrintUtil;
import com.zimaoda.util.QRCodeUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
@SuppressWarnings("unchecked")
public class QRCodeService {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeService.class);

    private static final QRCodeContext qrCodeContext = QRCodeContext.getInstance();

    private static final String LEFT_DATA_FILE = "./qrCodeData/qrCode.leftData";

    private static final String COMPLETE_DATA_FILE = "./qrCodeData/qrCode.completeData";

    private static final String INFO_DATA_FILE = "./qrCodeData/qrCode.infoData";

    public static void dataLoad() {

        // 读取本地数据
        File leftDataFile = new File(LEFT_DATA_FILE);
        try {
            if (leftDataFile.exists()) {
                List<String> leftDataList = FileUtils.readLines(leftDataFile, Charset.forName("utf-8"));
                for (String leftData : leftDataList) {
                    qrCodeContext.getLeftVector().add(leftData);
                }
            }
        } catch (IOException e) {
            logger.error(BaseUtil.getExceptionStackTrace(e));
        }

        File completeDataFile = new File(COMPLETE_DATA_FILE);
        try {
            if (completeDataFile.exists()) {
                List<String> completeDataList = FileUtils.readLines(completeDataFile, Charset.forName("utf-8"));
                for (String completeData : completeDataList) {
                    qrCodeContext.getCompleteVector().add(completeData);
                }
            }
        } catch (IOException e) {
            logger.error(BaseUtil.getExceptionStackTrace(e));
        }

        File artInfoDataFile = new File(INFO_DATA_FILE);
        try {
            if (artInfoDataFile.exists()) {
                String artInfo = FileUtils.readFileToString(artInfoDataFile, Charset.forName("utf-8"));
                qrCodeContext.setArtInfo(artInfo);
            } else {
                qrCodeContext.setArtInfo("");
            }
        } catch (IOException e) {
            logger.error(BaseUtil.getExceptionStackTrace(e));
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                qrCodeContext.getMainForm().getLeftList().setListData(qrCodeContext.getLeftVector());
                qrCodeContext.getMainForm().getLeftList().updateUI();
                qrCodeContext.getMainForm().getArtInfo().setText(qrCodeContext.getArtInfo());
                qrCodeContext.getMainForm().getArtInfo().updateUI();
                qrCodeContext.getMainForm().getLeftCount().setText(qrCodeContext.getLeftVector().size() + "");
                qrCodeContext.getMainForm().getCompleteCount().setText(qrCodeContext.getCompleteVector().size() + "");

                // 当有待打印时，禁用生成二维码按钮，禁用暂停按钮，保留打印和完成按钮
                // 否则禁用打印相关按钮，保留生成二维码按钮
                if (qrCodeContext.getLeftVector().size() > 0 || StringUtils.isNotBlank(qrCodeContext.getArtInfo()) || qrCodeContext.getCompleteVector().size() > 0) {
                    qrCodeContext.getMainForm().getCreateBtn().setEnabled(false);
                    qrCodeContext.getMainForm().getPauseBtn().setEnabled(false);
                } else {
                    qrCodeContext.getMainForm().getPrintBtn().setEnabled(false);
                    qrCodeContext.getMainForm().getPauseBtn().setEnabled(false);
                    qrCodeContext.getMainForm().getCompleteBtn().setEnabled(false);
                }
            }
        });
    }

    private static void dataStore(String artInfo, Vector<String> leftVector, Vector<String> completeVector) {

        // 存储数据到本地
        File leftDataFile = new File(LEFT_DATA_FILE);
        try {
            if (leftVector != null && !leftVector.isEmpty()) {
                FileUtils.writeLines(leftDataFile, leftVector);
            } else {
                if (leftDataFile.exists()) {
                    FileUtils.deleteQuietly(leftDataFile);
                }
            }
        } catch (IOException e) {
            logger.error(BaseUtil.getExceptionStackTrace(e));
        }

        File completeDataFile = new File(COMPLETE_DATA_FILE);
        try {
            if (completeVector != null && !completeVector.isEmpty()) {
                FileUtils.writeLines(completeDataFile, completeVector);
            } else {
                if (completeDataFile.exists()) {
                    FileUtils.deleteQuietly(completeDataFile);
                }
            }
        } catch (IOException e) {
            logger.error(BaseUtil.getExceptionStackTrace(e));
        }

        File artInfoDataFile = new File(INFO_DATA_FILE);
        try {
            if (StringUtils.isNotBlank(artInfo)) {
                FileUtils.writeStringToFile(artInfoDataFile, artInfo);
            } else {
                if (artInfoDataFile.exists()) {
                    FileUtils.deleteQuietly(artInfoDataFile);
                }
            }
        } catch (IOException e) {
            logger.error(BaseUtil.getExceptionStackTrace(e));
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (leftVector != null && leftVector.size() > 0) {
                    qrCodeContext.setLeftVector(leftVector);
                } else {
                    qrCodeContext.setLeftVector(new Vector<>());
                }
                if (completeVector != null && completeVector.size() > 0) {
                    qrCodeContext.setCompleteVector(completeVector);
                } else {
                    qrCodeContext.setCompleteVector(new Vector<>());
                }
                if (StringUtils.isNotBlank(artInfo)) {
                    qrCodeContext.setArtInfo(artInfo);
                } else {
                    qrCodeContext.setArtInfo("");
                }
                qrCodeContext.getMainForm().getLeftList().setListData(qrCodeContext.getLeftVector());
                qrCodeContext.getMainForm().getLeftList().updateUI();
                qrCodeContext.getMainForm().getArtInfo().setText(qrCodeContext.getArtInfo());
                qrCodeContext.getMainForm().getArtInfo().updateUI();
                qrCodeContext.getMainForm().getLeftCount().setText(qrCodeContext.getLeftVector().size() + "");
                qrCodeContext.getMainForm().getCompleteCount().setText(qrCodeContext.getCompleteVector().size() + "");
            }
        });
    }

    private static void dataBack(String artInfo, Vector<String> leftVector, Vector<String> completeVector) throws IOException {
        Map<String, Object> backData = new HashMap<>();
        backData.put("artInfo", artInfo);
        backData.put("failureVector", leftVector);
        backData.put("successVector", completeVector);
        Date today = new Date();
        String[] dates = BaseUtil.ymdDateFormat(today).split("-");
        File backDataFile = new File("./backQRCodeData/" + (dates[0] + dates[1] + dates[2]) + "/" + System.currentTimeMillis() + ".backData");
        FileUtils.writeStringToFile(backDataFile, BaseUtil.toJson(backData));
    }

    public static void createQRCode(String taxRegion, String artId, String batch, String createCount) {
        try {
            Map result = ZmdClient.createQRCode(taxRegion, artId, batch, createCount);
            if (Boolean.valueOf(result.get("success").toString())) {
                String artInfo = result.get("artInfo").toString();
                Vector<String> leftVector = BaseUtil.parseJson(result.get("data").toString(), Vector.class);
                dataStore(artInfo, leftVector, null);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        // 生成二维码按钮置为无效，打印按钮开启，完成按钮开启，待打印列表刷新，待打印数量刷新
                        qrCodeContext.getMainForm().getPrintBtn().setEnabled(true);
                        qrCodeContext.getMainForm().getCompleteBtn().setEnabled(true);
                        qrCodeContext.getMainForm().getLeftList().updateUI();
                        qrCodeContext.getMainForm().getLeftCount().setText(leftVector.size() + "");
                    }
                });
            } else {
                qrCodeContext.getMainForm().getCreateBtn().setEnabled(true);
                BaseUtil.messageDialog(result.get("message").toString());
            }
        } catch (Exception e) {
            qrCodeContext.getMainForm().getCreateBtn().setEnabled(true);
            String error = BaseUtil.getExceptionStackTrace(e);
            logger.error(error);
            BaseUtil.textAreaErrorDialog("错误", error);
        }
    }

    public static void startPrint() {
        if (qrCodeContext.isRunning()) {
            return ;
        } else {
            qrCodeContext.setRunning(true);
        }
        if (!PrintUtil.printServiceCheck()) {
            BaseUtil.messageDialog("没有找到默认打印机或者打印服务没有启动");
            return ;
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                qrCodeContext.getMainForm().getPrintBtn().setEnabled(false);
                qrCodeContext.getMainForm().getCompleteBtn().setEnabled(false);
                qrCodeContext.getMainForm().getPauseBtn().setEnabled(true);
            }
        });
        qrCodeContext.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!qrCodeContext.isRunning()) {
                        break;
                    }
                    if (QRCodeUtil.nowQueueSize() <= 10) {
                        Vector<String> leftVector = qrCodeContext.getLeftVector();
                        if (leftVector.size() > 0) {
                            Vector<String> completeVector = qrCodeContext.getCompleteVector();
                            int max = 100;
                            if (leftVector.size() < 100) {
                                max = leftVector.size();
                            }
                            List<String> buf = new ArrayList<>();
                            int i = 0;
                            while (i < max) {
                                String qrCode = leftVector.get(0);
                                completeVector.add(qrCode);
                                buf.add(qrCode);
                                leftVector.remove(0);
                                i++;
                            }
                            dataStore(qrCodeContext.getArtInfo(), leftVector, completeVector);
                            try {
                                QRCodeUtil.easyQRCodeBatchSave(buf);
                            } catch (Exception e) {
                                String error = BaseUtil.getExceptionStackTrace(e);
                                logger.error(error);
                                BaseUtil.textAreaErrorDialog("错误", error);
                            }
                        } else {
                            // done 完成
                            complete();
                            break;
                        }
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public static void complete() {
        qrCodeContext.setRunning(false);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                qrCodeContext.getMainForm().getPrintBtn().setEnabled(false);
                qrCodeContext.getMainForm().getPauseBtn().setEnabled(false);
                qrCodeContext.getMainForm().getCompleteBtn().setEnabled(false);
            }
        });
        qrCodeContext.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String successJson = BaseUtil.toJson(qrCodeContext.getCompleteVector());
                    String failureJson = BaseUtil.toJson(qrCodeContext.getLeftVector());
                    Map result = ZmdClient.printCallback(successJson, failureJson);
                    if (Boolean.valueOf(result.get("success").toString())) {
                        // 打印完成的数据进行一个备份
                        dataBack(qrCodeContext.getArtInfo(), qrCodeContext.getLeftVector(), qrCodeContext.getCompleteVector());
                        dataStore("", null, null);
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                qrCodeContext.getMainForm().getCreateBtn().setEnabled(true);
                                BaseUtil.messageDialog("成功回执服务器，请打印完成后再关闭本程序");
                            }
                        });
                    } else {
                        qrCodeContext.getMainForm().getPrintBtn().setEnabled(true);
                        qrCodeContext.getMainForm().getCompleteBtn().setEnabled(true);
                        BaseUtil.messageDialog(result.get("message").toString());
                    }
                } catch (Exception e) {
                    qrCodeContext.getMainForm().getPrintBtn().setEnabled(true);
                    qrCodeContext.getMainForm().getCompleteBtn().setEnabled(true);
                    String error = BaseUtil.getExceptionStackTrace(e);
                    logger.error(error);
                    BaseUtil.textAreaErrorDialog("错误", error);
                }

            }
        });
    }

    public static void pausePrint() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                qrCodeContext.setRunning(false);
                qrCodeContext.getMainForm().getPrintBtn().setEnabled(true);
                qrCodeContext.getMainForm().getPauseBtn().setEnabled(false);
                qrCodeContext.getMainForm().getCompleteBtn().setEnabled(true);
            }
        });
    }

}
