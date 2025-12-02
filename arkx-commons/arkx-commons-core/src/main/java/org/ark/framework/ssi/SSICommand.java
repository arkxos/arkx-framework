package org.ark.framework.ssi;

import java.io.PrintWriter;

/**
 * @class org.ark.framework.ssi.SSICommand
 * @author Darkness
 * @date 2013-1-31 下午12:33:42
 * @version V1.0
 */
public interface SSICommand {

	long process(SSIMediator paramSSIMediator, String paramString, String[] paramArrayOfString1,
			String[] paramArrayOfString2, PrintWriter paramPrintWriter) throws SSIStopProcessingException;

}
