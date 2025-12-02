package org.ark.framework.media;

import java.io.IOException;
import java.io.OutputStream;

class ImageDescriptor {
    public byte separator_;
    public short leftPosition_;
    public short topPosition_;
    public short width_;
    public short height_;
    private byte byte_;

    public ImageDescriptor(short width, short height, char separator) {
        this.separator_ = (byte) separator;
        this.leftPosition_ = 0;
        this.topPosition_ = 0;
        this.width_ = width;
        this.height_ = height;
        SetLocalColorTableSize((byte) 0);
        SetReserved((byte) 0);
        SetSortFlag((byte) 0);
        SetInterlaceFlag((byte) 0);
        SetLocalColorTableFlag((byte) 0);
    }

    public void Write(OutputStream output) throws IOException {
        output.write(this.separator_);
        BitUtils.WriteWord(output, this.leftPosition_);
        BitUtils.WriteWord(output, this.topPosition_);
        BitUtils.WriteWord(output, this.width_);
        BitUtils.WriteWord(output, this.height_);
        output.write(this.byte_);
    }

    public void SetLocalColorTableSize(byte num) {
        this.byte_ = (byte) (this.byte_ | num & 0x7);
    }

    public void SetReserved(byte num) {
        this.byte_ = (byte) (this.byte_ | (num & 0x3) << 3);
    }

    public void SetSortFlag(byte num) {
        this.byte_ = (byte) (this.byte_ | (num & 0x1) << 5);
    }

    public void SetInterlaceFlag(byte num) {
        this.byte_ = (byte) (this.byte_ | (num & 0x1) << 6);
    }

    public void SetLocalColorTableFlag(byte num) {
        this.byte_ = (byte) (this.byte_ | (num & 0x1) << 7);
    }
}
