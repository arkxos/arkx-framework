package org.ark.framework.ssi;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

/**
 * @class org.ark.framework.ssi.SSIPrintenv
 *
 * @author Darkness
 * @date 2013-1-31 下午12:35:55
 * @version V1.0
 */
public class SSIPrintenv implements SSICommand {
    public long process(SSIMediator ssiMediator, String commandName, String[] paramNames, String[] paramValues,
            PrintWriter writer) {
        long lastModified = 0L;

        if (paramNames.length > 0) {
            String errorMessage = ssiMediator.getConfigErrMsg();
            writer.write(errorMessage);
        } else {
            Collection variableNames = ssiMediator.getVariableNames();
            Iterator iter = variableNames.iterator();
            while (iter.hasNext()) {
                String variableName = (String) iter.next();
                String variableValue = ssiMediator.getVariableValue(variableName);

                if (variableValue == null) {
                    variableValue = "(none)";
                }
                writer.write(variableName);
                writer.write(61);
                writer.write(variableValue);
                writer.write(10);
                lastModified = System.currentTimeMillis();
            }
        }
        return lastModified;
    }
}
