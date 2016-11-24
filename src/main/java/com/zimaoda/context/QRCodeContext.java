package com.zimaoda.context;

import com.zimaoda.form.MainForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/11/23 0023.
 */
@SuppressWarnings("unchecked")
public class QRCodeContext {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeContext.class);

    private QRCodeContext() {}

    private static final QRCodeContext context = new QRCodeContext();

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private MainForm mainForm;

    private Vector<String> leftVector = new Vector<>();

    private Vector<String> completeVector = new Vector<>();

    private String artInfo;

    private boolean running = false;

    public static QRCodeContext getInstance() {
        return context;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public MainForm getMainForm() {
        return mainForm;
    }

    public void setMainForm(MainForm mainForm) {
        this.mainForm = mainForm;
    }

    public Vector<String> getLeftVector() {
        return leftVector;
    }

    public void setLeftVector(Vector<String> leftVector) {
        this.leftVector = leftVector;
    }

    public Vector<String> getCompleteVector() {
        return completeVector;
    }

    public void setCompleteVector(Vector<String> completeVector) {
        this.completeVector = completeVector;
    }

    public String getArtInfo() {
        return artInfo;
    }

    public void setArtInfo(String artInfo) {
        this.artInfo = artInfo;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
