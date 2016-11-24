package com.zimaoda.main;

import com.zimaoda.form.MainForm;
import com.zimaoda.util.BaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by Administrator on 2016/11/23 0023.
 */
public class MainGUI {
    private static final Logger logger = LoggerFactory.getLogger(MainGUI.class);

    public static void main(String[] args) throws IOException {

        JFrame frame = new JFrame();
        MainForm mainForm = new MainForm();
        frame.setContentPane(mainForm.getMainPanel());
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocation(300, 150);
        frame.pack();
        frame.setVisible(true);
        frame.setTitle("二维码打印");

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (mainForm.getQrCodeContext().isRunning()) {
                    BaseUtil.messageDialog("请先暂停打印然后再进行关闭");
                } else {
                    System.exit(0);
                }
            }
        });
    }
}
