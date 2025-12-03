package io.arkx.framework.media;

import java.awt.image.BufferedImage;

public class ImageScale {

    private int width;

    private int scaleWidth;

    double support = 3.0D;

    double PI = 3.14159265358978D;

    double[] contrib;

    double[] normContrib;

    double[] tmpContrib;

    int startContrib;

    int stopContrib;

    int nDots;

    int nHalfDots;

    public BufferedImage doScale(BufferedImage srcBufferImage, int w, int h) {
        this.width = srcBufferImage.getWidth();
        this.scaleWidth = w;
        calContrib();
        BufferedImage pbOut = horizontalFiltering(srcBufferImage, w);
        BufferedImage pbFinalOut = verticalFiltering(pbOut, h);
        return pbFinalOut;
    }

    private double Lanczos(int i, int inWidth, int outWidth, double Support) {
        double x = i * outWidth / inWidth;
        return Math.sin(x * this.PI) / (x * this.PI) * Math.sin(x * this.PI / Support) / (x * this.PI / Support);
    }

    private void calContrib() {
        this.nHalfDots = ((int) (this.width * this.support / this.scaleWidth));
        this.nDots = (this.nHalfDots * 2 + 1);
        try {
            this.contrib = new double[this.nDots];
            this.normContrib = new double[this.nDots];
            this.tmpContrib = new double[this.nDots];
        } catch (Exception e) {
            System.out.println("init contrib,normContrib,tmpContrib" + e);
        }
        int center = this.nHalfDots;
        this.contrib[center] = 1.0D;
        double weight = 0.0D;
        int i = 0;
        for (i = 1; i <= center; i++) {
            this.contrib[(center + i)] = Lanczos(i, this.width, this.scaleWidth, this.support);
            weight += this.contrib[(center + i)];
        }
        for (i = center - 1; i >= 0; i--) {
            this.contrib[i] = this.contrib[(center * 2 - i)];
        }
        weight = weight * 2.0D + 1.0D;
        for (i = 0; i <= center; i++) {
            this.normContrib[i] = (this.contrib[i] / weight);
        }
        for (i = center + 1; i < this.nDots; i++) {
            this.normContrib[i] = this.normContrib[(center * 2 - i)];
        }
    }

    private void calTempContrib(int start, int stop) {
        double weight = 0.0D;
        int i = 0;
        for (i = start; i <= stop; i++) {
            weight += this.contrib[i];
        }
        for (i = start; i <= stop; i++) {
            this.tmpContrib[i] = (this.contrib[i] / weight);
        }
    }

    private int getRedValue(int rgbValue) {
        int temp = rgbValue & 0xFF0000;
        return temp >> 16;
    }

    private int getGreenValue(int rgbValue) {
        int temp = rgbValue & 0xFF00;
        return temp >> 8;
    }

    private int getBlueValue(int rgbValue) {
        return rgbValue & 0xFF;
    }

    private int comRGB(int redValue, int greenValue, int blueValue) {
        return (redValue << 16) + (greenValue << 8) + blueValue;
    }

    private int horizontalFilter(BufferedImage bufImg, int startX, int stopX, int start, int stop, int y,
            double[] pContrib) {
        double valueRed = 0.0D;
        double valueGreen = 0.0D;
        double valueBlue = 0.0D;
        int valueRGB = 0;

        int i = startX;
        for (int j = start; i <= stopX; j++) {
            valueRGB = bufImg.getRGB(i, y);

            valueRed += getRedValue(valueRGB) * pContrib[j];
            valueGreen += getGreenValue(valueRGB) * pContrib[j];
            valueBlue += getBlueValue(valueRGB) * pContrib[j];
            i++;
        }
        valueRGB = comRGB(clip((int) valueRed), clip((int) valueGreen), clip((int) valueBlue));
        return valueRGB;
    }

    private BufferedImage horizontalFiltering(BufferedImage bufImage, int iOutW) {
        int dwInW = bufImage.getWidth();
        int dwInH = bufImage.getHeight();
        int value = 0;
        BufferedImage pbOut = new BufferedImage(iOutW, dwInH, 1);
        for (int x = 0; x < iOutW; x++) {
            int X = (int) (x * dwInW / iOutW + 0.5D);
            int y = 0;
            int startX = X - this.nHalfDots;
            int start;
            if (startX < 0) {
                startX = 0;
                start = this.nHalfDots - X;
            } else {
                start = 0;
            }
            int stopX = X + this.nHalfDots;
            int stop;
            if (stopX > dwInW - 1) {
                stopX = dwInW - 1;
                stop = this.nHalfDots + (dwInW - 1 - X);
            } else {
                stop = this.nHalfDots * 2;
            }
            if ((start > 0) || (stop < this.nDots - 1)) {
                calTempContrib(start, stop);
                for (y = 0; y < dwInH; y++) {
                    value = horizontalFilter(bufImage, startX, stopX, start, stop, y, this.tmpContrib);
                    pbOut.setRGB(x, y, value);
                }
            } else {
                for (y = 0; y < dwInH; y++) {
                    value = horizontalFilter(bufImage, startX, stopX, start, stop, y, this.normContrib);
                    pbOut.setRGB(x, y, value);
                }
            }
        }
        return pbOut;
    }

    private int verticalFilter(BufferedImage pbInImage, int startY, int stopY, int start, int stop, int x,
            double[] pContrib) {
        double valueRed = 0.0D;
        double valueGreen = 0.0D;
        double valueBlue = 0.0D;
        int valueRGB = 0;

        int i = startY;
        for (int j = start; i <= stopY; j++) {
            valueRGB = pbInImage.getRGB(x, i);
            valueRed += getRedValue(valueRGB) * pContrib[j];
            valueGreen += getGreenValue(valueRGB) * pContrib[j];
            valueBlue += getBlueValue(valueRGB) * pContrib[j];
            i++;
        }
        valueRGB = comRGB(clip((int) valueRed), clip((int) valueGreen), clip((int) valueBlue));
        return valueRGB;
    }

    private BufferedImage verticalFiltering(BufferedImage pbImage, int iOutH) {
        int iW = pbImage.getWidth();
        int iH = pbImage.getHeight();
        int value = 0;
        BufferedImage pbOut = new BufferedImage(iW, iOutH, 1);
        for (int y = 0; y < iOutH; y++) {
            int Y = (int) (y * iH / iOutH + 0.5D);
            int startY = Y - this.nHalfDots;
            int start;
            if (startY < 0) {
                startY = 0;
                start = this.nHalfDots - Y;
            } else {
                start = 0;
            }
            int stopY = Y + this.nHalfDots;
            int stop;
            if (stopY > iH - 1) {
                stopY = iH - 1;
                stop = this.nHalfDots + (iH - 1 - Y);
            } else {
                stop = this.nHalfDots * 2;
            }
            if ((start > 0) || (stop < this.nDots - 1)) {
                calTempContrib(start, stop);
                for (int x = 0; x < iW; x++) {
                    value = verticalFilter(pbImage, startY, stopY, start, stop, x, this.tmpContrib);
                    pbOut.setRGB(x, y, value);
                }
            } else {
                for (int x = 0; x < iW; x++) {
                    value = verticalFilter(pbImage, startY, stopY, start, stop, x, this.normContrib);
                    pbOut.setRGB(x, y, value);
                }
            }
        }
        return pbOut;
    }

    private int clip(int x) {
        if (x < 0) {
            return 0;
        }
        if (x > 255) {
            return 255;
        }
        return x;
    }

}
