package org.ark.framework.media;

import java.io.IOException;
import java.io.OutputStream;

class ScreenDescriptor {

	public short localScreenWidth_;

	public short localScreenHeight_;

	private byte byte_;

	public byte backgroundColorIndex_;

	public byte pixelAspectRatio_;

	public ScreenDescriptor(short width, short height, int numColors) {
		this.localScreenWidth_ = width;
		this.localScreenHeight_ = height;
		SetGlobalColorTableSize((byte) (BitUtils.BitsNeeded(numColors) - 1));
		SetGlobalColorTableFlag((byte) 1);
		SetSortFlag((byte) 0);
		SetColorResolution((byte) 7);
		this.backgroundColorIndex_ = 0;
		this.pixelAspectRatio_ = 0;
	}

	public void Write(OutputStream output) throws IOException {
		BitUtils.WriteWord(output, this.localScreenWidth_);
		BitUtils.WriteWord(output, this.localScreenHeight_);
		output.write(this.byte_);
		output.write(this.backgroundColorIndex_);
		output.write(this.pixelAspectRatio_);
	}

	public void SetGlobalColorTableSize(byte num) {
		this.byte_ = (byte) (this.byte_ | num & 0x7);
	}

	public void SetSortFlag(byte num) {
		this.byte_ = (byte) (this.byte_ | (num & 0x1) << 3);
	}

	public void SetColorResolution(byte num) {
		this.byte_ = (byte) (this.byte_ | (num & 0x7) << 4);
	}

	public void SetGlobalColorTableFlag(byte num) {
		this.byte_ = (byte) (this.byte_ | (num & 0x1) << 7);
	}

}
