package com.arkxos.framework.commons.util;

import org.apache.commons.logging.Log;

import com.arkxos.framework.commons.util.log.ILogger;

public class Log4jLogger
  implements ILogger
{
  Log log = null;
  
  public Log4jLogger(Log log)
  {
    this.log = log;
  }
  
  public void trace(Object paramObject)
  {
    this.log.trace(paramObject);
  }
  
  public void debug(Object paramObject)
  {
    this.log.debug(paramObject);
  }
  
  public void info(Object paramObject)
  {
    this.log.info(paramObject);
  }
  
  public void warn(Object paramObject)
  {
    this.log.warn(paramObject);
  }
  
  public void error(Object paramObject)
  {
    this.log.error(paramObject);
  }
  
  public void fatal(Object paramObject)
  {
    this.log.fatal(paramObject);
  }
}
