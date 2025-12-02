package org.ark.framework.security;

import java.util.ArrayList;

import io.arkx.framework.cosyui.web.RequestData;
import io.arkx.framework.cosyui.web.ResponseData;

/**
 * @class org.ark.framework.security.VerifyRuleList
 *
 * @author Darkness
 * @date 2013-1-31 下午12:26:08
 * @version V1.0
 */
public class VerifyRuleList {
    private ArrayList<String[]> array = new ArrayList<String[]>();
    private String Message;
    private RequestData Request;
    private ResponseData Response;

    public void add(String fieldID, String fieldName, String rule) {
        this.array.add(new String[]{fieldID, fieldName, rule});
    }

    public boolean doVerify() {
        VerifyRule rule = new VerifyRule();
        boolean flag = true;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.array.size(); i++) {
            String[] f = (String[]) this.array.get(i);
            rule.setRule(f[2]);
            if (!rule.verify(this.Request.getString(f[0]))) {
                sb.append(rule.getMessages(f[1]));
                flag = false;
            }
        }
        if (!flag) {
            this.Message = sb.toString();
            this.Response.setFailedMessage(sb.toString());
        }
        return flag;
    }

    public String getMessage() {
        return this.Message;
    }

    public RequestData getRequest() {
        return this.Request;
    }

    public void setRequest(RequestData request) {
        this.Request = request;
    }

    public ResponseData getResponse() {
        return this.Response;
    }

    public void setResponse(ResponseData response) {
        this.Response = response;
    }
}
