package com.zimaoda.util;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.PrintRequestAttributeSet;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * Created by fz on 2016/4/1.
 */
public class PrintUtil {

    private static PrintService ps = null;

    private static PrintService getPrintService() throws PrinterException {

        if (ps == null) {
            ps = PrintServiceLookup.lookupDefaultPrintService();
        }
        // for available test
        System.out.println(ps.getName());
        return ps;
    }

    public static boolean printServiceCheck() {
        try {
            getPrintService();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void printPrintable(PrintRequestAttributeSet pras, Printable printable) throws PrinterException {

        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintable(printable);
        printerJob.setPrintService(getPrintService());
        printerJob.print(pras);
    }
}
