package com.rapidark.framework.ssi;

import javax.servlet.http.HttpServletRequest;

import com.rapidark.framework.commons.util.StringUtil;

public class SSIServletRequestUtil
{
  public static String getRelativePath(HttpServletRequest request)
  {
    if (request.getAttribute("javax.servlet.include.request_uri") != null)
    {
      String result = (String)request.getAttribute("javax.servlet.include.path_info");
      if (result == null) {
        result = (String)request.getAttribute("javax.servlet.include.servlet_path");
      }
      if ((result == null) || (result.equals(""))) {
        result = "/";
      }
      return result;
    }
    String result1 = request.getServletPath();
    String result2 = request.getPathInfo();
    String result = null;
    if (StringUtil.isEmpty(result1)) {
      result = result2;
    } else if (StringUtil.isEmpty(result2)) {
      result = result1;
    } else {
      result = result1.length() > result2.length() ? result1 : result2;
    }
    if ((result == null) || (result.equals(""))) {
      result = "/";
    }
    return RequestUtil.normalize(result);
  }
  
  /**
   * @deprecated
   */
  public static String normalize(String path)
  {
    return RequestUtil.normalize(path);
  }
}
