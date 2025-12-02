package org.ark.framework.ssi;

import java.io.ByteArrayOutputStream;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

/**
 * @class org.ark.framework.ssi.ByteArrayServletOutputStream
 *
 * @author Darkness
 * @date 2013-1-31 下午12:26:49
 * @version V1.0
 */
public class ByteArrayServletOutputStream extends ServletOutputStream {
    protected ByteArrayOutputStream buf = null;

    public ByteArrayServletOutputStream() {
        this.buf = new ByteArrayOutputStream();
    }

    public byte[] toByteArray() {
        return this.buf.toByteArray();
    }

    public void write(int b) {
        this.buf.write(b);
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        // TODO Auto-generated method stub

    }
}
