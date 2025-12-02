package org.ark.framework.ssi;

import java.io.PrintWriter;

/**
 * @class org.ark.framework.ssi.SSISet
 *
 * @author Darkness
 * @date 2013-1-31 下午12:36:39
 * @version V1.0
 */
public class SSISet implements SSICommand {
    public long process(SSIMediator ssiMediator, String commandName, String[] paramNames, String[] paramValues,
            PrintWriter writer) throws SSIStopProcessingException {
        long lastModified = 0L;
        String errorMessage = ssiMediator.getConfigErrMsg();
        String variableName = null;
        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames[i];
            String paramValue = paramValues[i];
            if (paramName.equalsIgnoreCase("var")) {
                variableName = paramValue;
            } else if (paramName.equalsIgnoreCase("value")) {
                if (variableName != null) {
                    String substitutedValue = ssiMediator.substituteVariables(paramValue);
                    ssiMediator.setVariableValue(variableName, substitutedValue);
                    lastModified = System.currentTimeMillis();
                } else {
                    ssiMediator.log("#set--no variable specified");
                    writer.write(errorMessage);
                    throw new SSIStopProcessingException();
                }
            } else {
                ssiMediator.log("#set--Invalid attribute: " + paramName);
                writer.write(errorMessage);
                throw new SSIStopProcessingException();
            }
        }
        return lastModified;
    }
}
