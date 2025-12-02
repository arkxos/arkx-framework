package org.ark.framework.ssi;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * @class org.ark.framework.ssi.SSIFlastmod
 *
 * @author Darkness
 * @date 2013-1-31 下午12:35:13
 * @version V1.0
 */
public final class SSIFlastmod implements SSICommand {
    public long process(SSIMediator ssiMediator, String commandName, String[] paramNames, String[] paramValues,
            PrintWriter writer) {
        long lastModified = 0L;
        String configErrMsg = ssiMediator.getConfigErrMsg();
        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames[i];
            String paramValue = paramValues[i];
            String substitutedValue = ssiMediator.substituteVariables(paramValue);
            try {
                if ((paramName.equalsIgnoreCase("file")) || (paramName.equalsIgnoreCase("virtual"))) {
                    boolean virtual = paramName.equalsIgnoreCase("virtual");
                    lastModified = ssiMediator.getFileLastModified(substitutedValue, virtual);
                    Date date = new Date(lastModified);
                    String configTimeFmt = ssiMediator.getConfigTimeFmt();
                    writer.write(formatDate(date, configTimeFmt));
                } else {
                    ssiMediator.log("#flastmod--Invalid attribute: " + paramName);
                    writer.write(configErrMsg);
                }
            } catch (IOException e) {
                ssiMediator.log("#flastmod--Couldn't get last modified for file: " + substitutedValue, e);
                writer.write(configErrMsg);
            }
        }
        return lastModified;
    }

    protected String formatDate(Date date, String configTimeFmt) {
        Strftime strftime = new Strftime(configTimeFmt, DateTool.LOCALE_US);
        return strftime.format(date);
    }
}
