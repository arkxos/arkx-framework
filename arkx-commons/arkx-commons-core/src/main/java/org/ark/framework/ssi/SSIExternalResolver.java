package org.ark.framework.ssi;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

/**
 * @class org.ark.framework.ssi.SSIExternalResolver
 * @author Darkness
 * @date 2013-1-31 下午12:34:52
 * @version V1.0
 */
public abstract interface SSIExternalResolver {

    public abstract void addVariableNames(Collection<String> paramCollection);

    public abstract String getVariableValue(String paramString);

    public abstract void setVariableValue(String paramString1, String paramString2);

    public abstract Date getCurrentDate();

    public abstract long getFileSize(String paramString, boolean paramBoolean) throws IOException;

    public abstract long getFileLastModified(String paramString, boolean paramBoolean) throws IOException;

    public abstract String getFileText(String paramString, boolean paramBoolean) throws IOException;

    public abstract void log(String paramString, Throwable paramThrowable);

}
