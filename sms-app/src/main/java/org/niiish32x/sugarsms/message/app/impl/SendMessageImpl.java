package org.niiish32x.sugarsms.message.app.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.message.app.external.ZubrixSmsResponse;
import org.niiish32x.sugarsms.app.proxy.EmailSenderProxy;
import org.niiish32x.sugarsms.app.proxy.ZubrixSmsProxy;
import org.niiish32x.sugarsms.message.app.SendMessageService;
import org.niiish32x.sugarsms.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * SendMessageImpl
 *
 * @author shenghao ni
 * @date 2024.12.09 9:42
 */

@Service
@Slf4j
public class SendMessageImpl implements SendMessageService {


    @Resource
    ZubrixSmsProxy zubrixSmsProxy;


    @Autowired
    EmailSenderProxy emailSenderProxy;

    private final RateLimiter rateLimiter = RateLimiter.create(10);

    @Override
    public Result <ZubrixSmsResponse > sendOneZubrixSmsMessage(String number, String text) {
        ZubrixSmsResponse messageResponse = zubrixSmsProxy.send(number,text);
        return messageResponse.getErrorCode() == 0 ? Result.success(messageResponse) : Result.error(JSON.toJSONString(messageResponse));
    }



    @Override
    public boolean sendEmail() {
        return emailSenderProxy.sendTextEmail(EmailSenderProxy.PUBLIC_EMAIL_SUGAR,"1159417019@qq.com","test","测试");
    }

    @Override
    public boolean sendEmail(String toMail, String subject, String text) {
        if (rateLimiter.tryAcquire()) {
            return emailSenderProxy.sendTextEmail(EmailSenderProxy.PUBLIC_EMAIL_SUGAR,toMail,subject,text);
        }else {
            return false;
        }

    }


}
