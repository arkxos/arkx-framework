package com.rapidark.framework.script;

public class EvalException
  extends Exception
{
  private static final long serialVersionUID = 1L;
  private String message;
  private String originMessage;
  private String lineSource;
  private int rowNo;
  private int colNo;
  
  protected EvalException(String message, String originMessage, String lineSource, int rowNo, int colNo)
  {
    super(message);
    this.message = message;
    this.originMessage = originMessage;
    this.rowNo = rowNo;
    this.colNo = colNo;
    this.lineSource = lineSource;
  }
  
  public String toString()
  {
    return this.message;
  }
  
  public int getColNo()
  {
    return this.colNo;
  }
  
  public String getMessage()
  {
    return this.message;
  }
  
  public String getOriginMessage()
  {
    return this.originMessage;
  }
  
  public int getRowNo()
  {
    return this.rowNo;
  }
  
  public String getLineSource()
  {
    return this.lineSource;
  }
}
