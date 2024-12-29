package com.rapidark.framework.media;

import java.io.IOException;
import java.io.OutputStream;

class BitFile
{
  OutputStream output_;
  byte[] buffer_;
  int index_;
  int bitsLeft_;
  
  public BitFile(OutputStream output)
  {
    this.output_ = output;
    this.buffer_ = new byte['Ā'];
    this.index_ = 0;
    this.bitsLeft_ = 8;
  }
  
  public void Flush()
    throws IOException
  {
    int numBytes = this.index_ + (this.bitsLeft_ == 8 ? 0 : 1);
    if (numBytes > 0)
    {
      this.output_.write(numBytes);
      this.output_.write(this.buffer_, 0, numBytes);
      this.buffer_[0] = 0;
      this.index_ = 0;
      this.bitsLeft_ = 8;
    }
  }
  
  public void WriteBits(int bits, int numbits)
    throws IOException
  {
    int numBytes = 255;
    do
    {
      if (((this.index_ == 254) && (this.bitsLeft_ == 0)) || (this.index_ > 254))
      {
        this.output_.write(numBytes);
        this.output_.write(this.buffer_, 0, numBytes);
        
        this.buffer_[0] = 0;
        this.index_ = 0;
        this.bitsLeft_ = 8;
      }
      if (numbits <= this.bitsLeft_)
      {
        int tmp86_83 = this.index_; byte[] tmp86_79 = this.buffer_;tmp86_79[tmp86_83] = ((byte)(tmp86_79[tmp86_83] | (bits & (1 << numbits) - 1) << 8 - this.bitsLeft_));
        
        this.bitsLeft_ -= numbits;
        numbits = 0;
      }
      else
      {
        int tmp129_126 = this.index_; byte[] tmp129_122 = this.buffer_;tmp129_122[tmp129_126] = ((byte)(tmp129_122[tmp129_126] | (bits & (1 << this.bitsLeft_) - 1) << 8 - this.bitsLeft_));
        
        bits >>= this.bitsLeft_;
        numbits -= this.bitsLeft_;
        this.buffer_[(++this.index_)] = 0;
        this.bitsLeft_ = 8;
      }
    } while (numbits != 0);
  }
}
