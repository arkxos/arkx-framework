package io.arkx.framework.ssi;

import java.io.PrintWriter;

public abstract interface SSICommand
{
  public abstract long process(SSIMediator paramSSIMediator, String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, PrintWriter paramPrintWriter)
    throws SSIStopProcessingException;
}
