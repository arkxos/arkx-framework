package org.ark.framework.media;

import java.awt.*;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.io.OutputStream;

public class GifEncoder {

    short width_;

    short height_;

    int numColors_;

    byte[] pixels_;

    byte[] colors_;

    ScreenDescriptor sd_;

    ImageDescriptor id_;

    public GifEncoder(Image image) throws AWTException {
        this.width_ = (short) image.getWidth(null);
        this.height_ = (short) image.getHeight(null);

        int[] values = new int[this.width_ * this.height_];
        PixelGrabber grabber = new PixelGrabber(image, 0, 0, this.width_, this.height_, values, 0, this.width_);
        try {
            if (!grabber.grabPixels()) {
                throw new AWTException("Grabber returned false: " + grabber.status());
            }
        } catch (InterruptedException ex) {
            byte[][] r = new byte[this.width_][this.height_];
            byte[][] g = new byte[this.width_][this.height_];
            byte[][] b = new byte[this.width_][this.height_];
            int index = 0;
            for (int y = 0; y < this.height_; y++)
                for (int x = 0; x < this.width_; x++) {
                    r[x][y] = (byte) (values[index] >> 16 & 0xFF);
                    g[x][y] = (byte) (values[index] >> 8 & 0xFF);
                    b[x][y] = (byte) (values[index] & 0xFF);
                    index++;
                }
            ToIndexedColor(r, g, b);
        }
    }

    public GifEncoder(byte[][] r, byte[][] g, byte[][] b) throws AWTException {
        this.width_ = (short) r.length;
        this.height_ = (short) r[0].length;

        ToIndexedColor(r, g, b);
    }

    public void Write(OutputStream output) throws IOException {
        BitUtils.WriteString(output, "GIF87a");

        ScreenDescriptor sd = new ScreenDescriptor(this.width_, this.height_, this.numColors_);
        sd.Write(output);

        output.write(this.colors_, 0, this.colors_.length);

        ImageDescriptor id = new ImageDescriptor(this.width_, this.height_, ',');
        id.Write(output);

        byte codesize = BitUtils.BitsNeeded(this.numColors_);
        if (codesize == 1)
            codesize = (byte) (codesize + 1);
        output.write(codesize);

        LZWCompressor.LZWCompress(output, codesize, this.pixels_);
        output.write(0);

        id = new ImageDescriptor((short) 0, (short) 0, ';');
        id.Write(output);
        output.flush();
    }

    void ToIndexedColor(byte[][] r, byte[][] g, byte[][] b) throws AWTException {
        this.pixels_ = new byte[this.width_ * this.height_];
        this.colors_ = new byte[768];
        int colornum = 0;
        for (int x = 0; x < this.width_; x++) {
            for (int y = 0; y < this.height_; y++) {
                int search;
                for (search = 0; search < colornum; search++) {
                    if ((this.colors_[(search * 3)] == r[x][y]) && (this.colors_[(search * 3 + 1)] == g[x][y])
                            && (this.colors_[(search * 3 + 2)] == b[x][y]))
                        break;
                }
                if (search > 255) {
                    throw new AWTException("Too many colors.");
                }
                this.pixels_[(y * this.width_ + x)] = (byte) search;

                if (search == colornum) {
                    this.colors_[(search * 3)] = r[x][y];
                    this.colors_[(search * 3 + 1)] = g[x][y];
                    this.colors_[(search * 3 + 2)] = b[x][y];
                    colornum++;
                }
            }
        }
        this.numColors_ = (1 << BitUtils.BitsNeeded(colornum));
        byte[] copy = new byte[this.numColors_ * 3];
        System.arraycopy(this.colors_, 0, copy, 0, this.numColors_ * 3);
        this.colors_ = copy;
    }

}
