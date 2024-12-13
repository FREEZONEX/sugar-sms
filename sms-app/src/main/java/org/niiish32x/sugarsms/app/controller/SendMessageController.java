package org.niiish32x.sugarsms.app.controller;

import org.niiish32x.sugarsms.app.service.SendMessageService;
import org.niiish32x.sugarsms.common.result.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * SendMessageController
 *
 * @author shenghao ni
 * @date 2024.12.09 9:43
 */

@RestController
public class SendMessageController {
    @Resource
    SendMessageService sendMessageService;

    @RequestMapping("/send/sms/test")
    public Result sendSmsTest(){
        return sendMessageService.sendOneZubrixSmsMessage("919747934655","xx");
    }
}
