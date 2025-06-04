package io.arkx.framework.ssi;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

public class SSIPrintenv
  implements SSICommand
{
  public long process(SSIMediator ssiMediator, String commandName, String[] paramNames, String[] paramValues, PrintWriter writer)
  {
    long lastModified = 0L;
    if (paramNames.length > 0)
    {
      String errorMessage = ssiMediator.getConfigErrMsg();
      writer.write(errorMessage);
    }
    else
    {
      Collection<String> variableNames = ssiMediator.getVariableNames();
      Iterator<String> iter = variableNames.iterator();
      while (iter.hasNext())
      {
        String variableName = (String)iter.next();
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
