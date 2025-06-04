package io.arkx.framework.security;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;

import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.jce.provider.BrokenJCEBlockCipher;

public class Z3DESCipher extends BrokenJCEBlockCipher
{
  public Z3DESCipher()
  {
    super(new DESedeEngine());
  }
  
  public void init(int mode, Key key)
  {
    try
    {
      engineInit(mode, key, new SecureRandom());
    }
    catch (InvalidKeyException e)
    {
      e.printStackTrace();
    }
  }
  
  public byte[] doFinal(byte[] str)
    throws Exception
  {
    return engineDoFinal(str, 0, str.length);
  }
}
