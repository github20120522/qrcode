package com.zimaoda.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * Created by fz on 2016/4/1.
 */
public class PngPrintable implements Printable {

    private BufferedImage bufferedImage;

    private int x;
    private int y;
    private int w;
    private int h;

    public PngPrintable(BufferedImage bufferedImage, int x, int y, int w, int h) {

        this.bufferedImage = bufferedImage;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Paper paper = new Paper();
        paper.setImageableArea(x, y, w, h);
        paper.setSize(w, h);
        pageFormat.setPaper(paper);

        graphics.drawImage(bufferedImage, x, y, null);
        return PAGE_EXISTS;
    }
}
