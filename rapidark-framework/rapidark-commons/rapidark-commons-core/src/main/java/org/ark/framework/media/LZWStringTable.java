package org.ark.framework.media;

class LZWStringTable {
	private static final int RES_CODES = 2;
	private static final short HASH_FREE = -1;
	private static final short NEXT_FIRST = -1;
	private static final int MAXBITS = 12;
	private static final int MAXSTR = 4096;
	private static final short HASHSIZE = 9973;
	private static final short HASHSTEP = 2039;
	byte[] strChr_;
	short[] strNxt_;
	short[] strHsh_;
	short numStrings_;

	public LZWStringTable() {
		this.strChr_ = new byte[4096];
		this.strNxt_ = new short[4096];
		this.strHsh_ = new short[9973];
	}

	public int AddCharString(short index, byte b) {
		if (this.numStrings_ >= 4096) {
			return 65535;
		}
		int hshidx = Hash(index, b);
		while (this.strHsh_[hshidx] != -1) {
			hshidx = (hshidx + 2039) % 9973;
		}
		this.strHsh_[hshidx] = this.numStrings_;
		this.strChr_[this.numStrings_] = b;
		this.strNxt_[this.numStrings_] = (index != -1 ? index : -1);

		return this.numStrings_++;
	}

	public short FindCharString(short index, byte b) {
		if (index == -1) {
			return b;
		}
		int hshidx = Hash(index, b);
		int nxtidx;
		while ((nxtidx = this.strHsh_[hshidx]) != -1) {
			if ((this.strNxt_[nxtidx] == index) && (this.strChr_[nxtidx] == b))
				return (short) nxtidx;
			hshidx = (hshidx + 2039) % 9973;
		}

		return -1;
	}

	public void ClearTable(int codesize) {
		this.numStrings_ = 0;

		for (int q = 0; q < 9973; q++) {
			this.strHsh_[q] = -1;
		}

		int w = (1 << codesize) + 2;
		for (int q = 0; q < w; q++)
			AddCharString((short)-1, (byte) q);
	}

	public static int Hash(short index, byte lastbyte) {
		return (((short) (lastbyte << 8) ^ index) & 0xFFFF) % 9973;
	}
}