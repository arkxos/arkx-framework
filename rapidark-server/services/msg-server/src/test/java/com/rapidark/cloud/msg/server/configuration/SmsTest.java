package com.rapidark.cloud.msg.server.configuration;

import com.rapidark.common.test.BaseTest;
import com.rapidark.cloud.msg.client.model.SmsMessage;
import com.rapidark.cloud.msg.server.dispatcher.MessageDispatcher;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: liuyadu
 * @date: 2018/11/27 14:45
 * @description:
 */
public class SmsTest extends BaseTest {
    @Autowired
    private MessageDispatcher dispatcher;

    @Test
    public void testSms() {
        SmsMessage smsNotify = new SmsMessage();
        smsNotify.setPhoneNum("18510152531");
        smsNotify.setSignName("测试");
        smsNotify.setTplCode("测试内容");
        this.dispatcher.dispatch(smsNotify);
        try {
            Thread.sleep(50000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
