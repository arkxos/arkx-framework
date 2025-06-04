package org.ark.framework.jaf;

import com.arkxos.framework.cosyui.web.CookieData;
import com.arkxos.framework.cosyui.web.RequestData;
import com.arkxos.framework.cosyui.web.ResponseData;

/**   
 * @class org.ark.framework.jaf.IPage
 * @author Darkness
 * @date 2012-10-9 下午9:30:27 
 * @version V1.0   
 */

public interface IPage {

	void setRequest(RequestData dc);

	RequestData getRequest();

	CookieData getCookie();

	void setCookie(CookieData cookie);

	ResponseData getResponse();

	void setResponse(ResponseData response);

}
