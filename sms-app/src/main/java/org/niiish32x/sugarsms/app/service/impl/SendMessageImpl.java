package org.niiish32x.sugarsms.app.service.impl;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.app.external.ZubrixSmsResponse;
import org.niiish32x.sugarsms.app.proxy.EmailSenderProxy;
import org.niiish32x.sugarsms.app.proxy.ZubrixSmsProxy;
import org.niiish32x.sugarsms.app.service.SendMessageService;
import org.niiish32x.sugarsms.common.result.Result;
import org.niiish32x.sugarsms.common.result.ResultCode;
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


    @Override
    public Result <ZubrixSmsResponse > sendOneZubrixSmsMessage(String number, String text) {
        ZubrixSmsResponse messageResponse = zubrixSmsProxy.send(number,text);
        return messageResponse.getErrorCode() == 0 ? Result.success(messageResponse) : Result.error(JSON.toJSONString(messageResponse));
    }



    @Override
    public void sendEmail() {
        EmailSenderProxy.sendTextEmail(EmailSenderProxy.PUBLIC_EMAIL_SUGAR,"niiish32x@gmail.com","test","测试");
    }

    @Override
    public void sendEmail(String toMail, String subject, String text) {
        EmailSenderProxy.sendTextEmail(EmailSenderProxy.PUBLIC_EMAIL_SUGAR,toMail,subject,text);
    }


}
