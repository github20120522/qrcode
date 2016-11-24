package com.zimaoda.form;

import com.zimaoda.context.QRCodeContext;
import com.zimaoda.service.QRCodeService;
import com.zimaoda.util.BaseUtil;
import com.zimaoda.util.QRCodeUtil;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Administrator on 2016/11/23 0023.
 */
@SuppressWarnings("unchecked")
public class MainForm {

    private QRCodeContext qrCodeContext = QRCodeContext.getInstance();

    private JTextField taxRegion;
    private JList leftList;
    private JButton createBtn;
    private JTextPane artInfo;
    private JTextField artId;
    private JTextField batch;
    private JTextField createCount;
    private JButton printBtn;
    private JButton pauseBtn;
    private JButton completeBtn;
    private JLabel leftCount;
    private JPanel mainPanel;
    private JLabel completeCount;

    public MainForm() {
        init();
        initListeners();
    }

    private void init() {

        qrCodeContext.setMainForm(this);
        QRCodeService.dataLoad();
        // 开启二维码打印服务，当有数据进入队列时对数据进行整理打印
        QRCodeUtil.startServer();
    }

    private void initListeners() {

        createBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (createBtn.isEnabled()) {
                    String taxRegionStr = taxRegion.getText();
                    String artIdStr = artId.getText();
                    String batchStr = batch.getText();
                    String createCountStr = createCount.getText();
                    if (StringUtils.isNotBlank(taxRegionStr) &&
                            StringUtils.isNotBlank(artIdStr) &&
                            StringUtils.isNotBlank(batchStr) &&
                            StringUtils.isNotBlank(createCountStr)) {
                        createBtn.setEnabled(false);
                        qrCodeContext.getExecutorService().execute(new Runnable() {
                            @Override
                            public void run() {
                                QRCodeService.createQRCode(taxRegionStr, artIdStr, batchStr, createCountStr);
                            }
                        });
                    } else {
                        BaseUtil.messageDialog("请补全必要信息");
                    }
                }
            }
        });

        printBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (printBtn.isEnabled()) {
                    QRCodeService.startPrint();
                }
            }
        });

        pauseBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (pauseBtn.isEnabled()) {
                    QRCodeService.pausePrint();
                }
            }
        });

        completeBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (completeBtn.isEnabled()) {
                    QRCodeService.complete();
                }
            }
        });
    }

    public QRCodeContext getQrCodeContext() {
        return qrCodeContext;
    }

    public JList getLeftList() {
        return leftList;
    }

    public JButton getCreateBtn() {
        return createBtn;
    }

    public JTextPane getArtInfo() {
        return artInfo;
    }

    public JButton getPrintBtn() {
        return printBtn;
    }

    public JButton getPauseBtn() {
        return pauseBtn;
    }

    public JButton getCompleteBtn() {
        return completeBtn;
    }

    public JLabel getLeftCount() {
        return leftCount;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JLabel getCompleteCount() {
        return completeCount;
    }

}
