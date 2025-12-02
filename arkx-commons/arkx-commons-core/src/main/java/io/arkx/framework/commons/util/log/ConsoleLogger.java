package io.arkx.framework.commons.util.log;

import java.io.PrintStream;

import io.arkx.framework.commons.util.DateUtil;
import io.arkx.framework.commons.util.LogUtil;

/**
 */
public class ConsoleLogger implements ILogger {
    PrintStream out = null;

    public ConsoleLogger(boolean err) {
        if (err) {
            out = LogUtil.getSyserr();
        } else {
            out = System.out;
        }
    }

    private void println(String type, Object obj) {
        out.print(type);
        out.print(": ");
        out.print(DateUtil.getCurrentDate("yy-MM-dd HH:mm:ss"));
        out.print(' ');
        out.println(obj);
    }

    @Override
    public void trace(Object paramObject) {
        println("TRACE", paramObject);
    }

    @Override
    public void debug(Object paramObject) {
        println("DEBUG", paramObject);
    }

    @Override
    public void info(Object paramObject) {
        println("INFO", paramObject);
    }

    @Override
    public void warn(Object paramObject) {
        println("WARN", paramObject);
    }

    @Override
    public void error(Object paramObject) {
        println("ERROR", paramObject);
    }

    @Override
    public void fatal(Object paramObject) {
        println("FATAL", paramObject);
    }

}
