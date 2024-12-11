package org.niiish32x.sugarsms.app.service.impl;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.niiish32x.sugarsms.app.cache.UserPhoneCache;
import org.niiish32x.sugarsms.app.dto.AlertInfoDTO;
import org.niiish32x.sugarsms.app.dto.PersonCodesDTO;
import org.niiish32x.sugarsms.app.dto.PersonDTO;
import org.niiish32x.sugarsms.app.dto.SuposUserDTO;
import org.niiish32x.sugarsms.app.external.ZubrixSmsRequest;
import org.niiish32x.sugarsms.app.external.ZubrixSmsResponse;
import org.niiish32x.sugarsms.app.proxy.ZubrixSmsProxy;
import org.niiish32x.sugarsms.app.service.AlertService;
import org.niiish32x.sugarsms.app.service.PersonService;
import org.niiish32x.sugarsms.app.service.SendMessageService;
import org.niiish32x.sugarsms.app.service.UserService;
import org.niiish32x.sugarsms.common.supos.result.Result;
import org.niiish32x.sugarsms.common.supos.result.ResultCodeEnum;
import org.niiish32x.sugarsms.common.supos.utils.Retrys;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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
    UserPhoneCache userPhoneCache;

    @Resource
    ZubrixSmsProxy zubrixSmsProxy;

    @Resource
    UserService userService;

    @Resource
    PersonService personService;


    @Override
    public Result <ZubrixSmsResponse >sendOneZubrixSms(String number, String text) {
        ZubrixSmsRequest smsRequest = zubrixSmsProxy.buildRequest(number, text);
        String url = zubrixSmsProxy.buildUrl(smsRequest);
        ZubrixSmsResponse messageResponse = zubrixSmsProxy.send(url);
        return messageResponse.getErrorCode() == 0 ? Result.build(messageResponse,ResultCodeEnum.SUCCESS) : Result.build(messageResponse,ResultCodeEnum.FAIL);
    }
}
