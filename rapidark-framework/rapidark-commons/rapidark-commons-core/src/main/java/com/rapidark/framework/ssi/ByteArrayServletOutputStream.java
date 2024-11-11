package com.rapidark.framework.ssi;

import java.io.ByteArrayOutputStream;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

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
	}
}
